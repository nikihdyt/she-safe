package com.example.android.shesafe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.LinkedList;

public class HostFragmentActivity extends AppCompatActivity implements SensorEventListener {

    private LinkedList<String[]> mContactList = new LinkedList<>();

    private SensorManager mSensorManager;
    private Sensor mSensorLight;
    private CardView screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_fragment);

//        INITIALIZING UI ELEMENTS
        screen = (CardView) findViewById(R.id.screen);

        // Enable the up button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String fragmentName = intent.getStringExtra("fragment");

        Fragment fragment;
        switch (fragmentName) {
            case "contactsFragment":
                fragment = new ContactsFragment();
                break;
            case "aboutUsFragment":
                fragment = new AboutUsFragment();
                break;
            default:
                throw new IllegalArgumentException("Invalid fragment name: " + fragmentName);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

//        START HANDLE SENSOR
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        END HANDLE SENSOR

    }

    // finish the activity when the up button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorLight != null) {
            mSensorManager.registerListener(this, mSensorLight,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightLevel = event.values[0];
            float alpha = 1 - (lightLevel / 50000f);
            alpha = Math.max(alpha, 0f);
            alpha = Math.min(alpha, 0.8f);
            screen.setAlpha(alpha);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}