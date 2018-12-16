package com.libby.hanna.drivingalltheway.model.backend;

import android.content.ContentValues;

import com.libby.hanna.drivingalltheway.model.entities.Trip;
import com.libby.hanna.drivingalltheway.model.entities.Trip.TripState;

import java.sql.Time;
import java.text.SimpleDateFormat;

/*public class TripConst {
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
        try{
        Trip t = new Trip();
        t.setState(TripState.valueOf(cv.getAsString(STATE)));
        t.setSource(cv.getAsString(SOURCE));
        t.setDestination(cv.getAsString(DESTINATION));
        SimpleDateFormat format = new SimpleDateFormat("HH:mm"); // 12 hour format
        java.util.Date d1 = (java.util.Date) format.parse(cv.getAsString(START));
        t.setStart(new Time(d1.getTime()));
        d1 = (java.util.Date) format.parse(cv.getAsString(FINISH));
        t.setFinish(new Time(d1.getTime()));
        t.setName(cv.getAsString(NAME));
        t.setPhoneNumber(cv.getAsString(PHONENUMBER));
        t.setEmailAddress(cv.getAsString(EMAILADDRESS));
        return t;
        }
        catch(Exception ex)
        {
            return  null;
        }
    }
}
*/