package com.libby.hanna.thecarslord.model.backend;

import com.libby.hanna.thecarslord.model.entities.*;

import java.util.List;

public interface DB_manager {
    /**
     * @param <T> Interface in order to provide data on success or failure of T
     */
    public interface Action<T> {
        void onSuccess(T obj);

        void onFailure(Exception exception);
    }

    public interface NotifyDataChange<T> {
        void OnDataChanged(T obj);

        void onFailure(Exception exception);
    }

    List<String> getDriversNames();

    void addDriver(final Driver dr, final Action<Long> action);

    List<Trip> getNotHandeledTrips();

    List<Trip> getFinishedTrips();

    List<Trip> getSpecificDriverTrips();

    List<Trip> getNotHandeledTripsInCity();

    List<Trip> getNotHandeledTripsInDistance();

    List<Trip> getTripsByTime();

    List<Trip> getTripsByPrice();
}
