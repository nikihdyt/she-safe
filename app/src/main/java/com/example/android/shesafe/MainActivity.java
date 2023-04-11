package com.example.android.shesafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

public class MainActivity extends AppCompatActivity{

    private ImageButton btnSendMsg;
    private SwitchMaterial sBtnFullProtectionMode;
    private boolean isFullProtectionModeOn = false;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        START INITIALIZE UI ELEMENTS
        btnSendMsg = (ImageButton) findViewById(R.id.btn_messages);
        sBtnFullProtectionMode = (SwitchMaterial) findViewById(R.id.switch_high_risk_danger_mode);
//        END INITIALIZE UI ELEMENTS

//        START HANDLE UI ELEMENTS
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSendMessageDialog();
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
                String phoneNumber = "1234567890";
                String message =
                        getResources().getString(R.string.in_danger_sms_message)
                                + latitude + ", " + longitude;
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request the permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                } else {
                    // Permission is granted, send the SMS message
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    Toast.makeText(MainActivity.this,
                            getResources().getString(R.string.sms_sent_to) + phoneNumber,
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


}