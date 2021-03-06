package com.example.caam.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    Authentication auth;
    EditText usernameET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        auth = new Authentication(this.getBaseContext());
        if(auth.isLogged()){
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        usernameET = (EditText)findViewById(R.id.username);
        passwordET = (EditText)findViewById(R.id.password);
    }

    public void login(View v){
        if(validateInput()){
            new LoginManager().execute(String.format("%s/drivers?email=%s&password=%s", Authentication.SERVER, usernameET.getText().toString(), passwordET.getText().toString()));
        }
        else{
            Toast.makeText(getBaseContext(), R.string.login_not_valid_input, Toast.LENGTH_SHORT).show();
        }
    }

    public void resetPassword(View v){
        if(usernameET.getText().toString().length() > 3){
            Toast.makeText(getBaseContext(), R.string.login_reset_password_sent, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getBaseContext(), R.string.login_missing_email, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validateInput(){
        return usernameET.getText().toString().length() > 3 && passwordET.getText().toString().length() > 3;
    }

    private class LoginManager extends AsyncTask<String, Void, String> {

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
            if(auth.setAuthData(result)){
                Intent i = new Intent(getApplicationContext(), SelectCrafterActivity.class);
                startActivity(i);
                finish();
            }
            else {
                Toast.makeText(getBaseContext(), R.string.login_failure, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
