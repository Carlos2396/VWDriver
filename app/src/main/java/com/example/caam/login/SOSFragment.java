package com.example.caam.login;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class SOSFragment extends Fragment {
ImageButton firefighterButton;
ImageButton policeButton;
ImageButton medicalButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sos, container, false);
        firefighterButton = (ImageButton)v.findViewById(R.id.firefighterBtn);
        policeButton = (ImageButton)v.findViewById(R.id.securityBtn);
        medicalButton = (ImageButton)v.findViewById(R.id.medicalBtn);

        firefighterButton.setOnClickListener(new EmergencyListener());
        policeButton.setOnClickListener(new EmergencyListener());
        medicalButton.setOnClickListener(new EmergencyListener());

        // Inflate the layout for this fragment
        return v;
    }

    private class EmergencyListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            switch(view.getId()){
                case R.id.firefighterBtn:
                    intent.setData(Uri.parse("tel:068"));
                    startActivity(intent);
                    break;
                case R.id.securityBtn:
                    intent.setData(Uri.parse("tel:060"));
                    startActivity(intent);
                    break;
                case R.id.medicalBtn:
                    intent.setData(Uri.parse("tel:065"));
                    startActivity(intent);
                    break;
            }
        }
    }
}
