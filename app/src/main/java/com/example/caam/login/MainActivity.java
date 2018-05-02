package com.example.caam.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    public static final int ROUTEFRAGMENT = 0;
    public static final int MAINTENANCESELECTCRAFTERFRAGMENT = 1;
    public static final int PERFORMANCEFRAGMENT = 2;
    public static final int ALERTSFRAGMENT = 3;
    public static final int SOSFRAGMENT = 4;
    public static final int SENDALERTFRAGMENT = 5;
    public static final int LOADGASFRAGMENT = 6;
    public static final int MAINTENANCEFRAGMENT = 7;
    public static final int CHANGEBATTERYFRAGMENT = 8;

    String priority;
    int alertId;
    int maintenanceCrafterId;
    Authentication auth;
    private ViewPager viewPager;

    int passengerNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOnTouchListener(new ViewPagerOnTouchListener());
        viewPager.setOnDragListener(new ViewPagerOnDragListener());
        setUpViewPager(viewPager);

        auth = new Authentication(getBaseContext());

        checkPermissions();
    }

    /**
     * Getters and Setters
     */

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public void setPassengerNum(int passengerNum) { this.passengerNum = passengerNum;}

    private class ViewPagerOnTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    private class ViewPagerOnDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) { return true; }
    }

    public void checkPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE: {
                if(!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(this, "Se requieren permisos para acceder a tu localización.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    /**
     * Toolbar methods
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public void notificationsHandler(MenuItem item) {
        Intent i = new Intent(this, NotificationsActivity.class);
        startActivity(i);

    }

    public void profileHandler(MenuItem item) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    /**
     * Pager mehtods
     */
    private void setUpViewPager(ViewPager vp){

        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RouteFragment(), "Route Fragment");
        adapter.addFragment(new MaintenanceSelectCrafterFragment(), "Maintenance Select Crafter Fragment");
        adapter.addFragment(new PerformanceFragment(), "Performance Fragment");
        adapter.addFragment(new AlertsFragment(), "Alert Fragment");
        adapter.addFragment(new SOSFragment(), "SOS Fragment");
        adapter.addFragment(new SendAlertFragment(), "Send Alert Fragment");
        adapter.addFragment(new LoadGasFragment(), "Load Gas");
        adapter.addFragment(new MaintenanceFragment(), "Maintenance Fragment");
        adapter.addFragment(new ChangeBatteryFragment(), "Change Battery");


        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        viewPager.setCurrentItem(fragmentNumber);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_route:
                    setViewPager(ROUTEFRAGMENT);
                    return true;
                case R.id.navigation_maintenance:
                    setViewPager(MAINTENANCESELECTCRAFTERFRAGMENT);
                    return true;
                case R.id.navigation_performance:
                    setViewPager(PERFORMANCEFRAGMENT);
                    return true;
                case R.id.navigation_alerts:
                    setViewPager(ALERTSFRAGMENT);
                    return true;
                case R.id.navigation_sos:
                    setViewPager(SOSFRAGMENT);
                    return true;

            }
            return false;
        }
    };
}
