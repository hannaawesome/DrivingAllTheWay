package com.libby.hanna.thecarslord.model.datasource;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.libby.hanna.thecarslord.controller.MainActivity;
import com.libby.hanna.thecarslord.model.backend.DB_manager;
import com.libby.hanna.thecarslord.model.entities.Driver;
import com.libby.hanna.thecarslord.model.entities.Trip;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        List<String> names = new ArrayList<>();
        for (Driver i : driverList) {
            names.add(i.getFirstName() + ' ' + i.getLastName());
        }
        return names;
    }

    @Override
    public List<Trip> getNotHandeledTrips() {
        List<Trip> trips = new ArrayList<>();
        for (Trip i : tripList) {
            if (i.getState() == Trip.TripState.available)
                trips.add(i);
        }
        return trips;
    }

    @Override
    public List<Trip> getFinishedTrips() {
        List<Trip> trips = new ArrayList<>();
        for (Trip i : tripList) {
            if (i.getState() == Trip.TripState.finished)
                trips.add(i);
        }
        return trips;
    }

    @Override
    public List<Trip> getSpecificDriverTrips(Long _id) {
        //Query q=TripRef.orderByChild("driver").equalTo(_id);
        List<Trip> trips = new ArrayList<>();
        for (Trip i : tripList) {
            if (i.getDriver().equals(_id))
                trips.add(i);
        }
        return trips;
    }

    @Override
    public List<Trip> getNotHandeledTripsInCity(String city, Context c) {
        List<Trip> notHandeledTrips = getNotHandeledTrips();
        List<Trip> trips = new ArrayList<>();
        Geocoder geocoder = new Geocoder(c, Locale.getDefault());
        List<Address> addresses = null;
        Location l;
        for (Trip i : notHandeledTrips) {
            l = fromStringToLocation(c, i.getDestination());
            try {
                addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
                if (addresses.size() > 0) {
                    {
                        String cityName = addresses.get(0).getAddressLine(0);
                        if (cityName.equals(city))
                            trips.add(i);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return trips;
    }

    @Override
    public List<Trip> getNotHandeledTripsInDistance(int distance, Context c) {
        List<Trip> notHandeledTrips = getNotHandeledTrips();
        List<Trip> trips = new ArrayList<>();
        for (Trip i : notHandeledTrips) {
            if (Math.round(fromStringToLocation(c, i.getSource()).distanceTo(fromStringToLocation(c, i.getDestination())) / 1000) == distance)
                trips.add(i);
        }
        return trips;
    }

    @Override
    public List<Trip> getTripsByTime(Time t) {
        List<Trip> trips = new ArrayList<>();
        for (Trip i : tripList) {
            if (i.getStart().equals(t))
                trips.add(i);
        }
        return trips;
    }

    private Location fromStringToLocation(Context c, String str) {

        Geocoder gc = new Geocoder(c);
        try {
            if (gc.isPresent()) {
                List<Address> list = gc.getFromLocationName(str, 1);
                Address address = list.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();
                Location location = new Location(str);
                location.setLatitude(lat);
                location.setLongitude(lng);
                return location;
            }
            return null;
        } catch (Exception exception) {
            Toast.makeText(c, "must be something wrong with the location\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public List<Trip> getTripsByPrice(double price, Context c) {
        List<Trip> trips = new ArrayList<>();
        for (Trip i : tripList) {
            if (Math.round(fromStringToLocation(c, i.getSource()).distanceTo(fromStringToLocation(c, i.getDestination())) / 1000) * 2 == price)
                trips.add(i);
        }
        return trips;
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