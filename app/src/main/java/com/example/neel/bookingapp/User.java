package com.example.neel.bookingapp;

import android.media.Image;

/**
 * Created by sushrutshringarputale on 9/19/16.
 */

class User {
    private String firstName;
    private String lastName;
    private String email;
    private long phNo;
    private Image profPicture;

    public User(String email, String firstName, String lastName, long phNo, Image profPicture) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phNo = phNo;
        this.profPicture = profPicture;
    }

    public User(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getPhNo() {
        return phNo;
    }

    public void setPhNo(long phNo) {
        this.phNo = phNo;
    }

    public Image getProfPicture() {
        return profPicture;
    }

    public void setProfPicture(Image profPicture) {
        this.profPicture = profPicture;
    }

    @Override
    public String toString() {
        return "User: " + getFirstName() + " " + getLastName();
    }


}
