package com.example.caam.login;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class MaintenanceFragment extends Fragment {
    private String TAG = MaintenanceFragment.class.getSimpleName();
    TextView currCrafter;
    TextView currPlates;
    TextView gasTextView;
    TextView batteryTextView;
    Button gasButton;
    Button batteryButton;
    Button changeCrafterButton;
    String currCrafterName;
    String currPlatesName;

    int currId;
    private static String url = "https://fake-backend-mobile-app.herokuapp.com/crafters/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance, container, false);

        currCrafter = (TextView) view.findViewById(R.id.currCrafter);
        currPlates = (TextView) view.findViewById(R.id.currPlates);
        gasTextView = (TextView) view.findViewById(R.id.gasTextView);
        batteryTextView = (TextView) view.findViewById(R.id.batteryTextView);
        gasButton = (Button) view.findViewById(R.id.changeBatteryButton);
        batteryButton = (Button) view.findViewById(R.id.batteryButton);
        changeCrafterButton = (Button) view.findViewById(R.id.changeCrafterButton);
        Authentication auth = new Authentication(getActivity());
        //currPlatesName = auth.getCrafter();


        gasButton.setOnClickListener(new MaintenanceListener());
        batteryButton.setOnClickListener(new MaintenanceListener());
        changeCrafterButton.setOnClickListener(new MaintenanceListener());

        new GetInformation().execute();

        return view;
    }

    private class MaintenanceListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.changeBatteryButton:
                    ((MainActivity)getActivity()).setViewPager(7);
                //gasTextView.setText("aaaa");
                    //new addPassengerCrafter().execute();
                    break;
                case R.id.batteryButton:
                    ((MainActivity)getActivity()).setViewPager(8);
                    //batteryTextView.setText("bbbb");

                    break;
                case R.id.changeCrafterButton:
                    ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).MAINTENANCESELECTCRAFTERFRAGMENT);
                    break;
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
                    JSONArray crafters = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < crafters.length(); i++) {
                        JSONObject c = crafters.getJSONObject(i);

                        String plates = c.getString("plates");

                        if (plates.equals(currPlatesName)){
                            currId = c.getInt("id");
                            break;
                        }


                        // adding notif to notifs list

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
            currPlates.setText(currPlatesName);
            String currIdString = Integer.toString(currId);
            currCrafterName = "Crafter #" + currIdString;
            currCrafter.setText(currCrafterName);
        }

    }
}
