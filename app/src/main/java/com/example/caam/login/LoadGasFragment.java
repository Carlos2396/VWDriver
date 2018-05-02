package com.example.caam.login;

import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ernesto on 30-Apr-18.
 */

public class LoadGasFragment extends Fragment {
    private String TAG = LoadGasFragment.class.getSimpleName();

    TextView lastLoadTxt;
    EditText gasLittersTxt;
    Button loadGasBtn;
    Button returnBtn;

    JSONArray loads;
    String lastRefill;
    int liters;
    int crafterId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gasoline, container, false);

        lastLoadTxt = (TextView) view.findViewById(R.id.lastRefillTxt);
        gasLittersTxt = (EditText) view.findViewById(R.id.gasLittersTxt);
        loadGasBtn = (Button) view.findViewById(R.id.loadGasBtn);
        loadGasBtn.setOnClickListener(new loadGasListener());
        returnBtn = (Button) view.findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new ReturnListener());


        Log.d(TAG, "OnCreate");
        crafterId = ((MainActivity)getActivity()).maintenanceCrafterId;
        loadGasBtn.setEnabled(false);
        new GetCrafterManager().execute(String.format("%s/crafters/%d", Authentication.SERVER, crafterId));

        return view;
    }

    private class loadGasListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(gasLittersTxt.getText().toString().length() > 0){
                try{
                    liters = Integer.parseInt(gasLittersTxt.getText().toString());
                    new AddFuelRefillManager().execute(String.format("%s/crafters/%d", Authentication.SERVER, crafterId));
                }
                catch (NumberFormatException nfe){
                    gasLittersTxt.setText("");
                    Toast.makeText(getActivity(), "Número invalido. Debe ser entero.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class ReturnListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).MAINTENANCEFRAGMENT);
        }
    }

    private class AddFuelRefillManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            try{
                URL url = new URL(params[0]);

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("PATCH");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostJson());
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader((new InputStreamReader(connection.getInputStream())));
                    while((line = br.readLine()) != null){
                        response.append(line);
                    }
                }
                else {
                    Log.d(TAG, responseCode + "");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            Log.d(TAG, response.toString());

            return response.toString();
        }

        protected String getPostJson(){
            try{
                Date now = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                JSONObject crafter = new JSONObject();
                JSONObject refill = new JSONObject();
                refill.put("type", "Premium");
                refill.put("datetime", df.format(now));
                refill.put("liters", liters);

                loads.put(refill);
                crafter.put("fuel_reffils", loads);
                return crafter.toString();
            }
            catch (JSONException je){
                je.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONObject crafter = new JSONObject(result);
                if(crafter.has("id")){
                    gasLittersTxt.setText("");
                    JSONArray refills = crafter.getJSONArray("fuel_reffils");
                    JSONObject refill = refills.getJSONObject(refills.length()-1);
                    lastLoadTxt.setText(refill.getString("datetime"));

                    Toast.makeText(getActivity(), "Carga registrada.", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).MAINTENANCEFRAGMENT);
                }
                else{
                    throw new JSONException("");
                }
            }
            catch (JSONException je){
                Toast.makeText(getActivity(), "No se registró la carga. Intente de nuevo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetCrafterManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            try{
                URL url = new URL(params[0]);

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
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

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            try {
                JSONObject crafter = new JSONObject(result);
                loads = crafter.getJSONArray("fuel_reffils");
                lastRefill = loads.getJSONObject(loads.length()-1).getString("datetime");
                lastLoadTxt.setText(lastRefill);
                loadGasBtn.setEnabled(true);
            }
            catch(JSONException je) {
                je.printStackTrace();
            }
        }
    }
}
