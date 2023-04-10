package com.example.android.shesafe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.LinkedList;

public class HostFragmentActivity extends AppCompatActivity {

    private LinkedList<String[]> mContactList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_fragment);

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
            default:
                throw new IllegalArgumentException("Invalid fragment name: " + fragmentName);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        // TODO: retrieve the contact list from the fragment then store it to sharedPreference

//        GET contact list from ContactsFragment
//        ContactsFragment contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        LinkedList<String[]> contactList = contactsFragment.getContactList();
        // store the contact list in the sharedPreference
//        if (mContactList != null) {
//            storeLinkedList(mContactList);
//            Log.e("HostFragmentActivity", "contactList retrived from contactFragmnet: " + mContactList.toString());
//        } else {
//            Log.e("HostFragmentActivity", "contactList is null");
//        }

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

//    private void storeLinkedList(LinkedList<String[]> linkedList) {
//        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String linkedListJson = gson.toJson(linkedList);
//        editor.putString("linkedList", linkedListJson);
//        editor.apply();
//    }

}