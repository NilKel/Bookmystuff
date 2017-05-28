package com.example.neel.bookingapp.Model;

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
 * Created by sushrutshringarputale on 5/28/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 * @apiNote Parcelable not implemented properly. Do not rely on it. If passed as a parcel, always update from database first.
 */

public class Booking implements Parcelable {

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };
    public String id;
    public Date startTime;
    public Date endTime;
    public Turf turf;
    public Lobby lobby;
    public HashMap<String, String> attributes;

    public Booking(String id, Date startTime, Date endTime, Turf turf, Lobby lobby, HashMap<String, String> attributes) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.turf = turf;
        this.lobby = lobby;
        this.attributes = attributes;
    }

    public Booking() {
        this("", new Date(), new Date(), new Turf(), new Lobby(), new HashMap<>());
    }

    protected Booking(Parcel in) {
        id = in.readString();
        turf = in.readParcelable(Turf.class.getClassLoader());
        lobby = in.readParcelable(Lobby.class.getClassLoader());
        attributes = in.readHashMap(HashMap.class.getClassLoader());
    }

    public static Deferred<Booking, DatabaseException, Void> getBookingFromRef(BookingRef ref) {
        Booking booking = new Booking(ref.id, new Date(ref.startTime), new Date(ref.endTime), new Turf(ref.turfId), new Lobby(ref.lobbyId), ref.attributes);
        Deferred<Booking, DatabaseException, Void> deferred = new DeferredObject<>();
        DatabaseConnector databaseConnector = new DatabaseConnector();
        databaseConnector.updateTurf(booking.turf).promise().done(turf1 -> {
            booking.turf = turf1;
            databaseConnector.updateLobby(booking.lobby).promise().done(lobby1 -> {
                booking.lobby = lobby1;
                deferred.resolve(booking);
            }).fail(deferred::reject);
        }).fail(deferred::reject);
        return deferred;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", turf=" + turf +
                ", lobby=" + lobby +
                ", attributes=" + attributes +
                '}';
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
        dest.writeString(id);
        dest.writeParcelable(turf, flags);
        dest.writeParcelable(lobby, flags);
        dest.writeMap(attributes);
    }

    public interface IBookingCrud {
        Deferred<Booking, DatabaseException, Void> createBooking(Booking booking);

        Deferred<Booking, DatabaseException, Void> readBooking(Booking booking);

        Deferred<Booking, DatabaseException, Void> updateBooking(Booking booking);

        Deferred<Void, DatabaseException, Void> deleteBooking(Booking booking);
    }

    public static class BookingRef {
        public String id;
        public Long startTime;
        public Long endTime;
        public String turfId;
        public String lobbyId;
        public HashMap<String, String> attributes;

        public BookingRef(String id, Long startTime, Long endTime, String turfId, String lobbyId, HashMap<String, String> attributes) {
            this.id = id;
            this.startTime = startTime;
            this.endTime = endTime;
            this.turfId = turfId;
            this.lobbyId = lobbyId;
            this.attributes = attributes;
        }

        public BookingRef(Booking booking) {
            this.id = booking.id;
            this.attributes = booking.attributes;
            this.lobbyId = booking.lobby.getKey();
            this.startTime = booking.startTime.getTime();
            this.endTime = booking.endTime.getTime();
            this.turfId = booking.turf.getId();
        }

        public BookingRef() {
        }

        @Exclude
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            map.put("turfId", turfId);
            map.put("lobbyId", lobbyId);
            map.put("attributes", attributes);
            return map;
        }
    }
}
