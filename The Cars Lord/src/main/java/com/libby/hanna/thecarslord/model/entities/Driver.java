package com.libby.hanna.thecarslord.model.entities;

import java.util.Objects;

public class Driver {
    //region Fields
    String lastName;
    String firstName;
    Long _id;
    String phoneNumber;
    String emailAddress;
    String creditCardNumber;
//endregion

    //region Constructors
    public Driver() {
    }

    public Driver(String lastName, String firstName, Long _id, String phoneNumber, String emailAddress, String creditCardNumber) {

        this.lastName = lastName;
        this.firstName = firstName;
        this._id = _id;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.creditCardNumber = creditCardNumber;
    }

    public Driver(Driver d) {
        this.lastName = d.lastName;
        this.firstName = d.firstName;
        this._id = d._id;
        this.phoneNumber = d.phoneNumber;
        this.emailAddress = d.emailAddress;
        this.creditCardNumber = d.creditCardNumber;
    }
    //endregion

    //region Getter Setter
    public String getLastName() {

        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
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

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
    //endregion

    //region Operations
    @Override
    public String toString() {
        return "Driver{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", _id=" + _id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", creditCardNumber='" + creditCardNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return lastName.equals(driver.getLastName()) &&
                firstName.equals(driver.getFirstName()) &&
                _id.equals(driver.get_id()) &&
                phoneNumber.equals(driver.getPhoneNumber()) &&
                emailAddress.equals(driver.getEmailAddress()) &&
                creditCardNumber.equals(driver.getCreditCardNumber());
    }
    //endregion

}
