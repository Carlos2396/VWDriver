package com.example.caam.login;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Ernesto on 01-May-18.
 */

public class ChangePasswordFragment extends AppCompatActivity{
    private String TAG = ChangePasswordFragment.class.getSimpleName();

    TextView currPass;
    EditText newPass;
    EditText confirmPass;
    Button changePassButton;

    String currPassString;
    String newPassString;
    String confirmPassString;
    int driverId;

    private static String url = "https://fake-backend-mobile-app.herokuapp.com/drivers/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_change_password);

        currPass = (TextView) findViewById(R.id.currPass);
        newPass = (EditText) findViewById(R.id.newPass);
        confirmPass = (EditText) findViewById(R.id.confirmPass);
        changePassButton = (Button) findViewById(R.id.changePassButton);

        Authentication auth = new Authentication(getBaseContext());
        driverId = auth.getDriverID();

        new GetInformation().execute();
        changePassButton.setOnClickListener(new ChangePasswordListener());

        //return view;
    }
    private class ChangePasswordListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.changePassButton:
                    newPassString = newPass.getText().toString();
                    confirmPassString = confirmPass.getText().toString();

                    if (!(newPassString.equals(confirmPassString))){
                    Toast.makeText(ChangePasswordFragment.this, "Error: No concuerdan las contraseñas", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePasswordFragment.this, ProfileActivity.class);
                        startActivity(intent);
                } else {
                        new CreatePasswordManager().execute(String.format("%s/drivers/%d", Authentication.SERVER, driverId));
                    }



                    //new addPassengerCrafter().execute();
                    break;

            }
        }
    }

    private class CreatePasswordManager extends AsyncTask<String, Void, String> {

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
                JSONObject driver = new JSONObject();

                driver.put("password", newPassString);


                return driver.toString();
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
                    Toast.makeText(ChangePasswordFragment.this, "Falló envío de refill, intente de nuevo ", Toast.LENGTH_SHORT).show();

                    //((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).ALERTSFRAGMENT);
                }
                else{
                    throw new JSONException("");
                }
            }
            catch (JSONException je){
                Toast.makeText(ChangePasswordFragment.this, "Contraseña actualizada exitosamente.", Toast.LENGTH_SHORT).show();
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
                    JSONArray drivers = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < drivers.length(); i++) {
                        JSONObject c = drivers.getJSONObject(i);

                        int id = c.getInt("id");
                        String password = c.getString("password");

                        if (id == driverId){
                            currPassString = password;
                            break;
                        }


                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
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
            currPass.setText(currPassString);
        }

    }
}
