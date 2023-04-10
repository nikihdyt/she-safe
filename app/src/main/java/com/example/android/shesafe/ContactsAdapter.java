package com.example.android.shesafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.WordViewHolder> {

    private final LinkedList<String[]> mContacts;
    private final LayoutInflater mInflater;

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView contactNameItemView;
        public final TextView contactNumberItemView;
        final ContactsAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views for the RecyclerView.
         */
        public WordViewHolder(View itemView, ContactsAdapter adapter) {
            super(itemView);
            contactNameItemView = (TextView) itemView.findViewById(R.id.contact_name);
            contactNumberItemView = (TextView) itemView.findViewById(R.id.contact_number);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // All we do here is prepend "Clicked! " to the text in the view, to verify that
            // the correct item was clicked. The underlying data does not change.
            contactNameItemView.setText ("Clicked! "+ contactNameItemView.getText());
        }
    }

    public ContactsAdapter(Context context, LinkedList<String[]> contactList) {
        mInflater = LayoutInflater.from(context);
        this.mContacts = contactList;
    }

    /**
     * Inflates an item view and returns a new view holder that contains it.
     * Called when the RecyclerView needs a new view holder to represent an item.
     *
     * @param parent The view group that holds the item views.
     * @param viewType Used to distinguish views, if more than one type of item view is used.
     * @return a view holder.
     */
    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(R.layout.item_contact_list, parent, false);
        return new WordViewHolder(mItemView, this);
    }

    /**
     * Sets the contents of an item at a given position in the RecyclerView.
     * Called by RecyclerView to display the data at a specificed position.
     *
     * @param holder The view holder for that position in the RecyclerView.
     * @param position The position of the item in the RecycerView.
     */
    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        // Retrieve the data for that position.
        String[] mCurrent = mContacts.get(position);
        // Add the data to the view holder.
        holder.contactNameItemView.setText(mCurrent[0]);
        holder.contactNumberItemView.setText(mCurrent[1]);
    }

    /**
     * Returns the size of the container that holds the data.
     *
     * @return Size of the list of data.
     */
    @Override
    public int getItemCount() {
        return mContacts.size();
    }
}
