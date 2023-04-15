package com.example.android.shesafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private ImageButton btnSendMsg;
    private ImageButton btnCall;
    private SwitchMaterial sBtnFullProtectionMode;
    private boolean isFullProtectionModeOn = false;

    private String contactName;
    private String contactNumber;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude = -7.7730428;
    private double longitude = 110.3739477;

    private SensorManager mSensorManager;
    private Sensor mSensorLight;
    private CardView screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        START INITIALIZE UI ELEMENTS
        btnSendMsg = (ImageButton) findViewById(R.id.btn_messages);
        btnCall = (ImageButton) findViewById(R.id.btn_call);
        sBtnFullProtectionMode = (SwitchMaterial) findViewById(R.id.switch_high_risk_danger_mode);
        screen = (CardView) findViewById(R.id.screen);
//        END INITIALIZE UI ELEMENTS

//        START HANDLE UI ELEMENTS
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSendMessageDialog();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPolice(getResources().getString(R.string.police_number));
            }
        });
        sBtnFullProtectionMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    View view = getLayoutInflater().inflate(R.layout.dialog_turn_on_full_protection_mode, null);
                    showAlertDialog(
                            getResources().getString(R.string.are_you_sure_you_want_to_turn_on_full_protection_mode),
                            view );
                    startFullProtectionMode();
                } else {
                    showAlertDialog(
                            getResources().getString(R.string.you_sure_you_want_to_turn_off_full_protection_mode),
                            null);
                    stopFullProtectionMode();
                }
            }
        });
//        END HANDLE UI ELEMENTS

//        START HANDLE SENSOR
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        END HANDLE SENSOR

//        START HANDLE CONTACT DATA
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        contactName = sharedPreferences.getString("contactName", "");
        contactNumber = sharedPreferences.getString("contactNumber", "");
        Log.d("MainActivity", "contactName: " + contactName + ", contactNumber: " + contactNumber);
//        END HANDLE CONTACT DATA
    }

    /** Starts the full protection mode of the app when called.
     It uses the ShakeDetector library to detect when the phone is shaken.
     When the phone is shaken, it sends an SMS message to a pre-defined phone number with the user's location.
     If the app doesn't have permission to send SMS messages, it requests the permission.
     */
    private void startFullProtectionMode() {
        ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                };
                if (contactNumber != null) {
                    String message =
                            getResources().getString(R.string.in_danger_sms_message)
                                    + latitude + ", " + longitude;
                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request the permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    } else {
                        // Permission is granted, send the SMS message
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(contactNumber, null, message, null, null);
                        Toast.makeText(MainActivity.this,
                                getResources().getString(R.string.sms_sent_to) + contactName,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            getResources().getString(R.string.no_contact_selected),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (isFullProtectionModeOn) {
            ShakeDetector.start();
        }
    }

    private void stopFullProtectionMode() {
        ShakeDetector.stop();
        ShakeDetector.destroy();
    }

    private void callPolice(String phoneNumber) {
        // Format the phone number for dialing.
        String formattedNumber = String.format("tel: %s", phoneNumber);
        // Create the intent.
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        // Set the formatted phone number as data for the intent.
        dialIntent.setData(Uri.parse(formattedNumber));
        // If package resolves to an app, send intent.
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        } else {
            Log.e(MainActivity.class.getSimpleName(), "Can't resolve app for ACTION_DIAL Intent.");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_contacts:
                launchMenuOption("contactsFragment");
                return true;
            case R.id.action_language:
                Intent languageIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(languageIntent);
                return true;
            case R.id.menu_about_us:
                launchMenuOption("aboutUsFragment");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Launches an activity that became the host of a fragment.
     * The fragment is determinet by the menu option that was selected.
     *
     * @param fragmentName The name of the fragment to launch.
     */
    private void launchMenuOption(String fragmentName) {
        Intent intent = new Intent(this, HostFragmentActivity.class);
        intent.putExtra("fragment", fragmentName);
        startActivity(intent);
    }

    /** Will be shown after user click send message button
     * */
    private void showSendMessageDialog() {
        SendMessageDialogFragment sendMessageDialogFragment = new SendMessageDialogFragment();
        sendMessageDialogFragment.show(getSupportFragmentManager(), "Send Emergency Message Dialog");
    }

    /**
     * Show an AlertDialog that asks the user to confirm the action.
     *
     * @param message The message to display in the AlertDialog.
     * @param view The View object to display in the AlertDialog, or null if no view is needed.
     */
    private void showAlertDialog(@NonNull String message, @Nullable View view) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message)
                .setView(view)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (sBtnFullProtectionMode.isChecked()) {
                            sBtnFullProtectionMode.setChecked(false);
                        } else {
                            sBtnFullProtectionMode.setChecked(true);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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