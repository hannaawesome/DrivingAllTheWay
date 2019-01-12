package com.libby.hanna.thecarslord.model.datasource;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.entities.Driver;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Firebase_DBManager implements DB_manager {
    static DatabaseReference DriversRef;
    static DatabaseReference TripRef;
    static List<Driver> driverList;
    static List<Trip> tripList;
    static {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DriversRef = database.getReference("drivers");
        driverList = new ArrayList<Driver>();//change to query
        TripRef = database.getReference("trips");
        tripList = new ArrayList<Trip>();
    }
    public static ChildEventListener driverRefChildEventListener;
    public static ChildEventListener tripRefChildEventListener;

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
        List<String> names=new ArrayList<>();
        for (Driver i : driverList) {
            names.add(i.getFirstName()+' '+i.getLastName());
        }
        return names;
    }

    @Override
    public List<Trip> getNotHandeledTrips() {
        List<Trip> trips=new ArrayList<>();
        for (Trip i : tripList) {
            if(i.getState()== Trip.TripState.available)
            trips.add(i);
        }
        return trips;
    }

    @Override
    public List<Trip> getFinishedTrips() {
        List<Trip> trips=new ArrayList<>();
        for (Trip i : tripList) {
            if(i.getState()== Trip.TripState.finished)
                trips.add(i);
        }
        return trips;
    }

    @Override
    public List<Trip> getSpecificDriverTrips(Long _id) {
        //Query q=TripRef.orderByChild("driver").equalTo(_id);
        List<Trip> trips=new ArrayList<>();
        for (Trip i : tripList) {
            if(i.getDriver().equals(_id))
                trips.add(i);
        }
        return trips;
    }

    @Override
    public List<Trip> getNotHandeledTripsInCity(String city) {
        List<Trip> notHandeledTrips=getNotHandeledTrips();
        List<Trip> trips=new ArrayList<>();
        for (Trip i : notHandeledTrips) {
            if(i.getDestination().equals(city))//not accurate
                trips.add(i);
        }
        return trips;
    }

    @Override
    public List<Trip> getNotHandeledTripsInDistance(double distance) {
        return null;
    }

    @Override
    public List<Trip> getTripsByTime(Time t) {
        List<Trip> trips=new ArrayList<>();
        for (Trip i : tripList) {
            if(i.getStart().equals(t))
                trips.add(i);
        }
        return trips;
    }

    @Override
    public List<Trip> getTripsByPrice() {
        return tripList;
    }
    public static void NotifyToDriversList(final NotifyDataChange<List<Driver>> notifyDataChange) {
        if (notifyDataChange != null) {
            if (driverRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify trip list"));
                return;
            }
            driverList.clear();
            driverRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Driver d = dataSnapshot.getValue(Driver.class);
                    driverList.add(d);
                    notifyDataChange.OnDataChanged(driverList);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Driver d = dataSnapshot.getValue(Driver.class);
                    Long id = Long.parseLong(dataSnapshot.getKey());
                    // student.setId(id);
                    for (int i = 0; i < driverList.size(); i++) {
                        if (driverList.get(i).get_id().equals(id)) {
                            driverList.set(i, d);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(driverList);
                }
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            DriversRef.addChildEventListener(driverRefChildEventListener);
        }
    }
    public static void stopNotifyToDriversList() {
        if (driverRefChildEventListener != null) {
            DriversRef.removeEventListener(driverRefChildEventListener);
            driverRefChildEventListener = null;
        }
    }

    //region Trip
    public static void NotifyToTripList(final NotifyDataChange<List<Trip>> notifyDataChange) {
        if (notifyDataChange != null) {
            if (tripRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify trip list"));
                return;
            }
            tripList.clear();
            tripRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Trip t = dataSnapshot.getValue(Trip.class);
                    tripList.add(t);
                    notifyDataChange.OnDataChanged(tripList);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Trip t = dataSnapshot.getValue(Trip.class);
                    Long id = Long.parseLong(dataSnapshot.getKey());
                    // student.setId(id);
                    for (int i = 0; i < tripList.size(); i++) {
                        if (tripList.get(i).get_id().equals(id)) {
                            tripList.set(i, t);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(tripList);
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            TripRef.addChildEventListener(tripRefChildEventListener);
        }
    }
    public static void stopNotifyToTripList() {
        if (tripRefChildEventListener != null) {
            TripRef.removeEventListener(tripRefChildEventListener);
            tripRefChildEventListener = null;
        }
    }
    //endregion
}