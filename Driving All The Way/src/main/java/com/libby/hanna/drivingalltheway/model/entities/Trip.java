package com.libby.hanna.drivingalltheway.model.entities;

import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import com.google.firebase.database.Exclude;

import java.sql.Time;

public class Trip {

    /**
     * inner class, defines the state which the trip is in
     */
    public enum TripState {
        available, inProcess, finished
    }


    //region Fields
    private static Long countId = new Long(0);
    private Long _id;
    private TripState state;
    private String source;
    private String destination;
    private Time start;
    private Time finish;
    private String name;
    private String phoneNumber;
    private String emailAddress;
    //endregion

    //region Constructors
    public Trip(TripState state, String source, String destination, Time start, Time finish, String name,
                String phoneNumber, String emailAddress) {
        this._id = new Long(countId);
        countId=new Long(countId.intValue()+1);
        this.state = state;
        this.source = source;
        this.destination = destination;
        this.start = start;
        this.finish = finish;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public Trip() {
        this._id = countId;
        countId++;
    }

    /**
     * copy constructor
     *
     * @param t
     */
    public Trip(Trip t) {
        this._id = t._id;
        this.state = t.state;
        this.source = t.source;
        this.destination = t.destination;
        this.start = t.start;
        this.finish = t.finish;
        this.name = t.name;
        this.phoneNumber = t.phoneNumber;
        this.emailAddress = t.emailAddress;
    }
    //endregion

    //region Getter and Setter
    @Exclude
    public Long get_id() {
        return _id;
    }
    @Exclude
    public static Long getCountId() {
        return countId;
    }

    public TripState getState() {
        return state;
    }

    public void setState(TripState state) {
        this.state = state;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getFinish() {
        return finish;
    }

    public void setFinish(Time finish) {
        this.finish = finish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    //endregion

    //region operations
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return _id==trip._id&&state == trip.state &&
                source.equals(trip.source) &&
                destination.equals(trip.destination) &&
                start.equals(trip.start) &&
                finish.equals(trip.finish) &&
                name.equals(trip.name) &&
                phoneNumber.equals(trip.phoneNumber) &&
                emailAddress.equals(trip.emailAddress);
    }

    @Override
    public String toString() {
        return "Trip{" +"id="+-_id+
                ", state=" + state +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", start=" + start +
                ", finish=" + finish +
                ", name='" + name + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", emailAddress=" + emailAddress +
                '}';
    }
    //endregion

}