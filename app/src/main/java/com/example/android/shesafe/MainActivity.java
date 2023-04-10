package com.example.android.shesafe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnSendMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        START INITIALIZE UI ELEMENTS
        btnSendMsg = (ImageButton) findViewById(R.id.btn_messages);

//        START SET LISTENERS
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSendMessageDialog();
            }
        });

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

    private void showSendMessageDialog() {
        SendMessageDialogFragment sendMessageDialogFragment = new SendMessageDialogFragment();
        sendMessageDialogFragment.show(getSupportFragmentManager(), "Send Emergency Message Dialog");
    }
}