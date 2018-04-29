package com.example.caam.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RouteFragment extends Fragment {
    private String TAG = RouteFragment.class.getSimpleName();
    Authentication auth;

    Button add;
    Button remove;
    TextView passNum;
    String currPassNum;

    private static String url = "https://fake-backend-mobile-app.herokuapp.com/crafters/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        add = (Button) getActivity().findViewById(R.id.add);
        remove = (Button) getActivity().findViewById(R.id.remove);
        passNum = (TextView) getActivity().findViewById(R.id.passNum);

        add.setOnClickListener(new routeListener());
        remove.setOnClickListener(new routeListener());

        new GetCrafters().execute();
        return view;
    }

    private class routeListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.add:
                    new addPassengerCrafter().execute();
                    break;
                case R.id.remove:
                    new removePassengerCrafter().execute();
                    break;


            }
        }
    }

    private class addPassengerCrafter extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

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
                    JSONArray crafters = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < crafters.length(); i++) {
                        JSONObject c = crafters.getJSONObject(i);

                        /*if (c.get("id").toString().equals("1")){
                            c.put("age", 25);
                        }*/

                        int unparsedId = c.getInt("id");
                        String id = Integer.toString(unparsedId);
                        String model = c.getString("model");
                        int unparsedPassengers = c.getInt("passengers");
                        String passengers = Integer.toString(unparsedPassengers);

                        if (id.equals("1")){
                            unparsedPassengers++;
                            currPassNum = Integer.toString(unparsedPassengers);
                            break;
                        }

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
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


            passNum.setText(currPassNum);
        }

    }

    private class removePassengerCrafter extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

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
                    JSONArray crafters = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < crafters.length(); i++) {
                        JSONObject c = crafters.getJSONObject(i);

                        /*if (c.get("id").toString().equals("1")){
                            c.put("age", 25);
                        }*/

                        int unparsedId = c.getInt("id");
                        String id = Integer.toString(unparsedId);
                        String model = c.getString("model");
                        int unparsedPassengers = c.getInt("passengers");
                        String passengers = Integer.toString(unparsedPassengers);

                        if (id.equals("1")){
                            unparsedPassengers--;
                            if (unparsedPassengers >= 0){
                                currPassNum = Integer.toString(unparsedPassengers);

                            } else{
                                currPassNum = "0";
                            }
                            break;
                        }

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
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


            passNum.setText(currPassNum);
        }

    }

    private class GetCrafters extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

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
                    JSONArray crafters = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < crafters.length(); i++) {
                        JSONObject c = crafters.getJSONObject(i);

                        /*if (c.get("id").toString().equals("1")){
                            c.put("age", 25);
                        }*/

                        int unparsedId = c.getInt("id");
                        String id = Integer.toString(unparsedId);
                        String model = c.getString("model");
                        int unparsedPassengers = c.getInt("passengers");
                        String passengers = Integer.toString(unparsedPassengers);

                        if (id.equals("1")){
                            currPassNum = passengers;
                            break;
                        }


                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
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
            // Dismiss the progress dialog

            passNum.setText(currPassNum);
        }

    }
}

