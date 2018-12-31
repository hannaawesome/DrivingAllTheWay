/*
Hanna Weissberg 318796398
Libby Olidort 209274612
*/
package com.libby.hanna.drivingalltheway.model.datasource;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.libby.hanna.drivingalltheway.model.backend.DB_manager;
import com.libby.hanna.drivingalltheway.model.entities.*;

/**
 * Class that implements DB_manager - the function addTrip
 * and puts the data on firebase database
 */
public class Firebase_DBManager implements DB_manager {

    static DatabaseReference TripRef;
    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        TripRef = database.getReference("trips");//root
    }

    /**
     * implementation of the function addTrip
     */
    @Override
    public void addTrip(final Trip trip, final Action<String> action) {

            String key = trip.get_id();//setting key
            TripRef.child(key).setValue(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    action.onSuccess(trip.get_id());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    action.onFailure(e);
                }
            });
        //region The data that cannot enter automatically to the firebase, thus, needs to be enters manually.
        TripRef.child(key).child("start").setValue(trip.getStart().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(trip.get_id());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
        TripRef.child(key).child("finish").setValue(trip.getFinish().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(trip.get_id());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });

        //endregion
    }
}

