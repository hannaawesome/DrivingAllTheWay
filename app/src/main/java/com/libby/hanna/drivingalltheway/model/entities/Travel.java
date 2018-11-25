package com.libby.hanna.drivingalltheway.model.entities;

import java.sql.Time;

public class Travel {
    /**
     * inner class, defines the state which the travel is in
     */
    public enum TravelState{available, inProcess, finished};
    private TravelState state;
    private String source;
    private String destination;
    private Time start;
    private Time finish;
    private String name;
    private int phoneNumber;
    private String emailAddress;

}
