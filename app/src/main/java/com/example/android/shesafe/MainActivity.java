package com.example.android.shesafe;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
//                IF USE ACTIVITY, NOT FRAGMENT
//                Intent intent = new Intent(this, ContactsActivity.class);
//                startActivity(intent);
//                return true;
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
}