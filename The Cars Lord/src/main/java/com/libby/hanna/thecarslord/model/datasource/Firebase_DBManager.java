package com.libby.hanna.thecarslord.model.datasource;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Firebase_DBManager implements DB_manager {
    static DatabaseReference DriversRef;
    static DatabaseReference TripRef;
    static List<Driver> driverList;
    static List<Trip> tripList;
    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;
    static Trip trip;

    static {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DriversRef = database.getReference("drivers");
        driverList = new ArrayList<Driver>();
        TripRef = database.getReference("trips");
        tripList = new ArrayList<Trip>();
    }

    public static ChildEventListener driverRefChildEventListener;
    public static ChildEventListener tripRefChildEventListener;
    /**
     * @param dr     the driver to add
     * @param action details on success or fail of adding the driver to the firebase
     */
    //region backend implementation
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

    /**
     * @return the drivers' names
     */
    @Override
    public List<String> getDriversNames() {
        List<String> names = new ArrayList<>();
        for (Driver i : driverList) {
            names.add(i.getFirstName() + ' ' + i.getLastName());
        }
        return names;
    }

    /**
     * @return the available trips
     */
    @Override
    public List<Trip> getNotHandeledTrips() {
        List<Trip> trips = new ArrayList<>();
        for (Trip i : tripList) {
            if (i.getState() == Trip.TripState.available)
                trips.add(i);
        }
        return trips;
    }

    /**
     * @return the finished trips,
     */
    @Override
    public List<Trip> getFinishedTrips() {
        List<Trip> trips = new ArrayList<>();
        for (Trip i : tripList) {
            if (i.getState() == Trip.TripState.finished)
                trips.add(i);
        }
        return trips;
    }

    /**
     * @param _id the requested driver
     * @return all the drivers' trips
     */
    @Override
    public List<Trip> getSpecificDriverTrips(Long _id) {
        List<Trip> trips = new ArrayList<>();
        for (Trip i : tripList) {
            if (i.getDriver() != null)
                if (i.getDriver().equals(_id))
                    trips.add(i);
        }
        return trips;
    }

    /**
     * @param city the function find the location of the city because it
     *             may be entered in another language or does not exist
     * @param c    context for the geocoder and the toast
     * @return all the trips that are available and their destination is in the city
     */
    @Override
    public List<Trip> getNotHandeledTripsInCity(String city, Context c) {
        List<Trip> notHandeledTrips = getNotHandeledTrips();
        List<Trip> trips = new ArrayList<>();
        Geocoder geocoder = new Geocoder(c, Locale.getDefault());
        List<Address> addresses = null;
        List<Address> myCityAddresses = null;
        Location l, myCity;
        for (Trip i : notHandeledTrips) {
            l = fromStringToLocation(c, i.getSource());
            myCity = fromStringToLocation(c, city);
            if (myCity != null) {
                try {
                    addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
                    myCityAddresses = geocoder.getFromLocation(myCity.getLatitude(), myCity.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        {
                            String cityName = addresses.get(0).getAddressLine(0);
                            String otherCityName = myCityAddresses.get(0).getAddressLine(0);
                            if (cityName.equals(otherCityName))
                                trips.add(i);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(c, "failed to get location", Toast.LENGTH_SHORT).show();
                }

            } else
                return null;
        }
        return trips;
    }

    /**
     * @param distance in kilometers
     * @param a        it is activity and not context because in order to get your
     *                 location the activity is needed
     * @return all the trips that are available and the distance from source to the driver is distance
     */
    @Override
    public List<Trip> getNotHandeledTripsInDistance(final int distance, final Activity a, Location thisLocation) {
        final List<Trip> notHandeledTrips = getNotHandeledTrips();
        final List<Trip> trips = new ArrayList<>();
        for (Trip i : notHandeledTrips) {
            int temp = distanceCalc(i, a.getBaseContext());
            if (temp == distance)
                trips.add(i);
        }
        return trips;
    }

    /**
     * @param t the requested time
     * @return all the trips that has been requested to t time.
     */
    @Override
    public List<Trip> getTripsByTime(Time t) {
        List<Trip> trips = new ArrayList<>();
        for (Trip i : tripList) {
            if (i.getStart().equals(t))
                trips.add(i);
        }
        return trips;
    }

    /**
     * @param c   for geocoder
     * @param str the location in a string
     * @return Location made from this string
     */
    private Location fromStringToLocation(Context c, String str) {
        Geocoder gc = new Geocoder(c);
        try {
            if (gc.isPresent()) {
                List<Address> list = gc.getFromLocationName(str, 1);
                if (list == null) {
                    Toast.makeText(c, "your location does not exist", Toast.LENGTH_LONG).show();
                    return null;
                } else {
                    Address address = list.get(0);
                    double lat = address.getLatitude();
                    double lng = address.getLongitude();
                    Location location = new Location(str);
                    location.setLatitude(lat);
                    location.setLongitude(lng);
                    return location;
                }
            }
            return null;
        } catch (Exception exception) {
            Toast.makeText(c, "must be something wrong with the location\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * @param price the filter for trip price
     * @param t     the first filtered list (if all the trips, then it will be null)
     * @param c     for the function fromStringToLocation
     * @return all the trips from t (or from all the trips) that the trip's price will be price
     */
    @Override
    public List<Trip> getTripsByPrice(double price, List<Trip> t, Context c) {
        if (t == null)
            t = tripList;
        List<Trip> trips = new ArrayList<>();
        for (Trip i : t) {
            //2 dollars per km, the distance needs to be round or it will never be equal
            if (priceCalc(i, c) == price)
                trips.add(i);
        }
        return trips;
    }

    /**
     * @param t the trip to calculate the distance
     * @param c for calculating the location
     * @return the distance in km
     */
    public int distanceCalc(Trip t, Context c) {
        int temp = Math.round(fromStringToLocation(c, t.getSource()).distanceTo(fromStringToLocation(c, t.getDestination())) / 1000);
        return temp;
    }

    /**
     * @param t the trip to calculate the price
     * @param c for the function distanceCalc
     * @return the price for the trip
     */
    private double priceCalc(Trip t, Context c) {
        return distanceCalc(t, c) * 2;
    }
    //endregion

    //region notify driver list
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
//endregion

    /**
     * @param notifyDataChange follows changes in the code
     */
    //region notify trip list
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
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm"); // 12 hour format
                    try {
                        java.util.Date d1 = (java.util.Date) format.parse(dataSnapshot.child("start").getValue().toString());
                        t.setStart(new Time(d1.getTime()));
                        d1 = (java.util.Date) format.parse(dataSnapshot.child("finish").getValue().toString());
                        t.setFinish(new Time(d1.getTime()));
                        t.set_id(dataSnapshot.getKey());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tripList.add(t);
                    notifyDataChange.OnDataChanged(tripList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Trip t = dataSnapshot.getValue(Trip.class);
                    String id = dataSnapshot.getKey();
                    t.set_id(id);
                    for (int i = 0; i < tripList.size(); i++) {
                        if (tripList.get(i).get_id().equals(id)) {
                            trip = tripList.get(i);
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

    /**
     * to stop the listener
     */
    public static void stopNotifyToTripList() {
        if (tripRefChildEventListener != null) {
            TripRef.removeEventListener(tripRefChildEventListener);
            tripRefChildEventListener = null;
        }
    }
    //endregion

    //region status change

    /**
     * @param t      the trip to change
     * @param d      the driver that took this trip
     * @param action returns data weather the trip has changed to now or failed
     */
    public void changeNow(Trip t, Driver d, final Action<Void> action) {
        t.setDriver(d.get_id());
        t.setState(Trip.TripState.inProcess);
        TripRef.child(t.get_id()).setValue(t).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(aVoid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
        TripRef.child(t.get_id()).child("start").setValue(t.getStart().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(aVoid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
        TripRef.child(t.get_id()).child("finish").setValue(t.getFinish().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(aVoid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
    }

    /**
     * @param t      the trip to change to finish
     * @param fTime  the finish time
     * @param action returns data weather the trip has changed to finished or failed
     */
    public void changeFinish(Trip t, Driver d, final Time fTime, final Action<Void> action) {
        t.setState(Trip.TripState.finished);
        t.setFinish(fTime);
        t.setDriver(d.get_id());
        TripRef.child(t.get_id()).setValue(t).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                action.onSuccess(v);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
        TripRef.child(t.get_id()).child("start").setValue(t.getStart().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(aVoid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
        TripRef.child(t.get_id()).child("finish").setValue(t.getFinish().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(aVoid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
    }
    //endregion

    /**
     * @param c for the toast
     * @return the full data on current user
     */
    public Driver loadDataOnCurrentDriver(Context c) {
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        String email = currentUser.getEmail();
        Driver d = null;
        try {
             while (d==null) {//if driverList has not been fully loaded yet
            for (Driver i : driverList)
                if (email.equals(i.getEmailAddress()))
                    d = i;
             }
            return d;
        } catch (Exception ex) {
            Toast.makeText(c, ex.toString(), Toast.LENGTH_LONG).show();
            return null;
        }

    }

}