package com.example.neel.bookingapp.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.neel.bookingapp.Other.DatabaseConnector;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.Exclude;

import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sushrutshringarputale on 1/5/17.
 */
public class Turf implements Parcelable {
    public static final Creator<Turf> CREATOR = new Creator<Turf>() {
        @Override
        public Turf createFromParcel(Parcel in) {
            return new Turf(in);
        }

        @Override
        public Turf[] newArray(int size) {
            return new Turf[size];
        }
    };
    @Exclude
    public HashMap<Long, HashMap<Long, String>> availability;
    private Location location;
    private String name;
    private User owner;
    private String description;
    private Rating rating;
    private HashMap<String, String> attributes;
    private String id;

    public Turf(Location location, String name, User owner, String description, Rating rating, HashMap<String, String> attributes, String id) {
        this.location = location;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.rating = rating;
        this.attributes = attributes;
        this.id = id;
    }

    public Turf(Location location, String name, User owner) {
        this(location, name, owner, "", new Rating(), null, null);
    }

    public Turf() {
        this(null, "", new User("", ""));
    }


    public Turf(String id) {
        this.id = id;
    }


    protected Turf(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        name = in.readString();
        owner = in.readParcelable(User.class.getClassLoader());
        description = in.readString();
        id = in.readString();
        rating = in.readParcelable(Rating.class.getClassLoader());
        attributes = in.readHashMap(Map.class.getClassLoader());
    }

    public static Deferred<Turf, DatabaseException, Void> getTurfFromRef(TurfRef ref) {
        Turf turf = new Turf();
        Deferred<Turf, DatabaseException, Void> deferred = new DeferredObject<>();
        turf.location = LocationPlus.getLocationFromRepresentation(ref.location);
        turf.name = ref.name;
        turf.description = ref.description;
        turf.rating = new Rating(ref.rating);
        turf.attributes = ref.attributes;
        turf.id = ref.id;
        new DatabaseConnector().readUser(new User(ref.ownerId))
                .promise().done(user -> {
            turf.owner = user;
            deferred.resolve(turf);
        }).fail(deferred::reject);
        return deferred;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeString(name);
        dest.writeParcelable(owner, flags);
        dest.writeParcelable(rating, flags);
        dest.writeString(description);
        dest.writeString(id);
        dest.writeMap(this.attributes);
    }

    public Deferred<HashMap<Long, String>, DatabaseException, Void> getAvailibility(Date date) {
        Deferred<HashMap<Long, String>, DatabaseException, Void> deferred = new DeferredObject<>();
        new DatabaseConnector().getAvailabilityForLobby(this, date).promise()
                .done(deferred::resolve).fail(deferred::reject);
        return deferred;
    }


    public interface ITurfCrud {
        Deferred createTurf(Turf user);

        Deferred readTurf(Turf user);

        Deferred updateTurf(Turf user);

        Deferred deleteTurf(Turf user);
    }

    public static class TurfRef {
        public String location;
        public String name;
        public String ownerId;
        public String description;
        public float rating;
        public HashMap<String, String> attributes;
        public String id;

        public TurfRef(String location, String name, String ownerId, String description, int rating, HashMap<String, String> attributes, String id) {
            this.location = location;
            this.name = name;
            this.ownerId = ownerId;
            this.description = description;
            this.rating = rating;
            this.attributes = attributes;
            this.id = id;
        }

        public TurfRef() {
        }

        public TurfRef copyData(Turf turf) {
            TurfRef ref = new TurfRef();
            ref.attributes = turf.attributes;
            ref.description = turf.description;
            ref.id = turf.id;
            ref.location = turf.location.toString();
            ref.rating = turf.rating.getRating();
            ref.name = turf.name;
            ref.ownerId = turf.owner.id;
            return ref;
        }

        @Exclude
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("location", location);
            map.put("name", name);
            map.put("ownerId", ownerId);
            map.put("description", description);
            map.put("rating", rating);
            map.put("attributes", attributes);
            map.put("id", id);
            return map;
        }

    }

}
