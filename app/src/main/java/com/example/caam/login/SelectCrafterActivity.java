package com.example.caam.login;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SelectCrafterActivity extends AppCompatActivity {
    Spinner spinner;
    ArrayList<String> crafters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_crafter);
        spinner = (Spinner)findViewById(R.id.spinner);

        loadCrafters();
    }

    public void loadCrafters(){
        new CrafterManager().execute("https://jsonplaceholder.typicode.com/posts");
    }

    public void fillSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, crafters);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private class CrafterManager extends AsyncTask<String, Void, String> {

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
                connection.setDoOutput(true);

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
            fillSpinner();
        }
    }
}
