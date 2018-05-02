package com.example.caam.login;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class MaintenanceFragment extends Fragment {
    private String TAG = MaintenanceFragment.class.getSimpleName();

    Button changeCrafterBtn;
    ListView mList;

    ArrayAdapter<String> adapter;
    ArrayList<String> entries;
    int crafterId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance, container, false);

        mList = (ListView) view.findViewById(R.id.list);
        mList.setOnItemClickListener(new ListListener());

        changeCrafterBtn = (Button) view.findViewById(R.id.changeCrafterBtn);
        changeCrafterBtn.setOnClickListener(new ChangeCrafterListener());

        crafterId = ((MainActivity)getActivity()).maintenanceCrafterId;
        loadCrafter();
        return view;
    }

    public void loadCrafter(){
        new GetCrafterManager().execute(String.format("%s/crafters/%d", Authentication.SERVER, crafterId));
    }

    public void formatData(String... data){
        entries = new ArrayList<String>();
        for (String entry: data){
            entries.add(entry);
        }

        entries.add("Gasolina");
        entries.add("Batería");

    }

    public void setListData() {
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, entries);
        mList.setAdapter(adapter);
    }

    private class ListListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String item = adapter.getItem(i);
            Log.d(TAG, item);
            switch (item) {
                case "Gasolina":
                    ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).LOADGASFRAGMENT);
                    break;
                case "Batería":
                    ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).CHANGEBATTERYFRAGMENT);
                    break;
            }
        }
    }

    private class ChangeCrafterListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).MAINTENANCESELECTCRAFTERFRAGMENT);
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
                new GetCrafterManager().execute(String.format("%s/crafters/%d", Authentication.SERVER, crafterId));
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            try {
                JSONObject crafter = new JSONObject(result);

                formatData("Placa: " + crafter.getString("plates"),
                        "Modelo: " + crafter.getString("model"),
                        "Año: " + crafter.getInt("year"));

                setListData();
            }
            catch(JSONException je) {
                je.printStackTrace();
            }
        }
    }
}
