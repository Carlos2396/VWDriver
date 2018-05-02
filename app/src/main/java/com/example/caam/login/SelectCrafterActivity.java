package com.example.caam.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectCrafterActivity extends AppCompatActivity {

    private static final String TAG = "RouteSelectCrafter";

    Authentication auth;
    Button selectButton;
    Button abandonButton;
    Spinner spinner;
    ArrayList<String> craftersList;
    int selectedCrafterPlateIndex;
    HashMap<String, Integer> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_crafter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        selectButton = (Button) findViewById(R.id.select);
        abandonButton = (Button) findViewById(R.id.abandon);

        auth = new Authentication(getBaseContext());

        selectedCrafterPlateIndex = -1;
        spinner = (Spinner) findViewById(R.id.spinner);
        loadCrafters();

        if(auth.getCrafter() == 0){
            abandonButton.setVisibility(View.GONE);
        }
    }

    public void loadCrafters(){
        new SelectCrafterActivity.CrafterManager().execute(String.format("%s/crafters", Authentication.SERVER));
    }

    public void fillSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, craftersList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(selectedCrafterPlateIndex >= 0){
            spinner.setSelection(selectedCrafterPlateIndex);
        }
    }

    public void displayToast(String message){
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void selectCrafter(View v) {
        if(auth.setCrafter(map.get(spinner.getSelectedItem().toString()))){
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
        else{
            Toast.makeText(this, "Intente de nuevo.", Toast.LENGTH_SHORT).show();
        }
    }

    public void abandonCrafter(View v) {
        auth.removeCrafter();
        if(auth.getCrafter() == 0){
            abandonButton.setVisibility(View.GONE);
            displayToast("Crafter abandoned.");
        }
    }

    private class CrafterManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            try{
                URL url = new URL(params[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader((new InputStreamReader(connection.getInputStream())));
                    while((line = br.readLine()) != null){
                        response.append(line);
                    }
                }
                else {
                    Log.d("RouteSelectCrafterActivity", responseCode + "");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray crafters = new JSONArray(result);
                JSONObject crafter;
                map = new HashMap<String, Integer>();
                craftersList = new ArrayList<String>();

                for(int i=0; i<crafters.length(); i++){
                    crafter = crafters.getJSONObject(i);
                    map.put(crafter.getString("plates"), crafter.getInt("id"));

                    if(crafter.getInt("id") == auth.getCrafter()){
                        selectedCrafterPlateIndex = i;
                    }
                    craftersList.add(crafter.getString("plates"));
                }

                fillSpinner();
            }
            catch(JSONException je) {
                je.printStackTrace();
                displayToast("Ha ocurrido un error. Intente de nuevo en unos minutos.");
            }
        }
    }
}
