package com.example.caam.login;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

import javax.net.ssl.HttpsURLConnection;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText usernameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        usernameET = (EditText)findViewById(R.id.username);
    }

        public void resetPassword(View v){
            new  ResetPasswordManager().execute("https://jsonplaceholder.typicode.com/posts", usernameET.getText().toString());
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
                postParams.put("title", params[1]); //change for usernmae
                postParams.put("body", params[2]); // change for password
                postParams.put("userId", "1"); // change for password

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostString(postParams));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader((new InputStreamReader(connection.getInputStream())));
                    while((line = br.readLine()) != null){
                        System.out.println(line);
                        response.append(line);
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
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
