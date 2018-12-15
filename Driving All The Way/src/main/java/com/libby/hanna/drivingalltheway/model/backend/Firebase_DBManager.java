package com.libby.hanna.drivingalltheway.model.backend;


import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.libby.hanna.drivingalltheway.model.entities.*;

import java.util.ArrayList;
import java.util.List;

public class Firebase_DBManager implements DB_manager {

    static DatabaseReference TripRef;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        TripRef = database.getReference("trips");
    }
    @Override
    public boolean addTrip(Trip trip) {
        try {
            String key = trip.get_id().toString();
            TripRef.child(key).setValue(trip);
            TripRef.child(key).child("start").setValue(trip.getStart().toString());
            TripRef.child(key).child("finish").setValue(trip.getFinish().toString());

           /* addTripToFirebase(trip, new Firebase_DBManager.Action<Long>() {
                @Override
                public void onSuccess(Long obj) {
                }

                @Override
                public void onFailure(Exception exception) {
                   // Toast.makeText(getBaseContext(), "Error \n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onProgress(String status, double percent) {

                }
            });*/
            return true;
        } catch (Exception e)
        {
            return false;
          //  Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_LONG).show();
        }
    }
    /*
    public interface Action<T> {
        void onSuccess(T obj);

        void onFailure(Exception exception);

        void onProgress(String status, double percent);
    }*/


   /* private static void addTripToFirebase(final Trip t, final Action<Long> action) {
        String key = t.get_id().toString();
        TripRef.child(key).setValue(t)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(t.get_id());
                action.onProgress("upload trip data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload trip data", 100);
            }
        });
        TripRef.child(key).child("start").setValue(t.getStart().toString());
        TripRef.child(key).child("finish").setValue(t.getFinish().toString());
    }*/
}

