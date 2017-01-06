package com.example.neel.bookingapp.Model;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Created by sushrutshringarputale on 1/5/17.
 */

public class Turf {
    private Location location;
    private String name;
    private User owner;
    private String description;
    private Rating rating;
    private Map<String, String> attributes;
    private String Id;
    private int numFree;

    public Turf(Location location, String name, User owner, String description, Rating rating, Map<String, String> attributes) {
        this.location = location;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.rating = rating;
        this.attributes = attributes;
    }

    public Turf(Location location, String name, User owner) {
        this(location, name, owner, "", new Rating(), null);
    }

    public Turf() {
        this(null, "", new User("",""));
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Turf{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                '}';
    }

    public Turf saveLobby() {
        try {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("turf");
            String key = db.push().getKey();
            db.child(key).setValue(this);
        } catch (NullPointerException e) {
            Log.e("Turf update", "Firebase uninit" + e.getMessage());
        }
        return this;
    }


    public int getNumFree() {
        return numFree;
    }

    public void setNumFree(int numFree) {
        this.numFree = numFree;
    }
}
