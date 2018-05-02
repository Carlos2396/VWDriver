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

    private static String url = "https://fake-backend-mobile-app.herokuapp.com/crafters/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_battery, container, false);

        return view;
    }

}