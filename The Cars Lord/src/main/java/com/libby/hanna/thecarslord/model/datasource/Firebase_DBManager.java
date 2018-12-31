package com.libby.hanna.thecarslord.model.datasource;

import android.drm.DrmStore;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.entities.Driver;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.util.ArrayList;
import java.util.List;

public class Firebase_DBManager implements DB_manager {
    static DatabaseReference DriversRef;
    static List<Driver> driverList;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DriversRef = database.getReference("drivers");
        driverList = new ArrayList<>();
        DriversRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                // statusText.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // statusText.setText("Failed to read value.");
            }
        });
    }

    @Override
    public void addDriver(final Driver dr, final Action<Long> action) {
        String key = dr.get_id().toString();//setting key
        DriversRef.child(key).setValue(dr).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(dr.get_id());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
    }

    @Override
    public List<String> getDriversNames() {
        DataSnapshot dataSnapshot;
        //Driver student = dataSnapshot.getValue(Driver.class);
        //String id = dataSnapshot.getKey();
        //student.setId(Long.parseLong(id));
        //driverList.add()
        return null;
    }

    @Override
    public List<Trip> getNotHandeledTrips() {
        return null;
    }

    @Override
    public List<Trip> getFinishedTrips() {
        return null;
    }

    @Override
    public List<Trip> getSpecificDriverTrips() {
        return null;
    }

    @Override
    public List<Trip> getNotHandeledTripsInCity() {
        return null;
    }

    @Override
    public List<Trip> getNotHandeledTripsInDistance() {
        return null;
    }

    @Override
    public List<Trip> getTripsByTime() {
        return null;
    }

    @Override
    public List<Trip> getTripsByPrice() {
        return null;
    }
}
