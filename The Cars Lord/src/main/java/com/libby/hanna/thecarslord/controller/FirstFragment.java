package com.libby.hanna.thecarslord.controller;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.libby.hanna.thecarslord.R;
import com.libby.hanna.thecarslord.model.backend.DBManagerFactory;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.datasource.Firebase_DBManager;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.util.List;

public class FirstFragment extends Fragment {
    View view;
    //expandablelist will be used when only sees the name and full details opens
    //once you click on someone, layout with details opens
    private RecyclerView tripsRecycleView;
    private List<Trip> availTripList;
    private Spinner filter;
    private DB_manager be;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        be = DBManagerFactory.GetFactory();
        view = inflater.inflate(R.layout.fragment_first, container, false);
        filter = (Spinner) view.findViewById(R.id.filter1);
//filter.getSelectedItem();
        tripsRecycleView = view.findViewById(R.id.firstRecycleView);
        tripsRecycleView.setHasFixedSize(true);
        tripsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        Firebase_DBManager.NotifyToTripList(new Firebase_DBManager.NotifyDataChange<List<Trip>>() {
            @Override
            public void OnDataChanged(List<Trip> obj) {
                if (tripsRecycleView.getAdapter() == null) {
                    int t=0;
                    availTripList = be.getNotHandeledTripsInDistance(t,getActivity().getBaseContext());
                    tripsRecycleView.setAdapter(new TripsRecyclerViewAdapter());
                } else tripsRecycleView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getActivity().getBaseContext(), "error to get trips list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
        return view;
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

        }

        @Override
        public int getItemCount() {
            return availTripList.size();
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

    @Override
    public void onDestroy() {
        Firebase_DBManager.stopNotifyToTripList();
        super.onDestroy();
    }
}
