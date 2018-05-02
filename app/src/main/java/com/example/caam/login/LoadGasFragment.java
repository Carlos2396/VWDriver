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
import java.text.SimpleDateFormat;
import java.util.Calendar;


import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ernesto on 30-Apr-18.
 */

public class LoadGasFragment extends Fragment {
    private String TAG = LoadGasFragment.class.getSimpleName();

    TextView lastLoadTime;
    EditText numGas;
    Button gasButton;

    String currPlatesName;
    int currId;
    String lastRefill;
    int refillNum;
    JSONArray globalRefills;
    String currDateTime;



    private static String url = "https://fake-backend-mobile-app.herokuapp.com/crafters/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gasoline, container, false);

        lastLoadTime = (TextView) view.findViewById(R.id.lastLoadTime);
        numGas = (EditText) view.findViewById(R.id.numGas);
        gasButton = (Button) view.findViewById(R.id.changeBatteryButton);

        Authentication auth = new Authentication(getActivity());
        //currPlatesName = auth.getCrafter();


        gasButton.setOnClickListener(new loadGasListener());
        new GetInformation().execute();

        return view;
    }
    private class loadGasListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.changeBatteryButton:
                    refillNum = Integer.parseInt(numGas.getText().toString());
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    currDateTime = df.format(Calendar.getInstance().getTime());
                    new CreateRefillManager().execute(String.format("%s/crafters/%d", Authentication.SERVER, currId));

                    //new addPassengerCrafter().execute();
                    break;

            }
        }
    }

    private class CreateRefillManager extends AsyncTask<String, Void, String> {

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
                if(responseCode == HttpURLConnection.HTTP_CREATED){
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

        protected String getPostJson() {
            try{
           /* HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray crafters = new JSONArray(jsonStr);


                    JSONObject c = crafters.getJSONObject((currId));*/
                    JSONObject refill = new JSONObject();
                    refill.put("type", "Magna");
                    refill.put("datetime", currDateTime);
                    refill.put("liters", refillNum);
                    globalRefills.put(refill);
                    JSONObject finalRefill = new JSONObject();
                    finalRefill.put("fuel_reffils", globalRefills);


                    return finalRefill.toString();
                } catch (JSONException je) {
                    je.printStackTrace();
                    return null;
                }
            }


        @Override
        protected void onPostExecute(String result) {
            try{
                JSONObject alert = new JSONObject(result);
                if(alert.has("type")){
                    //message.setText("");
                    Toast.makeText(getActivity(), "Falló envío de refill, intente de nuevo ", Toast.LENGTH_SHORT).show();

                    //((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).ALERTSFRAGMENT);
                }
                else{
                    throw new JSONException("");
                }
            }
            catch (JSONException je){
                Toast.makeText(getActivity(), "Refill enviada exitosamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class GetInformation extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray crafters = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < crafters.length(); i++) {
                        JSONObject c = crafters.getJSONObject(i);

                        String plates = c.getString("plates");
                        JSONArray refills = c.getJSONArray("fuel_reffils");
                        globalRefills = c.getJSONArray("fuel_reffils");


                        if (plates.equals(currPlatesName)){
                            currId = c.getInt("id");
                            JSONObject gas = refills.getJSONObject(refills.length()-1);
                            lastRefill = gas.getString("datetime");
                            break;
                        }


                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lastLoadTime.setText(lastRefill);
        }

    }
}
