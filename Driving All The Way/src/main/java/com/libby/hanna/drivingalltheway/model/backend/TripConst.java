package com.libby.hanna.drivingalltheway.model.backend;

import android.content.ContentValues;

import com.libby.hanna.drivingalltheway.model.entities.Trip;
import com.libby.hanna.drivingalltheway.model.entities.Trip.TripState;

import java.sql.Time;

public class TripConst {
    public static final String STATE = "state";
    public static final String SOURCE = "source";
    public static final String DESTINATION = "destination";
    public static final String START = "start";
    public static final String FINISH = "finish";
    public static final String NAME = "name";
    public static final String PHONENUMBER = "phoneNumber";
    public static final String EMAILADDRESS = "emailAddress";

    public static ContentValues TripToContentValues(Trip t) {

        ContentValues cv = new ContentValues();
        cv.put(TripConst.STATE, t.getState().toString());
        cv.put(SOURCE, t.getSource());
        cv.put(DESTINATION, t.getDestination());
        cv.put(START, t.getStart().toString());
        cv.put(FINISH, t.getFinish().toString());
        cv.put(NAME, t.getName());
        cv.put(PHONENUMBER, t.getPhoneNumber());
        cv.put(EMAILADDRESS, t.getEmailAddress());
        return cv;
    }

    public static Trip ContentValuesTOTrip(ContentValues cv) {
        Trip t = new Trip();
        t.setState(TripState.valueOf(cv.getAsString(STATE)));
        t.setSource(cv.getAsString(SOURCE));
        t.setDestination(cv.getAsString(DESTINATION));
        t.setStart(Time.valueOf(cv.getAsString(START)));
        t.setFinish(Time.valueOf(cv.getAsString(FINISH)));
        t.setName(cv.getAsString(NAME));
        t.setPhoneNumber(cv.getAsString(PHONENUMBER));
        t.setEmailAddress(cv.getAsString(EMAILADDRESS));
        return t;
    }
}
