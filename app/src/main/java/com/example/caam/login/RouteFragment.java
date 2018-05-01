package com.example.caam.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import javax.net.ssl.HttpsURLConnection;

public class RouteFragment extends Fragment {
    private String TAG = RouteFragment.class.getSimpleName();
    //Authentication auth = new Authentication(getActivity());
    int currId;

    Button add;
    Button remove;
    TextView passNum;
    TextView currCrafter;
    String currPassNum;
    String currCrafterName;
    int passLimit;

    private static String url = "https://fake-backend-mobile-app.herokuapp.com/crafters/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        add = (Button) view.findViewById(R.id.add);
        remove = (Button) view.findViewById(R.id.remove);
        passNum = (TextView) view.findViewById(R.id.passNum);
        currCrafter = (TextView) view.findViewById(R.id.currCrafter);
        Authentication auth = new Authentication(getActivity());
        //currId = auth.getDriverID();
        currCrafterName = auth.getCrafter();

        //System.out.println("currId:" + currId);
        // currId = 1;

        add.setOnClickListener(new routeListener());
        remove.setOnClickListener(new routeListener());

        //currId = 1;


        new GetCrafters().execute();
        return view;
    }

    private class routeListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.add:

                    new addPassengerCrafter().execute(String.format("%s/crafters/%d", Authentication.SERVER, currId));

                    break;
                case R.id.remove:
                    new removePassengerCrafter().execute(String.format("%s/crafters/%d", Authentication.SERVER, currId));
                    break;


            }
        }
    }
    private class addPassengerCrafter extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            try{
                URL url = new URL(params[0]);
                //getLastLocation();

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("PUT");
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

        protected String getPostJson(){

            try{
                int integerCurrPassNum = Integer.parseInt(currPassNum);
                if (integerCurrPassNum < passLimit){
                    integerCurrPassNum++;
                    currPassNum = Integer.toString(integerCurrPassNum);
                } else{
                    currPassNum = Integer.toString(passLimit);
                }
                passNum.setText(currPassNum);

                JSONObject crafter = new JSONObject();

                crafter.put("passengers", Integer.parseInt(currPassNum));



                return crafter.toString();
            }
            catch (JSONException je){
                je.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            /*try{
                JSONObject alert = new JSONObject(result);
               if(alert.has("id")){
                    //message.setText("");
                    //Toast.makeText(getActivity(), "Alerta enviada exitosamente.", Toast.LENGTH_SHORT).show();
                   // ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).ALERTSFRAGMENT);
               }
                else{
                    throw new JSONException("");
                }
            }
            catch (JSONException je){
                //Toast.makeText(getActivity(), "Falló envío de alerta, intente de nuevo.", Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    private class removePassengerCrafter extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            try{
                URL url = new URL(params[0]);
                //getLastLocation();

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

        protected String getPostJson(){
           /* HttpHandler sh = new HttpHandler();
            String model = "a";
            String plates = "a";
            int year = 0;
            String start = "a";
            int capacity = 0;
            JSONArray batteryArr = null;
            JSONArray refillArr = null;

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    //JSONArray crafters = new JSONArray(jsonStr);


                        JSONObject c = new JSONObject();

                       /* model = c.getString("model");
                        plates = c.getString("plates");
                        year = c.getInt("year");
                        //String year = Integer.toString(unparsedYear);
                        start = c.getString("start");
                        capacity = c.getInt("capacity");
                        batteryArr = c.getJSONArray("batteries");
                        refillArr = c.getJSONArray("fuel_reffils");
                       c.put("passengers", Integer.parseInt(currPassNum));
                       return c.toString();


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

            }*/
            try{
                int integerCurrPassNum = Integer.parseInt(currPassNum);
                if (integerCurrPassNum > 0){
                    integerCurrPassNum--;
                    currPassNum = Integer.toString(integerCurrPassNum);
                } else{
                    currPassNum = "0";
                }
                passNum.setText(currPassNum);
                JSONObject crafter = new JSONObject();
                crafter.put("passengers", Integer.parseInt(currPassNum));


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
                JSONObject alert = new JSONObject(result);
                if(alert.has("id")){
                    //message.setText("");
                    //Toast.makeText(getActivity(), "Alerta enviada exitosamente.", Toast.LENGTH_SHORT).show();
                    // ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).ALERTSFRAGMENT);
                }
                else{
                    throw new JSONException("");
                }
            }
            catch (JSONException je){
                //Toast.makeText(getActivity(), "Falló envío de alerta, intente de nuevo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetCrafters extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

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
                        int unparsedYear = c.getInt("year");
                        String year = Integer.toString(unparsedYear);
                        String start = c.getString("start");
                        int unparsedCapacity = c.getInt("capacity");
                        String capacity = Integer.toString(unparsedCapacity);
                        int unparsedPassengers = c.getInt("passengers");
                        String passengers = Integer.toString(unparsedPassengers);


                        if (plates.equals(currCrafterName)){
                            currPassNum = passengers;
                            passLimit = unparsedCapacity;
                            currId = (i+1);
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
            // Dismiss the progress dialog

            passNum.setText(currPassNum);
            currCrafter.setText(currCrafterName);
        }

    }
}

