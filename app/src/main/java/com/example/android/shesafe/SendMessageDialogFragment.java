package com.example.android.shesafe;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.util.Log;
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
import com.google.gson.Gson;

import android.Manifest;

public class SendMessageDialogFragment extends DialogFragment{

    private MapView mMapView;
    private GoogleMap mMap;
    private Button btnSendMsg;
    private LatLng currentLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private String provider;
    private double latitude;
    private double longitude;

    private String contactName;
    private String contactNumber;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_send_message, container, false);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, proceed with location access
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // If permission is granted, get the user's current location
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Use location data
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            });
        }

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
                Log.d("MainActivity", "onMapReady: OK");
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                currentLocation = new LatLng(-7.7730428, 110.3739477);
                mMap.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title(getResources().getString(R.string.current_location)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20));
            }
        });

//        START HANDLE CONTACT DATA
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        contactName = sharedPreferences.getString("contactName", "");
        contactNumber = sharedPreferences.getString("contactNumber", "");
        Log.d("MainActivity", "contactName: " + contactName + ", contactNumber: " + contactNumber);
//        END HANDLE CONTACT DATA

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactNumber != "") {
                    Log.d("MainActivity", "MASUK ONCLICK" + "contactName: " + contactName + ", contactNumber: " + contactNumber);
                    String message =
                            getResources().getString(R.string.in_danger_sms_message)
                                    + currentLocation.latitude + ", " + currentLocation.longitude ;
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.SEND_SMS},
                                1);
                    } else {
                        // Permission is granted, send the SMS message
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(contactNumber, null, message, null, null);
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.sms_sent_to)
                                        + " " + contactName, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.no_contact_selected),
                            Toast.LENGTH_SHORT).show();
                }


            }
        });

        return rootView;
    }

    // Handle the result of the permission request


}