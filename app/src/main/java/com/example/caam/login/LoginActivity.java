package com.example.caam.login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.json.*;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    Authentication auth;
    EditText usernameET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = new Authentication(this.getBaseContext());
        if(auth.isLogged()){
            Intent i = new Intent(getApplicationContext(), SelectCrafterActivity.class);
            startActivity(i);
            finish();
        }

        System.out.println("Started");

        usernameET = (EditText)findViewById(R.id.username);
        passwordET = (EditText)findViewById(R.id.password);
    }

    public void login(View v){
        new LoginManager().execute(String.format("%s/drivers?email=%s&password=%s", Authentication.SERVER, usernameET.getText().toString(), passwordET.getText().toString()));
    }

    public void startResetPassword(View v){
        Intent i = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        i.putExtra("username", usernameET.getText().toString());
        startActivity(i);
    }

    private class LoginManager extends AsyncTask<String, Void, String> {

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

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if(auth.setAuthData(result)){
                System.out.println("Successfully set Auth data");
                Intent i = new Intent(getApplicationContext(), SelectCrafterActivity.class);
                startActivity(i);
                finish();
            }
            else {
                Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
