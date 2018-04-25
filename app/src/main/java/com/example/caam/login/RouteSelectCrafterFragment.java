package com.example.caam.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RouteSelectCrafterFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "RouteSelectCrafter";

    Authentication auth;
    Button selectButton;
    Button abandonButton;
    Spinner spinner;
    ArrayList<String> craftersList;
    int selectedCrafterPlateIndex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) throws AndroidRuntimeException {
        View view = inflater.inflate(R.layout.fragment_route_select_crafter, container, false);

        selectButton = (Button)view.findViewById(R.id.select);
        selectButton.setOnClickListener(new SelectListener());

        abandonButton = (Button)view.findViewById(R.id.abandon);
        abandonButton.setOnClickListener(new AbandonListener());

        auth = new Authentication(getActivity());

        selectedCrafterPlateIndex = -1;
        spinner = (Spinner)view.findViewById(R.id.spinner);
        loadCrafters();

        if(auth.getCrafter() == null){
            abandonButton.setVisibility(View.GONE);
        }

        return view;
    }

    public void loadCrafters(){
        new RouteSelectCrafterFragment.CrafterManager().execute(String.format("%s/crafters", Authentication.SERVER));
    }

    public void fillSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, craftersList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(selectedCrafterPlateIndex >= 0){
            spinner.setSelection(selectedCrafterPlateIndex);
        }
    }

    public void displayToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private class SelectListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            auth.setCrafter(spinner.getSelectedItem().toString());
            ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).ROUTEFRAGMENT);
        }
    }

    private class AbandonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            auth.removeCrafter();
            if(auth.getCrafter() == null){
                abandonButton.setVisibility(View.GONE);
                displayToast("Crafter abandoned.");
            }
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
                craftersList = new ArrayList<String>();
                String plate;

                for(int i=0; i<crafters.length(); i++){
                    plate = crafters.getJSONObject(i).getString("plates");
                    if(plate.equals(auth.getCrafter())){
                        selectedCrafterPlateIndex = i;
                    }
                    craftersList.add(plate);
                }

                fillSpinner();
            }
            catch(JSONException je) {
                je.printStackTrace();
                Toast.makeText(getActivity(), "Ha ocurrido un error. Intente de nuevo en unos minutos.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
