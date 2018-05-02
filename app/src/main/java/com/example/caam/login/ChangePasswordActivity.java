package com.example.caam.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class ChangePasswordActivity extends AppCompatActivity{
    private String TAG = ChangePasswordActivity.class.getSimpleName();

    EditText currPass;
    EditText newPass;
    EditText confirmPass;
    TextView currError;
    TextView newError;
    Button changePassButton;

    String currPassString;
    int driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_change_password);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        currError = (TextView) findViewById(R.id.currErrorTxt);
        newError = (TextView) findViewById(R.id.newErrorTxt);

        currPass = (EditText) findViewById(R.id.currentTxt);
        newPass = (EditText) findViewById(R.id.newTxt);
        confirmPass = (EditText) findViewById(R.id.confirmTxt);

        changePassButton = (Button) findViewById(R.id.changePasswordBtn);
        changePassButton.setOnClickListener(new ChangeButtonListener());

        Authentication auth = new Authentication(getBaseContext());

        driverId = auth.getDriverID();

        new DriverManager().execute(String.format("%s/drivers/%d", Authentication.SERVER, driverId));
    }

    public boolean validate() {
        if(!currPassString.equals(currPass.getText().toString())){
            currError.setText(R.string.change_password_error_incorrect_current);
            return false;
        }
        else{
            currError.setText("");
        }

        if(newPass.getText().toString().length() <= 3){
            newError.setText(R.string.change_password_error_short_new);
            return false;
        }else{
            newError.setText("");
        }

        if(!newPass.getText().toString().equals(confirmPass.getText().toString())){
            newError.setText(R.string.change_password_error_confirmation_match);
            return false;
        }
        else{
            newError.setText("");
        }

        return true;
    }

    private class ChangeButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(validate()){
                new ChangePasswordManager().execute(String.format("%s/drivers/%d", Authentication.SERVER, driverId));
            }
        }
    }

    private class ChangePasswordManager extends AsyncTask<String, Void, String> {

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
                JSONObject driver = new JSONObject();
                driver.put("password", newPass.getText().toString());

                return driver.toString();
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
                    Toast.makeText(getBaseContext(), R.string.change_password_success, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    throw new JSONException("");
                }
            }
            catch (JSONException je){
                Toast.makeText(getBaseContext(), R.string.change_password_failure, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DriverManager extends AsyncTask<String, Void, String> {

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
            try{
                JSONObject user = new JSONObject(result);
                currPassString = user.getString("password");
            }
            catch (JSONException je){
                Toast.makeText(getBaseContext(), R.string.general_try_again_later, Toast.LENGTH_SHORT).show();
                je.printStackTrace();
                finish();
            }
        }
    }
}
