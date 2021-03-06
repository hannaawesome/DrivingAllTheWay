package com.libby.hanna.thecarslord.model.backend;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.libby.hanna.thecarslord.model.entities.*;

import java.sql.Time;
import java.util.List;

public interface DB_manager {
    /**
     * @param <T> Interface in order to provide data on success or failure of T
     */
    public interface Action<T> {
        void onSuccess(T obj);

        void onFailure(Exception exception);
    }
    /**
     * @param <T>
     *     Interface in order to provide data when T is changed
     */
    public interface NotifyDataChange<T> {
        void OnDataChanged(T obj);

        void onFailure(Exception exception);
    }

    List<String> getDriversNames();

    void addDriver(final Driver dr, final Action<Long> action);

    List<Trip> getNotHandeledTrips();

    List<Trip> getFinishedTrips();

    List<Trip> getSpecificDriverTrips(Long _id);

    List<Trip> getNotHandeledTripsInCity(String city,Context c);

    List<Trip> getNotHandeledTripsInDistance(int distance,Activity c,Location thisLocation);

    List<Trip> getTripsByTime(Time t);

    List<Trip> getTripsByPrice(double price,List<Trip> t,Context c);
    void changeNow(Trip t, Driver d, final Action<Void> action);
    void changeFinish(Trip t, Driver d,final Time fTime,final Action<Void> action);
    Driver loadDataOnCurrentDriver(Context c);
    int distanceCalc(Trip t,Context c);
    }
