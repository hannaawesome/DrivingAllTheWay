package com.libby.hanna.drivingalltheway.model.entities;

import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import java.sql.Time;
import java.util.Objects;

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
    private PhoneNumberUtils phoneNumber;
    private ContactsContract.CommonDataKinds.Email emailAddress;

    public Travel(TravelState state, String source, String destination, Time start, Time finish, String name, PhoneNumberUtils phoneNumber, ContactsContract.CommonDataKinds.Email emailAddress) {
        this.state = state;
        this.source = source;
        this.destination = destination;
        this.start = start;
        this.finish = finish;
        this.name = name;
        this.phoneNumber=phoneNumber;
        this.emailAddress = emailAddress;
    }

    //region Getter and Setter
    public TravelState getState() {
        return state;
    }

    public void setState(TravelState state) {
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

    public PhoneNumberUtils getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumberUtils phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ContactsContract.CommonDataKinds.Email getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(ContactsContract.CommonDataKinds.Email emailAddress) {
        this.emailAddress = emailAddress;
    }
    //endregion
// bhhh
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Travel travel = (Travel) o;
        return getState() == travel.getState() &&
                Objects.equals(getSource(), travel.getSource()) &&
                Objects.equals(getDestination(), travel.getDestination()) &&
                Objects.equals(getStart(), travel.getStart()) &&
                Objects.equals(getFinish(), travel.getFinish()) &&
                Objects.equals(getName(), travel.getName()) &&
                Objects.equals(getPhoneNumber(), travel.getPhoneNumber()) &&
                Objects.equals(getEmailAddress(), travel.getEmailAddress());
    }

    @Override
    public String toString() {
        return "Travel{" +
                "state=" + state +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", start=" + start +
                ", finish=" + finish +
                ", name='" + name + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", emailAddress=" + emailAddress +
                '}';
    }

}
