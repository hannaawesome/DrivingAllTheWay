package com.libby.hanna.drivingalltheway.model.entities;

import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import java.sql.Time;

public class Trip {

        /**
         * inner class, defines the state which the trip is in
         */
        public enum TripState {
            available, inProcess, finished
        }



        private TripState state;
        private String source;
        private String destination;
        private Time start;
        private Time finish;
        private String name;
        private PhoneNumberUtils phoneNumber;
        private ContactsContract.CommonDataKinds.Email emailAddress;

        public Trip(TripState state, String source, String destination, Time start, Time finish, String name, PhoneNumberUtils phoneNumber, ContactsContract.CommonDataKinds.Email emailAddress) {
            this.state = state;
            this.source = source;
            this.destination = destination;
            this.start = start;
            this.finish = finish;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.emailAddress = emailAddress;
        }

        //region Getter and Setter
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


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Trip trip = (Trip) o;
            return state == trip.state &&
                    source.equals(trip.source) &&
                    source.equals(trip.destination) &&
                    source.equals(trip.start) &&
                    source.equals(trip.finish) &&
                    source.equals(trip.name) &&
                    source.equals(trip.phoneNumber) &&
                    source.equals(trip.emailAddress);
        }


        @Override
        public String toString() {
            return "Trip{" +
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