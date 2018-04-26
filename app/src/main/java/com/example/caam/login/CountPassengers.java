package com.example.caam.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Ernesto on 23-Apr-18.
 */


public class CountPassengers extends AppCompatActivity {

    TextView passengerNumTV;
    TextView nextStopTV;
    TextView passengerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_passengers);

        passengerNumTV = (TextView) findViewById(R.id.passengerNum);
        nextStopTV = (TextView) findViewById(R.id.nextStop);
        passengerTV = (TextView) findViewById(R.id.passengers);
    }

    public void addPassengers(View v){
        String StringNum = passengerNumTV.getText().toString();
        int num = Integer.parseInt(StringNum);
        num++;
        if (num != 1){
            passengerTV.setText("passengers");
        } else{
            passengerTV.setText("passenger");
        }
        passengerNumTV.setText(Integer.toString(num));
    }

    public void removePassengers(View v){
        String StringNum = passengerNumTV.getText().toString();
        int num = Integer.parseInt(StringNum);
        if (num > 0){
            num--;
            if (num != 1){
                passengerTV.setText("passengers");
            } else{
                passengerTV.setText("passenger");
            }
            passengerNumTV.setText(Integer.toString(num));
        } else {
            passengerNumTV.setText(Integer.toString(num));
        }
    }

}
