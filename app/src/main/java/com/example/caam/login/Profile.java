package com.example.caam.login;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static com.example.caam.login.LoginActivity.TAG;


/**
created Ernesto Ramírez Sáyago
 */
public class Profile extends Fragment {
    public static final String TAG = "Profile";
    Authentication auth;
    ImageView profileImage;
    TextView driverName;
    RatingBar driverRating;
    Button changePassword;
    Button closeSession;

    String loggedDriverName;
    int loggedDriverRating;

    private static String url = "https://fake-backend-mobile-app.herokuapp.com/drivers/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        driverName = (TextView) view.findViewById(R.id.driverName);
        driverRating = (RatingBar) view.findViewById(R.id.driverRating);
        changePassword = (Button) view.findViewById(R.id.changePassword);
        closeSession = (Button) view.findViewById(R.id.closeSession);
        changePassword.setOnClickListener(new ProfileListener());
        closeSession.setOnClickListener(new ProfileListener());

        //driverList = new ArrayList<>();
        new GetInformation().execute();

        return view;
    }

    private class ProfileListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.closeSession:

                    auth = new Authentication(getActivity().getBaseContext());
                    auth.removeAuthData();
                    Intent intent = new Intent(getActivity(), Profile.class);
                    startActivity(intent);
                    break;
                case R.id.changePassword:


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

                    // Getting JSON Array node
                    JSONArray ratings = new JSONArray(jsonStr);

                    // looping through All drivers
                    for (int i = 0; i < ratings.length(); i++) {
                        JSONObject r = ratings.getJSONObject(i);
                        int rating = r.getInt("rating");
                        String name = r.getString("name");

                        if (name.equals("Bure")){ //TODO: checar que sea el usuario logueado
                            loggedDriverName = name;
                            loggedDriverRating = rating;
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
                                    Toast.LENGTH_LONG).show();
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
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            driverName.setText(loggedDriverName);
            driverRating.setNumStars(loggedDriverRating);
        }
    }

    public void logOut(View v){

    }

    public void changePassword(View v){

    }
}
    //}
