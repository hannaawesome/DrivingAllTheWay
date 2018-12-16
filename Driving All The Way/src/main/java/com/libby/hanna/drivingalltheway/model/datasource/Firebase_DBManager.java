package com.libby.hanna.drivingalltheway.model.datasource;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.libby.hanna.drivingalltheway.model.backend.DB_manager;
import com.libby.hanna.drivingalltheway.model.entities.*;


public class Firebase_DBManager implements DB_manager {

    static DatabaseReference TripRef;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        TripRef = database.getReference("trips");
    }

    @Override
    public void addTrip(final Trip trip, final Action<Long> action) {

            String key = trip.get_id().toString();
            TripRef.child(key).setValue(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    action.onSuccess(trip.get_id());
                    action.onProgress("upload trip data", 100);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    action.onFailure(e);
                    action.onProgress("error upload student data", 100);
                }
            });

        TripRef.child(key).child("start").setValue(trip.getStart().toString());
        TripRef.child(key).child("finish").setValue(trip.getFinish().toString());
        //location
    }
}

