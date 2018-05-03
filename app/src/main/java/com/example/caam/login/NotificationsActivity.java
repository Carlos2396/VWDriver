package com.example.caam.login;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ernesto on 29-Apr-18.
 */

public class NotificationsActivity extends AppCompatActivity {
    private String TAG = NotificationsActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    private static String url = "https://fake-backend-mobile-app.herokuapp.com/alerts?_sort=datetime&_order=desc";
    ArrayList<HashMap<String, String>> notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_notifications);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        notificationsList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        new GetNotifications().execute();
    }

    private class GetNotifications extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(NotificationsActivity.this);
            pDialog.setMessage("Por favor, espera un momento.");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray notifs = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < notifs.length(); i++) {
                        JSONObject c = notifs.getJSONObject(i);

                        String type = c.getString("type");
                        String priority = c.getString("priority");
                        String message = c.getString("message");
                        String datetime = c.getString("datetime");

                        // tmp hash map for single contact
                        HashMap<String, String> notif = new HashMap<>();

                        // adding each child node to HashMap key => value
                        notif.put("type", type);
                        notif.put("priority", priority);
                        notif.put("message", message);
                        notif.put("datetime", datetime);


                        // adding notif to notifs list
                        notificationsList.add(notif);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.general_try_again_later, Toast.LENGTH_LONG).show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    NotificationsActivity.this, notificationsList,
                    R.layout.list_item, new String[]{"type", "priority", "message",
                    "datetime"}, new int[]{R.id.type,
                    R.id.priority, R.id.message, R.id.datetime});

            lv.setAdapter(adapter);
        }

    }
}

