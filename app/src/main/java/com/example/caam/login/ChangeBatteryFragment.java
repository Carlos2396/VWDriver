package com.example.caam.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class ChangeBatteryFragment extends Fragment{

    private String TAG = ChangeBatteryFragment.class.getSimpleName();

    TextView lastBatteryChangeTime;
    Button changeBatteryButton;

    String currPlatesName;
    int currId;
    String lastBattery;
    int refillNum;
    JSONArray globalBatteries;
    String currDateTime;



    private static String url = "https://fake-backend-mobile-app.herokuapp.com/crafters/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_battery, container, false);

        lastBatteryChangeTime = (TextView) view.findViewById(R.id.lastBatteryChangeTime);
        changeBatteryButton = (Button) view.findViewById(R.id.changeBatteryButton);

        Authentication auth = new Authentication(getActivity());
        currPlatesName = auth.getCrafter();


        changeBatteryButton.setOnClickListener(new addBatteryListener());
        new GetInformation().execute();

        return view;
    }
    private class addBatteryListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.batteryButton:
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    currDateTime = df.format(Calendar.getInstance().getTime());
                    new CreateBatteryManager().execute(String.format("%s/crafters/%d", Authentication.SERVER, currId));

                    //new addPassengerCrafter().execute();
                    break;

            }
        }
    }

    private class CreateBatteryManager extends AsyncTask<String, Void, String> {

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

                JSONObject battery = new JSONObject();
                battery.put("brand", "HTL");
                battery.put("model", "Max charge");
                battery.put("date", currDateTime);
                globalBatteries.put(battery);

                JSONObject b = new JSONObject();
                b.put("batteries", globalBatteries);


                return b.toString();
            } catch (JSONException je) {
                je.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            try{
                JSONObject alert = new JSONObject(result);
                if(alert.has("brand")){
                    //message.setText("");
                    Toast.makeText(getActivity(), "bateria enviada exitosamente .", Toast.LENGTH_SHORT).show();

                    //((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).ALERTSFRAGMENT);
                }
                else{
                    throw new JSONException("");
                }
            }
            catch (JSONException je){
                Toast.makeText(getActivity(), "Falló envío de bateria, intente de nuevo", Toast.LENGTH_SHORT).show();

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
                        JSONArray batteries = c.getJSONArray("batteries");
                        globalBatteries = c.getJSONArray("batteries");


                        if (plates.equals(currPlatesName)){
                            currId = c.getInt("id");
                            JSONObject battery = batteries.getJSONObject(batteries.length()-1);
                            lastBattery = battery.getString("date");
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
            lastBatteryChangeTime.setText(lastBattery);
        }

    }
}