package com.libby.hanna.thecarslord.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.Spinner;
import android.widget.Toast;

import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.backend.DBManagerFactory;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.datasource.Firebase_DBManager;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.util.List;

import net.cachapa.expandablelayout.ExpandableLayout;

public class FirstFragment extends Fragment {
    View view;
    //expandablelist will be used when only sees the name and full details opens
    //once you click on someone, layout with details opens
    private RecyclerView tripsRecycleView;
    private List<Trip> availTripList;
    private Spinner filterFirstChoice;
    private DB_manager be;
private EditText filterText;
private ATripAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        be = DBManagerFactory.GetFactory();
        view = inflater.inflate(R.layout.fragment_first, container, false);
        filterFirstChoice = (Spinner) view.findViewById(R.id.filter1);
        filterFirstChoice.setSelection(0);
        filterText=(EditText)view.findViewById(R.id.filterEditText);
        tripsRecycleView = view.findViewById(R.id.firstRecycleView);
        tripsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        filterFirstChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position, long id) {
                if (!filterFirstChoice.getSelectedItem().toString().equals("all"))
                    FilterDialog();
            }

            public void onNothingSelected(AdapterView arg0) {
            }
        });

        if (tripsRecycleView.getAdapter() == null) {
            availTripList = be.getNotHandeledTrips();
            adapter=new ATripAdapter(tripsRecycleView, availTripList, (String) filterFirstChoice.getSelectedItem(), getActivity());
            tripsRecycleView.setAdapter(adapter);
        } else tripsRecycleView.getAdapter().notifyDataSetChanged();

        return view;

    }

    private void FilterDialog() {
        AlertDialog.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_NEGATIVE:
                        adapter.getFilter().filter(filterText.getText().toString());
                        break;
                    case Dialog.BUTTON_POSITIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity().getBaseContext());
        alertDialogBuilder.setTitle("Filter");
        if (filterFirstChoice.getSelectedItem().toString().equals("city"))
            alertDialogBuilder.setMessage("Enter City:");
        else
            alertDialogBuilder.setMessage("Enter Distance in kilometers:");
        alertDialogBuilder.setView(filterText);
        alertDialogBuilder.setPositiveButton("Ok", onClickListener);
        alertDialogBuilder.setNegativeButton("Cancel ", onClickListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    private static class ATripAdapter extends RecyclerView.Adapter<ATripAdapter.ViewHolder> implements Filterable {
        private static final int UNSELECTED = -1;
        private DB_manager be;
        private RecyclerView recyclerView;
        private List<Trip> tripList;
        private List<Trip> filteredTripList;
        private Filter tripFilter;
        private int selectedItem = UNSELECTED;
        String choice;
        Activity a;

        public ATripAdapter(RecyclerView recyclerView, List<Trip> t, String choice, Activity c) {

            this.recyclerView = recyclerView;
            this.choice = choice;
            this.tripList = t;
            this.filteredTripList = tripList;
            be = DBManagerFactory.GetFactory();
            this.a = c;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            if (choice.equals("all") || choice == null)
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trip_view_holder, parent, false);
            else
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trip_filter_view_holder, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return filteredTripList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
            private ExpandableLayout expandableLayout;
            private Button expandButton;

            public ViewHolder(View itemView) {
                super(itemView);

                expandableLayout = itemView.findViewById(R.id.expandable_layout);
                expandableLayout.setInterpolator(new OvershootInterpolator());
                expandableLayout.setOnExpansionUpdateListener(this);
                expandButton = itemView.findViewById(R.id.expand_button);

                expandButton.setOnClickListener(this);
            }

            public void bind() {
                int position = getAdapterPosition();
                boolean isSelected = position == selectedItem;
                expandButton.setSelected(isSelected);
                expandableLayout.setExpanded(isSelected, false);
            }

            @Override
            public void onClick(View view) {
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
            }

            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d("ExpandableLayout", "State: " + state);
                if (state == ExpandableLayout.State.EXPANDING) {
                    recyclerView.smoothScrollToPosition(getAdapterPosition());
                }
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
                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0 || choice.equals("all")) {
                    // No filter implemented we return all the list
                    results.values = tripList;
                    results.count = tripList.size();
                }
                // We perform filtering operation
                else {
                    if (choice.equals("city")) {
                        List<Trip> cTrip = be.getNotHandeledTripsInCity(constraint.toString(), a.getBaseContext());
                        results.values = cTrip;
                        results.count = cTrip.size();
                    } else {

                        List<Trip> dTrip = be.getNotHandeledTripsInDistance(Integer.parseInt(constraint.toString()), a);
                        results.values = dTrip;
                        results.count = dTrip.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                filteredTripList = (List<Trip>) results.values;
                notifyDataSetChanged();
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


    @Override
    public void onDestroy() {
        Firebase_DBManager.stopNotifyToTripList();
        super.onDestroy();
    }
}
