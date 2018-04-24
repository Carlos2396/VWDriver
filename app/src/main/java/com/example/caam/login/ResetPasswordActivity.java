package com.example.caam.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

import javax.net.ssl.HttpsURLConnection;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText usernameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        System.out.println("Started ResetPassword activity.");

        usernameET = (EditText)findViewById(R.id.username);

        Intent i = getIntent();
        if(i.getStringExtra("username") == null){
            System.out.println("Is null");
        }
        System.out.println(i.getStringExtra("username"));
        usernameET.setText(i.getStringExtra("username"));
    }

    public void resetPassword(View v){
        new  ResetPasswordManager().execute("https://jsonplaceholder.typicode.com/posts", usernameET.getText().toString());
    }

    public void returnToLogin(View v) {
        finish();
    }

    private class ResetPasswordManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            try{
                URL url = new URL(params[0]);

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                HashMap<String, String> postParams = new HashMap<>();
                postParams.put("title", params[1]); //change for username
                postParams.put("body", "body"); // change for password
                postParams.put("userId", "1"); // change for password

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostString(postParams));
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
            String message;
            try{
                JSONObject response = new JSONObject(result);
                message = String.format("Un correo para reestablecer tu contraseña ha sido enviado a %s", response.getString("title"));
            }
            catch(JSONException je){
                message = String.format("Intente de nuevo en unos minutos.");
            }

            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
        }

        private String getPostString(HashMap<String,String> params) throws UnsupportedEncodingException {
            StringBuffer result = new StringBuffer();
            boolean first = true;

            for(Map.Entry<String, String> entry: params.entrySet()){
                if(first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }
}
