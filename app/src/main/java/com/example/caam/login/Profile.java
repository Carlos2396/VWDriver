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
            String url = "https://fake-backend-mobile-app.herokuapp.com/drivers";
            String jsonStr = sh.makeServiceCall(url);

            //Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray ratings = jsonObj.getJSONArray("rating");

                    // looping through All Contacts
                    for (int i = 0; i < ratings.length(); i++) {
                        JSONObject r = ratings.getJSONObject(i);
                        String rating = r.getString("rating");
                        String name = r.getString("name");

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                   /* runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });*/

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });*/
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            /*ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{ "email","mobile"},
                    new int[]{R.id.email, R.id.mobile});
            lv.setAdapter(adapter);*/
            driverName.setText("name");
            driverRating.setNumStars(Integer.parseInt("rating"));
        }
    }

    public void logOut(View v){

    }

    public void changePassword(View v){

    }
}
    //}
