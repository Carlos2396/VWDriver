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

    TextView modelTxt;
    TextView lastChangeTxt;
    Button returnBtn;
    int crafterId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battery, container, false);

        modelTxt = (TextView) view.findViewById(R.id.modelTxt);
        lastChangeTxt = (TextView) view.findViewById(R.id.lastChangeTxt);
        returnBtn = (Button) view.findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new ReturnListener());

        crafterId = ((MainActivity)getActivity()).maintenanceCrafterId;

        new GetCrafterManager().execute(String.format("%s/crafters/%d", Authentication.SERVER, crafterId));

        return view;
    }

    private class ReturnListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).MAINTENANCEFRAGMENT);
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
            Log.d(TAG, result);
            try {
                JSONObject crafter = new JSONObject(result);
                JSONArray batteries = crafter.getJSONArray("batteries");
                JSONObject battery = batteries.getJSONObject(batteries.length()-1);

                lastChangeTxt.setText(battery.getString("date"));
                modelTxt.setText(battery.getString("model"));
            }
            catch(JSONException je) {
                je.printStackTrace();
            }
        }
    }
}