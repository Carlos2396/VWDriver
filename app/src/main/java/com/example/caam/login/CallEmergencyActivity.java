package com.example.caam.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Ernesto on 24-Apr-18.
 */

public class CallEmergencyActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_emergency);
    }

    public void callFirefighter(View v){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:066"));
        startActivity(intent);
    }

    public void notCallNumber(View v){
        setContentView(R.layout.activity_login);
    }
}
