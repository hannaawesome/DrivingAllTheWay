package com.libby.hanna.thecarslord.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.Date;
import java.util.List;

import static com.libby.hanna.thecarslord.controller.MainActivity.thisLoca;

/**
 * contains all the available trips, filter by city or distance, start and finish trips
 */
public class FirstFragment extends Fragment {
    View view;
    private RecyclerView tripsRecycleView;
    private List<Trip> availTripList;
    public static Spinner filterFirstChoice;
    private DB_manager be;
    private EditText filterText;
    private ATripAdapter adapter;
    private AppCompatButton changeFilter;
    private Driver registeredDriver;


    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        be = DBManagerFactory.GetFactory();
        view = inflater.inflate(R.layout.fragment_first, container, false);
        filterFirstChoice = (Spinner) view.findViewById(R.id.filter1);
        changeFilter = (AppCompatButton) view.findViewById(R.id.filterButton);
        tripsRecycleView = view.findViewById(R.id.firstRecycleView);
        tripsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        filterFirstChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position, long id) {
                if (!filterFirstChoice.getSelectedItem().toString().equals("all")) {
                    FilterDialog();
                    changeFilter.setEnabled(true);
                } else {
                    adapter.getFilter().filter("whatever");
                    changeFilter.setEnabled(false);
                }
            }

            public void onNothingSelected(AdapterView arg0) {
                filterFirstChoice.setSelection(0);
            }
        });
        changeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog();
            }
        });
        //set the reccylerview adapter
        if (tripsRecycleView.getAdapter() == null) {
            availTripList = be.getNotHandeledTrips();
            while (registeredDriver == null)
                registeredDriver = be.loadDataOnCurrentDriver(getActivity().getBaseContext());
            adapter = new ATripAdapter(tripsRecycleView, availTripList, registeredDriver, getActivity());
            tripsRecycleView.setAdapter(adapter);
        } else tripsRecycleView.getAdapter().notifyDataSetChanged();
        return view;
    }

    /**
     * creates dialog to enter the required filter
     */
    private void FilterDialog() {
        AlertDialog.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        adapter.getFilter().filter(filterText.getText().toString());
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        alertDialogBuilder.setTitle("Filter");
        if (filterFirstChoice.getSelectedItem().toString().equals("by city"))
            alertDialogBuilder.setMessage("Enter City:");
        else
            alertDialogBuilder.setMessage("Enter Distance in kilometers:");
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.filter_dialog_layout, null);
        alertDialogBuilder.setView(dialogView.findViewById(R.id.dialog_leyout));
        filterText = (EditText) dialogView.findViewById(R.id.filterEditText);
        if (filterFirstChoice.getSelectedItem().toString().equals("by city"))
            filterText.setHint("e.g. Jerusalem");
        else
            filterText.setHint("e.g. 10");
        alertDialogBuilder.setPositiveButton("Ok", onClickListener);
        alertDialogBuilder.setNegativeButton("Cancel ", onClickListener);
        alertDialogBuilder.show();

    }

    /**
     * the recyclerview adapter for the first fragment, works like expandable-recyclerview
     */
    private class ATripAdapter extends RecyclerView.Adapter<ATripAdapter.ViewHolder> implements Filterable {
        private DB_manager be;
        private RecyclerView recyclerView;
        private List<Trip> tripList;
        private List<Trip> origTripList;
        private Filter tripFilter;
        private String strFilterText;
        Activity a;
        private AppCompatButton smsConfirm;
        private AppCompatButton emailConfirm;
        private AppCompatButton phoneConfirm;
        Driver theDriver;
        private ArrayList<Integer> counter;//for the collapse and expand


        public ATripAdapter(RecyclerView recyclerView, List<Trip> t, Driver d, Activity c) {
            this.recyclerView = recyclerView;
            this.tripList = t;
            this.origTripList = new ArrayList<Trip>(tripList);
            be = DBManagerFactory.GetFactory();
            this.a = c;
            theDriver = d;
            counter = new ArrayList<Integer>();
            for (int i = 0; i < tripList.size(); i++)
                counter.add(0);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trip_view_holder, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.destination.setText(tripList.get(position).getDestination());
            if (FirstFragment.filterFirstChoice.getSelectedItem().toString().equals("all") || FirstFragment.filterFirstChoice.getSelectedItem().toString() == null)
                holder.theFilter.setText(tripList.get(position).getSource());
            else
                holder.theFilter.setText(strFilterText);
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

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView email;
            private TextView phone;
            private TextView name;
            private TextView from;
            private TextView to;
            private TextView start;
            private TextView finish;
            private AppCompatButton driveNow;
            private AppCompatButton finishTrip;
            Trip theTrip;
            private TextView destination;
            private TextView theFilter;
            private CardView cardView;
            View innerView;
            View title;

            public ViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                destination = (TextView) itemView.findViewById(R.id.destinationTextView);
                theFilter = (TextView) itemView.findViewById(R.id.chosenFilterTextView);
                title = itemView.findViewById(R.id.titleLayout);
                innerView = itemView.findViewById(R.id.allDetails);
                getViews(itemView);
                driveNow.setOnClickListener(this);
                finishTrip.setOnClickListener(this);

            }

            public void getViews(View itemView) {
                name = (TextView) itemView.findViewById(R.id.passengerNameTextView);
                from = (TextView) itemView.findViewById(R.id.sourceExTextView);
                to = (TextView) itemView.findViewById(R.id.destinationExTextView);
                email = (TextView) itemView.findViewById(R.id.emailTextView);
                phone = (TextView) itemView.findViewById(R.id.phoneTextView);
                start = (TextView) itemView.findViewById(R.id.startTimeTextView);
                finish = (TextView) itemView.findViewById(R.id.endTimeTextView);
                driveNow = (AppCompatButton) itemView.findViewById(R.id.confirmButton);
                finishTrip = (AppCompatButton) itemView.findViewById(R.id.doneButton);
            }

            public void bind() {
                int position = getAdapterPosition();
                theTrip = tripList.get(position);
                name.setText(theTrip.getName());
                from.setText(theTrip.getSource());
                to.setText(theTrip.getDestination());
                email.setText(theTrip.getEmailAddress());
                phone.setText(theTrip.getPhoneNumber());
                start.setText(theTrip.getStart().toString());
                if (theTrip.getState().equals(Trip.TripState.finished))
                    finish.setText(theTrip.getFinish().toString());
                else
                    finish.setText(R.string.finishTime);
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.confirmButton:
                        confirmDialog();
                        break;
                    case R.id.doneButton:
                        Date d = new Date();
                        Time time = new Time(d.getTime());
                        be.changeFinish(theTrip, theDriver, time, new DB_manager.Action<Void>() {
                            @Override
                            public void onSuccess(Void d) {
                                Toast.makeText(a.getBaseContext(), "The trip has been finished successfully!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Toast.makeText(a.getBaseContext(), "Could not update the data, must be something wrong \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        tripList.remove(getAdapterPosition());
                        recyclerView.getAdapter().notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }

            /**
             * creates dialog to choose which way to confirm starting drive
             */
            private void confirmDialog() {
                AlertDialog.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case AlertDialog.BUTTON_NEGATIVE:
                                dialog.cancel();
                                break;
                        }
                    }
                };
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a, R.style.MyDialogTheme);
                alertDialogBuilder.setTitle("Confirm by:");
                // Get the layout inflater
                LayoutInflater inflater = LayoutInflater.from(a.getBaseContext());
                final View dialogView = inflater.inflate(R.layout.confirmation_dialog_layout, null);
                alertDialogBuilder.setView(dialogView.findViewById(R.id.buttonLayout));
                smsConfirm = dialogView.findViewById(R.id.bySMS);
                phoneConfirm = dialogView.findViewById(R.id.byPhoneCall);
                emailConfirm = dialogView.findViewById(R.id.byEmail);
                alertDialogBuilder.setNegativeButton("Cancel ", onClickListener);
                final AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();

                smsConfirm.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        try {
                            if (ContextCompat.checkSelfPermission(a.getBaseContext(),
                                    Manifest.permission.SEND_SMS)
                                    != PackageManager.PERMISSION_GRANTED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(a.getParent(),
                                        Manifest.permission.SEND_SMS)) {
                                } else {
                                    a.requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 5);
                                }
                            }
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(theTrip.getPhoneNumber(), theDriver.getPhoneNumber(), "A driver is ready for your trip!", null, null);
                            changeNow();
                            dialog.cancel();
                        } catch (Exception ex) {
                            Toast.makeText(a.getBaseContext(), "Could not send sms, must be something wrong", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                phoneConfirm.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        try {
                            dialContactPhone(theTrip.getPhoneNumber());
                            changeNow();
                            dialog.cancel();
                        } catch (Exception ex) {
                            Toast.makeText(a.getBaseContext(), "Could not make a call, must be something wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                emailConfirm.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        try {
                            sendEmail(theTrip.getEmailAddress(), theDriver.getFirstName() + " " + theDriver.getLastName());
                            changeNow();
                            dialog.cancel();
                        } catch (Exception ex) {
                            Toast.makeText(a.getBaseContext(), "Could not send email, must be something wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            /**
             * @param phoneNumber to confirm trip
             */
            @RequiresApi(api = Build.VERSION_CODES.M)
            private void dialContactPhone(final String phoneNumber) {
                if (ActivityCompat.checkSelfPermission(a.getBaseContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    a.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 5);
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                a.startActivity(callIntent);
            }

            /**
             * @param theEmail   to confirm trip
             * @param driverName that will be automatically written in the email
             */
            @RequiresApi(api = Build.VERSION_CODES.M)
            private void sendEmail(final String theEmail, final String driverName) {
                if (ActivityCompat.checkSelfPermission(a.getBaseContext(),
                        Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    a.requestPermissions(new String[]{Manifest.permission.INTERNET}, 5);
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{theEmail});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Trip Status");
                intent.putExtra(Intent.EXTRA_TEXT, "Your trip had been chosen by " + driverName + "!");
                try {
                    FirstFragment.this.startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                } catch (android.content.ActivityNotFoundException exception) {
                    Toast.makeText(a.getBaseContext(), "No email clients installed on device!", Toast.LENGTH_LONG).show();
                }
            }

            private void changeNow() {
                be.changeNow(theTrip, theDriver, new DB_manager.Action<Void>() {
                    @Override
                    public void onSuccess(Void d) {
                        Toast.makeText(a.getBaseContext(), "The trip is now in process!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(a.getBaseContext(), "Could not update the data, must be something wrong \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                tripList.remove(getAdapterPosition());
                recyclerView.getAdapter().notifyDataSetChanged();
            }

        }

        @Override
        public Filter getFilter() {
            if (tripFilter == null)
                tripFilter = new TripFilter();
            return tripFilter;
        }

        /**
         * filter by city or distance
         */
        private class TripFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                strFilterText = constraint.toString();
                FilterResults results = new FilterResults();
                List<Trip> filteredList;
                // We implement here the filter logic
                if ((String) constraint == null || constraint.length() == 0 || FirstFragment.filterFirstChoice.getSelectedItem().toString().equals("all")) {
                    // No filter implemented we return all the list
                    filteredList = new ArrayList<Trip>(origTripList);
                }
                // We perform filtering operation
                else {
                    if (FirstFragment.filterFirstChoice.getSelectedItem().toString().equals("by city")) {
                        filteredList = be.getNotHandeledTripsInCity(constraint.toString(), a.getBaseContext());
                    } else {
                        while (thisLoca == null) ;//makes sure the location was found
                        filteredList = be.getNotHandeledTripsInDistance(Integer.parseInt(constraint.toString()), a, thisLoca);
                    }
                }
                if (filteredList == null)
                    filteredList = new ArrayList<Trip>();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                tripList.removeAll(tripList);
                tripList.addAll((List<Trip>) results.values);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

        }
    }
}