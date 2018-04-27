package com.example.caam.login;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendAlertFragment extends Fragment implements LocationListener {
    private static final String TAG = "SendAlertFragment";
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 5; // medio minuto
    private static final long MIN_DISTANCE_BETWEEN_UPDATES = 1; // 1.5 metros

    private LocationManager mLocationManager;
    Location location;

    ImageView image;
    TextView title;
    TextView error;
    EditText message;
    Button send;
    Button cancel;

    Authentication auth;
    String type;
    String priority;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_alert, container, false);

        image = (ImageView) view.findViewById(R.id.image);
        title = (TextView) view.findViewById(R.id.type);
        message = (EditText) view.findViewById(R.id.message);
        error = (TextView) view.findViewById(R.id.error);
        error.setVisibility(View.GONE);

        send = (Button) view.findViewById(R.id.send);
        cancel = (Button) view.findViewById(R.id.cancel);
        send.setOnClickListener(new SendListener());
        cancel.setOnClickListener(new CancelListener());

        auth = new Authentication(getActivity());

        int selected = ((MainActivity) getActivity()).getAlertId();
        priority = ((MainActivity) getActivity()).getPriority();

        setType(selected);

        mLocationManager = (LocationManager) ((MainActivity) getActivity()).getSystemService(Context.LOCATION_SERVICE);
        location = null;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpdates();
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            getLastLocation();
        }
        else{
            send.setEnabled(false);
            error.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    void setType(int selected) {
        switch (selected) {
            case 1:
                type = "Accidente";
                image.setImageResource(R.drawable.accident);
                break;
            case 2:
                type = "Calle cerrada";
                image.setImageResource(R.drawable.closed_street);
                break;
            case 3:
                type = "Tráfico";
                image.setImageResource(R.drawable.traffic);
                break;
        }

        title.setText(type);
    }

    /**
     * Button listeners
     */
    private class SendListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            new CreateAlertManager().execute(String.format("%s/alerts", Authentication.SERVER));
        }
    }

    private class CancelListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ((MainActivity) getActivity()).setViewPager(((MainActivity) getActivity()).ALERTSFRAGMENT);
        }
    }

    /**
     * Location methods
     */

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location loc) {
                        // GPS location can be null if GPS is switched off
                        if (loc != null) {
                            location = loc;
                            Log.d(TAG, location.toString());
                        }
                        else {
                            Log.d(TAG, "Location is null");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    public void setUpdates(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.i(TAG, String.valueOf(location.getLatitude()));
        Log.i(TAG, String.valueOf(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        send.setEnabled(true);
        error.setVisibility(View.GONE);
    }

    @Override
    public void onProviderDisabled(String provider) {
        send.setEnabled(false);
        error.setVisibility(View.VISIBLE);
    }

    /**
     * Makes the post request
     */
    private class CreateAlertManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            try{
                URL url = new URL(params[0]);
                getLastLocation();

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostJson());
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_CREATED){
                    String line;
                    BufferedReader br = new BufferedReader((new InputStreamReader(connection.getInputStream())));
                    while((line = br.readLine()) != null){
                        response.append(line);
                    }
                }
                else {
                    Log.d(TAG, responseCode + "");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            Log.d(TAG, response.toString());

            return response.toString();
        }

        protected String getPostJson(){
            try{
                JSONObject alert = new JSONObject();
                alert.put("type", type);
                alert.put("priority", priority);
                alert.put("message", message.getText().toString());
                if(location != null){
                    alert.put("lat", location.getLatitude());
                    alert.put("lng", location.getLongitude());
                }
                else{
                    alert.put("lat", null);
                    alert.put("lng", null);
                }

                return alert.toString();
            }
            catch (JSONException je){
                je.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONObject alert = new JSONObject(result);
                if(alert.has("id")){
                    message.setText("");
                    Toast.makeText(getActivity(), "Alerta enviada exitosamente.", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).ALERTSFRAGMENT);
                }
                else{
                    throw new JSONException("");
                }
            }
            catch (JSONException je){
                Toast.makeText(getActivity(), "Falló envío de alerta, intente de nuevo.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
