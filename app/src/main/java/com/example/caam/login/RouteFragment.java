package com.example.caam.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

    CountDownTimer timer;

    ImageView add;
    ImageView remove;
    Button change;
    TextView passNum;

    int currPassNum;
    int selectedCrafterId;
    int passLimit;

    private static String url = "https://fake-backend-mobile-app.herokuapp.com/crafters/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        add = (ImageView) view.findViewById(R.id.add);
        remove = (ImageView) view.findViewById(R.id.remove);
        passNum = (TextView) view.findViewById(R.id.passNum);
        change = (Button) view.findViewById(R.id.changeBtn);

        Authentication auth = new Authentication(getActivity());
        selectedCrafterId = auth.getCrafter();
        Log.d(TAG, selectedCrafterId + " selected crafter id");

        add.setOnClickListener(new routeListener());
        remove.setOnClickListener(new routeListener());

        currPassNum = 0;
        passLimit = 20;

        initTimer();

        new GetCrafterManager().execute(String.format("%s/crafters/%d", Authentication.SERVER, selectedCrafterId));
        return view;
    }

    public void initTimer() {
        timer = new CountDownTimer(1000*60*60*24, 1000*30) {
            @Override
            public void onTick(long l) {
                new UpdatePassengersManager().execute(String.format("%s/crafters/%d", Authentication.SERVER, selectedCrafterId));
            }

            @Override
            public void onFinish() {
                startActivity(((MainActivity)getActivity()).getIntent());
                ((MainActivity)getActivity()).finish();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        initTimer();
        Log.d(TAG, "onResumed");
        timer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        Log.d(TAG, "onPaused");
    }

    private class routeListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.add:
                    if(currPassNum < passLimit)
                        currPassNum++;
                    break;
                case R.id.remove:
                    if(currPassNum > 0)
                        currPassNum--;
                    break;
            }
            passNum.setText(currPassNum + "");
        }
    }

    /**
     * Makes the post request
     */
    private class UpdatePassengersManager extends AsyncTask<String, Void, String> {

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
                JSONObject crafter = new JSONObject();
                crafter.put("passengers", currPassNum);

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
                    Toast.makeText(getActivity(), "Pasajeros actualizados exitosamente.", Toast.LENGTH_SHORT).show();
                }
                else{
                    throw new JSONException("");
                }
            }
            catch (JSONException je){
                Toast.makeText(getActivity(), "Falló actualización de pasajeros.", Toast.LENGTH_SHORT).show();
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
            try {
                JSONObject crafter = new JSONObject(result);
                change.setText(crafter.getString("plates"));
                passLimit = crafter.getInt("capacity");
                currPassNum = crafter.getInt("passengers");
                passNum.setText(currPassNum + "");
            }
            catch(JSONException je) {
                je.printStackTrace();
            }
        }
    }
}

