package com.libby.hanna.drivingalltheway.model.backend;

import com.libby.hanna.drivingalltheway.model.entities.Trip;

public interface DB_manager
{
    public interface Action<T> {
        void onSuccess(T obj);

        void onFailure(Exception exception);
        }
    void addTrip(final Trip t ,final Action<Long> action);

}
