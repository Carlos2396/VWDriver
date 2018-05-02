package com.example.caam.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ernesto on 30-Apr-18.
 */

public class ProfileActivity extends AppCompatActivity {
    private String TAG = NotificationsActivity.class.getSimpleName();

    RatingBar driverRating;
    Button changePassword;
    Button closeSession;
    TextView driverName;

    int driverId;
    int driverStars;
    String currDriverName;

    private static String url = "https://fake-backend-mobile-app.herokuapp.com/drivers/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Perfil");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        driverRating = (RatingBar) findViewById(R.id.driverRating);
        changePassword = (Button) findViewById(R.id.changePassword);
        closeSession = (Button) findViewById(R.id.closeSession);
        driverName = (TextView) findViewById(R.id.driverName);

        Authentication auth = new Authentication(getBaseContext());
        driverId = auth.getDriverID();

        changePassword.setOnClickListener(new ProfileListener());
        closeSession.setOnClickListener(new ProfileListener());

        new GetInformation().execute();
    }

    private class ProfileListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.changePassword:
                    Intent intent = new Intent(getBaseContext(), ChangePasswordActivity.class);
                    startActivity(intent);
                    break;
                case R.id.closeSession:
                    Authentication auth = new Authentication(getBaseContext());
                    auth.removeAuthData();

                    Intent i = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(i);
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

                        int id = c.getInt("id");
                        if (id == driverId){
                            currDriverName = c.getString("name");
                            driverStars = c.getInt("rating");
                            break;
                        }


                        // adding notif to notifs list

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
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

            driverRating.setNumStars(driverStars);
            driverName.setText(currDriverName);

        }

    }
}
