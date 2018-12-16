/*
Hanna Weissberg 318796398
Libby Olidort 209274612
*/
package com.libby.hanna.drivingalltheway.model.backend;
import com.libby.hanna.drivingalltheway.model.entities.Trip;

/**
 * The interface that contains the function add trip and the interface Action
 */
public interface DB_manager
{

    /**
     * @param <T>
     *   Interface in order to provide data on success or failure of T
     */
    public interface Action<T> {
        void onSuccess(T obj);
        void onFailure(Exception exception);
        }

    /**
     * to add new trip to the list of trips
     * @param t
     * @param action
     */
    void addTrip(final Trip t ,final Action<Long> action);

}
