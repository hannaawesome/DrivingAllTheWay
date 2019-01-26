package com.libby.hanna.thecarslord.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.OvershootInterpolator;
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

import net.cachapa.expandablelayout.ExpandableLayout;

public class FirstFragment extends Fragment {
    View view;
    private RecyclerView tripsRecycleView;
    private List<Trip> availTripList;
    private List<Driver> driverList;
    private Spinner filterFirstChoice;
    private DB_manager be;
    private EditText filterText;
    private ATripAdapter adapter;
    private AppCompatButton changeFilter;
    private Driver registeredDriver;


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
                    adapter.getFilter().filter("");
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
        if (tripsRecycleView.getAdapter() == null) {
            availTripList = be.getNotHandeledTrips();
            registeredDriver = be.loadDataOnCurrentDriver(getActivity().getBaseContext());
            adapter = new ATripAdapter(tripsRecycleView, availTripList, registeredDriver, filterFirstChoice, getActivity());
            tripsRecycleView.setAdapter(adapter);
        } else tripsRecycleView.getAdapter().notifyDataSetChanged();
        return view;
    }

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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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


    private static class ATripAdapter extends RecyclerView.Adapter<ATripAdapter.ViewHolder> implements Filterable {
        private static final int UNSELECTED = -1;
        private DB_manager be;
        private RecyclerView recyclerView;
        private List<Trip> tripList;
        private List<Trip> origTripList;
        private Filter tripFilter;
        private int selectedItem = UNSELECTED;
        private String strFilterText;
        Spinner sChoice;
        String choice;
        Activity a;
        private AppCompatButton smsConfirm;
        private AppCompatButton emailConfirm;
        private AppCompatButton phoneConfirm;
        Driver theDriver;

        public ATripAdapter(RecyclerView recyclerView, List<Trip> t, Driver d, Spinner sChoice, Activity c) {
            this.recyclerView = recyclerView;
            this.sChoice = sChoice;
            this.choice = this.sChoice.getSelectedItem().toString();
            this.tripList = t;
            this.origTripList = tripList;
            be = DBManagerFactory.GetFactory();
            this.a = c;
            theDriver = d;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trip_view_holder, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return tripList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
            private ExpandableLayout expandableLayout;
            private AppCompatButton expandButton;
            private TextView destination;
            private TextView theFilter;
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


            public ViewHolder(View itemView) {
                super(itemView);
                getViews(itemView);
                expandableLayout.setInterpolator(new OvershootInterpolator());
                expandableLayout.setOnExpansionUpdateListener(this);
                expandButton.setOnClickListener(this);
                driveNow.setOnClickListener(this);
                finishTrip.setOnClickListener(this);
            }

            public void getViews(View itemView) {
                destination = (TextView) itemView.findViewById(R.id.destinationTextView);
                theFilter = (TextView) itemView.findViewById(R.id.chosenFilterTextView);
                name = (TextView) itemView.findViewById(R.id.passengerNameTextView);
                from = (TextView) itemView.findViewById(R.id.sourceExTextView);
                to = (TextView) itemView.findViewById(R.id.destinationExTextView);
                email = (TextView) itemView.findViewById(R.id.emailTextView);
                phone = (TextView) itemView.findViewById(R.id.phoneTextView);
                start = (TextView) itemView.findViewById(R.id.startTimeTextView);
                finish = (TextView) itemView.findViewById(R.id.endTimeTextView);
                driveNow = (AppCompatButton) itemView.findViewById(R.id.confirmButton);
                finishTrip = (AppCompatButton) itemView.findViewById(R.id.doneButton);
                expandableLayout = itemView.findViewById(R.id.expandable_layout);
                expandButton = itemView.findViewById(R.id.expand_button);
            }

            public void bind() {
                int position = getAdapterPosition();
                boolean isSelected = position == selectedItem;
                expandButton.setSelected(isSelected);
                expandableLayout.setExpanded(isSelected, false);
                theTrip = tripList.get(position);
                destination.setText(theTrip.getDestination());
                choice = sChoice.getSelectedItem().toString();
                if (choice.equals("all") || choice == null)
                    theFilter.setText(theTrip.getSource());
                else
                    theFilter.setText(strFilterText);
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
                    case R.id.expand_button:
                        ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
                        if (holder != null) {
                            holder.expandButton.setSelected(false);
                            holder.expandableLayout.collapse();
                        }
                        int position = getAdapterPosition();
                        if (position == selectedItem) {
                            selectedItem = UNSELECTED;
                        } else {
                            expandButton.setSelected(true);
                            expandableLayout.expand();
                            selectedItem = position;
                        }
                        break;
                    case R.id.confirmButton:
                        confirmDialog();
                        Toast.makeText(a.getBaseContext(), "Could not send sms, must be something wrong", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.doneButton:
                        Date d = new Date();
                        Time time = new Time(d.getTime());
                        be.changeFinish(theTrip, Trip.TripState.finished, time, new DB_manager.Action<Void>() {
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

            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d("ExpandableLayout", "State: " + state);
                if (state == ExpandableLayout.State.EXPANDING) {
                    recyclerView.smoothScrollToPosition(getAdapterPosition());
                }
            }

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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
                alertDialogBuilder.setTitle("Confirm by:");
                // Get the layout inflater
                LayoutInflater inflater = LayoutInflater.from(a.getBaseContext());
                View dialogView = inflater.inflate(R.layout.confirmation_dialog_layout, null);
                alertDialogBuilder.setView(dialogView.findViewById(R.id.buttonLayout));
                smsConfirm = dialogView.findViewById(R.id.bySMS);
                phoneConfirm = dialogView.findViewById(R.id.byPhoneCall);
                emailConfirm = dialogView.findViewById(R.id.byEmail);
                alertDialogBuilder.setNegativeButton("Cancel ", onClickListener);
                alertDialogBuilder.show();

                smsConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(theTrip.getPhoneNumber(), null, "A driver is ready for your trip!", null, null);
                            changeNow();

                        } catch (Exception ex) {
                            Toast.makeText(a.getBaseContext(), "Could not send sms, must be something wrong", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                phoneConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            dialContactPhone(theTrip.getPhoneNumber());
                            changeNow();
                        } catch (Exception ex) {
                            Toast.makeText(a.getBaseContext(), "Could not make a call, must be something wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                emailConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            sendEmail(theTrip.getEmailAddress(), theDriver.getEmailAddress());
                            changeNow();
                        } catch (Exception ex) {
                            Toast.makeText(a.getBaseContext(), "Could not send email, must be something wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            private void dialContactPhone(final String phoneNumber) {
                a.getBaseContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
            }

            private void changeNow() {
                be.changeNow(theTrip, theDriver, Trip.TripState.inProcess, new DB_manager.Action<Void>() {
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

            private void sendEmail(final String theEmail, final String driverName) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", theEmail, null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Trip Status");
                intent.putExtra(Intent.EXTRA_TEXT, "Your trip had been chosen by " + driverName + "!");
                a.getBaseContext().startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        }

        @Override
        public Filter getFilter() {
            if (tripFilter == null)
                tripFilter = new TripFilter();
            return tripFilter;
        }

        private class TripFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                strFilterText = constraint.toString();
                FilterResults results = new FilterResults();
                List<Trip> filteredList;
                // We implement here the filter logic
                choice = sChoice.getSelectedItem().toString();
                if (constraint == null || constraint.length() == 0 || choice.equals("all")) {
                    // No filter implemented we return all the list
                    filteredList = origTripList;
                }
                // We perform filtering operation
                else {
                    if (choice.equals("by city")) {
                        filteredList = be.getNotHandeledTripsInCity(constraint.toString(), a.getBaseContext());
                    } else {
                        filteredList = be.getNotHandeledTripsInDistance(Integer.parseInt(constraint.toString()), a);
                    }
                }
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
/*

        recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);
//fetch data and on ExpandableRecyclerAdapter
        recyclerView.setAdapter(new ExpandableRecyclerAdapter(availTripList));

        ExpandableRecyclerViewAdapter expandableCategoryRecyclerViewAdapter =
                new ExpandableRecyclerViewAdapter(getApplicationContext(), parentList,
                        childListHolder);



        expanderRecyclerView.setAdapter(expandableCategoryRecyclerViewAdapter);
        if (tripsRecycleView.getAdapter() == null) {
                    availTripList = be.getNotHandeledTrips();
                    tripsRecycleView.setAdapter(new FirstFragment.TripsRecyclerViewAdapter());
                } else tripsRecycleView.getAdapter().notifyDataSetChanged();
        return view;
    }
    public class TripExpandableRecyclerViewAdapter extends RecyclerView.Adapter<TripExpandableRecyclerViewAdapter.ViewHolder> {

        ArrayList<String> nameList = new ArrayList<String>();
        ArrayList<String> image = new ArrayList<String>();
        ArrayList<Integer> counter = new ArrayList<Integer>();
        ArrayList<ArrayList> itemNameList = new ArrayList<ArrayList>();
        Context context;

        public TripExpandableRecyclerViewAdapter(Context context,
                                             ArrayList<String> nameList,
                                             ArrayList<ArrayList> itemNameList) {
            this.nameList = nameList;
            this.itemNameList = itemNameList;
            this.context = context;

            Log.d("namelist", nameList.toString());

            for (int i = 0; i < nameList.size(); i++) {
                counter.add(0);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageButton dropBtn;
            RecyclerView cardRecyclerView;
            CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.categoryTitle);
                dropBtn = itemView.findViewById(R.id.categoryExpandBtn);
                cardRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
                cardView = itemView.findViewById(R.id.cardView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_collapseview, parent, false);

            ExpandableRecyclerViewAdapter.ViewHolder vh = new ExpandableRecyclerViewAdapter.ViewHolder(v);

            return vh;

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(nameList.get(position));

            InnerRecyclerViewAdapter itemInnerRecyclerView = new InnerRecyclerViewAdapter(itemNameList.get(position));


            holder.cardRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (counter.get(position) % 2 == 0) {
                        holder.cardRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        holder.cardRecyclerView.setVisibility(View.GONE);
                    }

                    counter.set(position, counter.get(position) + 1);


                }
            });
            holder.cardRecyclerView.setAdapter(itemInnerRecyclerView);

        }

        @Override
        public int getItemCount() {
            return availTripList.size();
        }
    }

    public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ViewHolder> {

        private List<Repo> repos;
        private SparseBooleanArray expandState = new SparseBooleanArray();
        private Context context;

        public ExpandableRecyclerAdapter(List<Repo> repos) {
            this.repos = repos;
            //set initial expanded state to false
            for (int i = 0; i < repos.size(); i++) {
                expandState.append(i, false);
            }
        }

        @Override
        public ExpandableRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            this.context = viewGroup.getContext();
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expandable_card_row, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ExpandableRecyclerAdapter.ViewHolder viewHolder, final  int i) {

            viewHolder.setIsRecyclable(false);

            viewHolder.tvName.setText(repos.get(i).getName());

            viewHolder.tvOwnerLogin.setText("Owner: " +repos.get(i).getOwner().getLogin());
            viewHolder.tvOwnerUrl.setText(repos.get(i).getOwner().getUrl());

            Picasso.with(context)
                    .load(repos.get(i).getOwner().getImageUrl())
                    .resize(500, 500)
                    .centerCrop()
                    .into(viewHolder.ivOwner);

            //check if view is expanded
            final boolean isExpanded = expandState.get(i);
            viewHolder.expandableLayout.setVisibility(isExpanded?View.VISIBLE:View.GONE);

            viewHolder.buttonLayout.setRotation(expandState.get(i) ? 180f : 0f);
            viewHolder.buttonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onClickButton(viewHolder.expandableLayout, viewHolder.buttonLayout,  i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return repos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            private TextView tvName,tvOwnerLogin, tvOwnerUrl;
            private ImageView ivOwner;
            public RelativeLayout buttonLayout;
            public LinearLayout expandableLayout;

            public ViewHolder(View view) {
                super(view);

                tvName = (TextView)view.findViewById(R.id.textView_name);
                tvId = (TextView)view.findViewById(R.id.textView_id);
                tvUrl = (TextView)view.findViewById(R.id.textView_url);
                tvOwnerLogin = (TextView)view.findViewById(R.id.textView_Owner);
                tvOwnerUrl = (TextView)view.findViewById(R.id.textView_OwnerUrl);
                ivOwner = (ImageView) view.findViewById(R.id.imageView_Owner);

                buttonLayout = (RelativeLayout) view.findViewById(R.id.button);
                expandableLayout = (LinearLayout) view.findViewById(R.id.expandableLayout);
            }
        }

        private void onClickButton(final LinearLayout expandableLayout, final RelativeLayout buttonLayout, final  int i) {

            //Simply set View to Gone if not expanded
            //Not necessary but I put simple rotation on button layout
            if (expandableLayout.getVisibility() == View.VISIBLE){
                createRotateAnimator(buttonLayout, 180f, 0f).start();
                expandableLayout.setVisibility(View.GONE);
                expandState.put(i, false);
            }else{
                createRotateAnimator(buttonLayout, 0f, 180f).start();
                expandableLayout.setVisibility(View.VISIBLE);
                expandState.put(i, true);
            }
        }

        //Code to rotate button
        private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
            animator.setDuration(300);
            animator.setInterpolator(new LinearInterpolator());
            return animator;
        }
    }
    public class TripsRecyclerViewAdapter extends RecyclerView.Adapter<TripsRecyclerViewAdapter.TripViewHolder> {

        @Override
        public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.trip_view_holder, parent, false);
            return new TripViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TripViewHolder holder, int position) {
            Trip t = availTripList.get(position);
            holder.idTextView.setText(t.get_id());

            final boolean isExpanded = position==mExpandedPosition;
            holder.details.setVisibility(isExpanded?View.VISIBLE:View.GONE);
            holder.itemView.setActivated(isExpanded);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpandedPosition = isExpanded ? -1:position;
                    TransitionManager.beginDelayedTransition(recyclerView);
                    notifyDataSetChanged();
                }
            });
        }



        public class TripViewHolder extends RecyclerView.ViewHolder {
            TextView idTextView;

            TripViewHolder(View itemView) {
                super(itemView);
                idTextView = itemView.findViewById(R.id.idTextView);
            }
        }
/*
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    return null;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                }
            };
        }*/
}
