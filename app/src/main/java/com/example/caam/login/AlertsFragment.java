package com.example.caam.login;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlertsFragment extends Fragment {

    ImageView low;
    ImageView high;
    ImageView street;
    ImageView traffic;
    ImageView accident;

    String priority;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);

        low = (ImageView) view.findViewById(R.id.lowPriority);
        high = (ImageView) view.findViewById(R.id.highPriority);

        low.setOnClickListener(new PriorityListener());
        high.setOnClickListener(new PriorityListener());

        street = (ImageView) view.findViewById(R.id.street);
        traffic = (ImageView) view.findViewById(R.id.traffic);
        accident = (ImageView) view.findViewById(R.id.accident);

        street.setOnClickListener(new AlertListener());
        traffic.setOnClickListener(new AlertListener());
        accident.setOnClickListener(new AlertListener());

        priority = "Baja";

        return view;
    }

    private class PriorityListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.lowPriority){
                low.setImageResource(R.drawable.lowpriorityiconselected);
                high.setImageResource(R.drawable.highpriorityicon);
                priority = "Baja";
            }
            else{
                low.setImageResource(R.drawable.lowpriorityicon);
                high.setImageResource(R.drawable.highpriorityiconselected);
                priority = "Alta";
            }
        }
    }

    private class AlertListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int selected = 0;
            switch (view.getId()){
                case R.id.accident:
                    selected = 1;
                    break;
                case R.id.street:
                    selected = 2;
                    break;
                case R.id.traffic:
                    selected = 3;
                    break;
            }
            ((MainActivity)getActivity()).alertPriority = priority;
            ((MainActivity)getActivity()).alertId = selected;
            ((MainActivity)getActivity()).setViewPager(((MainActivity)getActivity()).SENDALERTFRAGMENT);
        }
    }
}
