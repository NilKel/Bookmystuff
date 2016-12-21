package com.example.neel.bookingapp;

import android.media.Image;

import com.firebase.client.Firebase;

/**
 * Created by sushrutshringarputale on 9/19/16.
 */

class User {
    private String Id;
    private String name;
    private String email;
    private long phNo;
    private Image profPicture;
    private String password;

    public User(String email, String name, String password, long phNo) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phNo = phNo;
    }

    public User(String email, String name, long phNo, Image profPicture) {
        this.email = email;
        this.name = name;
        this.phNo = phNo;
        this.profPicture = profPicture;
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "User: " + getName();
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


    public void saveUser() {
        //Add YOUR Firebase Reference URL instead of the following URL
        Firebase myFirebaseRef = new Firebase("https://bookmystuff-79c2e.firebaseio.com/");
        myFirebaseRef = myFirebaseRef.child("users").child(getId());
        myFirebaseRef.setValue(this);
    }
}
