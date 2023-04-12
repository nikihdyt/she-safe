package com.example.android.shesafe;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.LinkedList;

public class ContactsFragment extends Fragment {

    private final LinkedList<String[]> mContactList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private ContactsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Set AppBar title
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Emergency Contacts");

        // Get the RecyclerView for contact list and set its adapter and layout manager
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_contact_list);
        mAdapter = new ContactsAdapter(this.getContext(), mContactList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // Button handler for adding a contact
        ExtendedFloatingActionButton btnAddContact = (ExtendedFloatingActionButton) rootView.findViewById(R.id.btn_add_contact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
            }
        });

        return rootView;
    }

    /**
     * Add a contact to the contact list.
     * Show an AlertDialog with an EditText to enter the contact's name and phone number.
     * When the user clicks the Send button, add the contact to the contact list.
     */
    private void addContact() {
        int contactListSize = mContactList.size();

        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        // Set the title and message for the dialog
        builder.setTitle("Add new contact");

        // Create EditText fields that wrapped in a LinearLayout
        LinearLayout layout = new LinearLayout(this.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final EditText inputName = new EditText(this.getContext());
        inputName.setHint("Name");
        layout.addView(inputName);

        final EditText inputNumber = new EditText(this.getContext());
        inputNumber.setHint("Phone Number");
        layout.addView(inputNumber);

        builder.setView(layout);

        // Add the Send button to the dialog
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String addedContactName = inputName.getText().toString();
                String addedContactNumber = inputNumber.getText().toString();

                String[] addedContact = {addedContactName, addedContactNumber};
                mContactList.addLast(addedContact);
                Log.d(ContactsFragment.class.getSimpleName(), "contactList TERBARU setelah add new contact" + mContactList);

                // Notify the adapter, that the data has changed so it can
                // update the RecyclerView to display the data.
                mRecyclerView.getAdapter().notifyItemInserted(contactListSize);
                // Scroll to the bottom.
                mRecyclerView.smoothScrollToPosition(contactListSize);
            }
        });

        // Add the Cancel button to the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }

    public LinkedList<String[]> getContactList() {
        Log.e(ContactsFragment.class.getSimpleName(), "contactList ADA" + mContactList);
        return mContactList;
    }
}