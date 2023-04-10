package com.example.android.shesafe;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class SendMessageDialogFragment extends DialogFragment {

    private MapView mMapView;
    private GoogleMap mMap;
    private Button btnSendMsg;
    private LatLng currentLocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_send_message, container, false);

        mMapView = rootView.findViewById(R.id.map_view);
        btnSendMsg = rootView.findViewById(R.id.btn_send_message);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Add a marker at the user's current location
                currentLocation = new LatLng(-7.7730428, 110.3739477);
                mMap.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20));
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "1234567890";
                String message = "Please help me, I'm in danger! My location is: " + + currentLocation.latitude + ", " + currentLocation.longitude ;

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request the permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.SEND_SMS},
                            1);
                } else {
                    // Permission is granted, send the SMS message
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    Toast.makeText(getActivity(), "SMS sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the SMS message
                Toast.makeText(getActivity(), "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}