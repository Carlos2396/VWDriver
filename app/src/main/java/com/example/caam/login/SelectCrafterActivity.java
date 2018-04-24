package com.example.caam.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SelectCrafterActivity extends AppCompatActivity {
    Spinner spinner;
    ArrayList<String> craftersList;
    Authentication auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_crafter);

        auth = new Authentication(this.getBaseContext());
        spinner = (Spinner)findViewById(R.id.spinner);
        loadCrafters();
    }

    public void loadCrafters(){
        new CrafterManager().execute(String.format("%s/crafters", Authentication.SERVER));
    }

    public void fillSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, craftersList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void selectCrafter(View v){
        auth.setCrafter(spinner.getSelectedItem().toString());
        Intent i = new Intent(getApplicationContext(), RouteActivity.class);
        startActivity(i);
        finish();
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
                    System.out.println(responseCode);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            Log.d("Request result", response.toString());
            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray crafters = new JSONArray(result);
                craftersList = new ArrayList<String>();

                for(int i=0; i<crafters.length(); i++){
                    craftersList.add(crafters.getJSONObject(i).getString("plates"));
                }

                fillSpinner();
            }
            catch(JSONException je) {
                je.printStackTrace();
                Toast.makeText(getBaseContext(), "Ha ocurrido un error. Intente de nuevo en unos minutos.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
