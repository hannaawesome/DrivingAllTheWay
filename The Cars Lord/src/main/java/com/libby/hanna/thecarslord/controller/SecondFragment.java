package com.libby.hanna.thecarslord.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.backend.DBManagerFactory;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.entities.Driver;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * contains all the driver's trips, sort by distance, add to contacts
 */
public class SecondFragment extends Fragment {
    View view;
    private RecyclerView tripsRecycleView;
    private List<Trip> tripByDriver;
    public Spinner filterFirstChoice;
    private DB_manager be;
    private ATripAdapter adapter;
    private Driver registeredDriver;

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        be = DBManagerFactory.GetFactory();
        view = inflater.inflate(R.layout.fragment_second, container, false);
        filterFirstChoice = (Spinner) view.findViewById(R.id.filter2);
        filterFirstChoice.setSelection(0);
        filterFirstChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position, long id) {
                sortTheList();
                adapter.notifyDataSetChanged();
            }

            public void onNothingSelected(AdapterView arg0) {
                filterFirstChoice.setSelection(0);
            }
        });
        //set the reccylerview adapter
        tripsRecycleView = view.findViewById(R.id.secondRecycleView);
        tripsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        if (tripsRecycleView.getAdapter() == null) {
            registeredDriver = be.loadDataOnCurrentDriver(getActivity().getBaseContext());
            tripByDriver = be.getSpecificDriverTrips(registeredDriver.get_id());
            adapter = new ATripAdapter(tripByDriver, registeredDriver, getActivity());
            tripsRecycleView.setAdapter(adapter);
        } else tripsRecycleView.getAdapter().notifyDataSetChanged();
        return view;
    }

    /**
     * if chose to sort by distance, then it will be sorted by distance
     */
    private void sortTheList() {
        if (filterFirstChoice.getSelectedItem().toString().equals("by distance"))
            Collections.sort(tripByDriver, new Comparator<Trip>() {
                @Override
                public int compare(Trip lhs, Trip rhs) {
                    return be.distanceCalc(lhs, getActivity().getBaseContext()) < be.distanceCalc(rhs, getActivity().getBaseContext()) ? -1 : (be.distanceCalc(lhs, getActivity().getBaseContext()) > be.distanceCalc(lhs, getActivity().getBaseContext())) ? 1 : 0;
                }
            });

    }

    /**
     * the recyclerview adapter for the second fragment, works like expandable-recyclerview
     */
    private static class ATripAdapter extends RecyclerView.Adapter<ATripAdapter.ViewHolder> {
        private List<Trip> tripList;
        Activity a;
        Driver theDriver;
        private ArrayList<Integer> counter;//for the collapse and expand
        private DB_manager be;

        public ATripAdapter(List<Trip> t, Driver d, Activity c) {
            this.tripList = t;
            this.a = c;
            theDriver = d;
            counter = new ArrayList<Integer>();
            for (int i = 0; i < tripList.size(); i++)
                counter.add(0);
            be = DBManagerFactory.GetFactory();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.second_view_holder, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            //the title of each card
            holder.destination.setText(tripList.get(position).getDestination());
            holder.source.setText(tripList.get(position).getSource());
            //collapse and expand
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (counter.get(position) % 2 == 0) {
                        holder.innerView.setVisibility(View.VISIBLE);
                        holder.title.setVisibility(View.INVISIBLE);
                    } else {
                        holder.innerView.setVisibility(View.GONE);
                        holder.title.setVisibility(View.VISIBLE);
                    }
                    counter.set(position, counter.get(position) + 1);
                }
            });
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return tripList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView email;
            private TextView phone;
            private TextView name;
            private TextView from;
            private TextView to;
            private TextView start;
            private TextView finish;
            private TextView inStatus;
            Trip theTrip;
            private TextView destination;
            private TextView source;
            private TextView status;
            private AppCompatButton addToContact;
            private CardView cardView;
            View innerView;
            View title;

            public ViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                destination = (TextView) itemView.findViewById(R.id.destinationTextView);
                source = (TextView) itemView.findViewById(R.id.chosenFilterTextView);
                status = (TextView) itemView.findViewById(R.id.status);
                title = itemView.findViewById(R.id.titleLayout);
                innerView = itemView.findViewById(R.id.allDetails);
                getViews(itemView);
                //when wants to add to own contacts
                addToContact.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        String DisplayName = theTrip.getName();
                        String MobileNumber = theTrip.getPhoneNumber();
                        String emailID = theTrip.getEmailAddress();
                        if (ActivityCompat.checkSelfPermission(a.getBaseContext(),
                                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(a.getBaseContext(),
                                Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                            a.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 5);

                        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                        ops.add(ContentProviderOperation.newInsert(
                                ContactsContract.RawContacts.CONTENT_URI)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                .build());

                        //------------------------------------------------------ Names
                        if (DisplayName != null) {
                            ops.add(ContentProviderOperation.newInsert(
                                    ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(
                                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                            DisplayName).build());
                        }

                        //------------------------------------------------------ Mobile Number
                        if (MobileNumber != null) {
                            ops.add(ContentProviderOperation.
                                    newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    .build());
                        }


                        //------------------------------------------------------ Email
                        if (emailID != null) {
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                    .build());
                        }
                        // Asking the Contact provider to create a new contact
                        try {
                            a.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                            Toast.makeText(a.getBaseContext(), "Contact added successfully!", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(a.getBaseContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }

            public void getViews(View itemView) {
                name = (TextView) itemView.findViewById(R.id.passengerNameTextView);
                from = (TextView) itemView.findViewById(R.id.sourceExTextView);
                to = (TextView) itemView.findViewById(R.id.destinationExTextView);
                email = (TextView) itemView.findViewById(R.id.emailTextView);
                phone = (TextView) itemView.findViewById(R.id.phoneTextView);
                start = (TextView) itemView.findViewById(R.id.startTimeTextView);
                finish = (TextView) itemView.findViewById(R.id.endTimeTextView);
                inStatus = (TextView) itemView.findViewById(R.id.status2);
                addToContact = (AppCompatButton) itemView.findViewById(R.id.addContact);
            }

            public void bind() {
                int position = getAdapterPosition();
                status.setText(theTrip.getState().toString());
                //the inside of the cards
                theTrip = tripList.get(position);
                name.setText(theTrip.getName());
                from.setText(theTrip.getSource());
                to.setText(theTrip.getDestination());
                email.setText(theTrip.getEmailAddress());
                phone.setText(theTrip.getPhoneNumber());
                start.setText(theTrip.getStart().toString());
                inStatus.setText(theTrip.getState().toString());
                if (theTrip.getState().equals(Trip.TripState.finished))
                    finish.setText(theTrip.getFinish().toString());
                else
                    finish.setText(R.string.finishTime);
            }


        }
    }
}