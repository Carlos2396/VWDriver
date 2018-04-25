package com.example.caam.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int PERFORMANCEFRAGMENT = 0;
    public static final int ROUTEFRAGMENT = 2;
    public static final int ROUTESELECTCRAFTERFRAGMENT = 1;
    public static final int MAINTENANCEFRAGMENT = 4;
    public static final int MAINTENANCESELECTCRAFTERFRAGMENT = 3;
    public static final int ALERTSFRAGMENT = 5;
    public static final int SOSFRAGMENT = 6;

    Authentication auth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent i;
            switch (item.getItemId()) {
                case R.id.navigation_route:
                    if(auth.getCrafter() != null){
                        setViewPager(ROUTEFRAGMENT);
                    }
                    else{
                        setViewPager(ROUTESELECTCRAFTERFRAGMENT);
                    }
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

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        viewPager = (ViewPager)findViewById(R.id.container);
        setUpViewPager(viewPager);

        auth = new Authentication(getBaseContext());
    }

    private void setUpViewPager(ViewPager vp){

        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PerformanceFragment(), "Performance Fragment");
        adapter.addFragment(new RouteSelectCrafterFragment(), "Route Select Crafter Fragment");
        adapter.addFragment(new RouteFragment(), "Route Fragment");
        adapter.addFragment(new MaintenanceSelectCrafterFragment(), "Maintenance Select Crafter Fragment");
        adapter.addFragment(new MaintenanceFragment(), "Maintenance Fragment");
        adapter.addFragment(new AlertsFragment(), "Alert Fragment");
        adapter.addFragment(new SOSFragment(), "SOS Fragment");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        viewPager.setCurrentItem(fragmentNumber);
    }
}
