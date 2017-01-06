package com.example.neel.bookingapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sushrutshringarputale on 1/6/17.
 */

public class Lobby implements Parcelable{
    private User owner;
    private int numFree;
    private ArrayList<User> lobbyList;

    public Lobby(User owner, int numFree, ArrayList<User> lobbyList) {
        this.owner = owner;
        this.numFree = numFree;
        this.lobbyList = lobbyList;
    }

    public Lobby(User owner) {
        this.owner = owner;
        this.lobbyList = new ArrayList<>();
        this.lobbyList.add(owner);
    }

    public Lobby() {
        this(new User("", ""),0, new ArrayList<User>());
    }

    protected Lobby(Parcel in) {
        numFree = in.readInt();
    }

    public static final Creator<Lobby> CREATOR = new Creator<Lobby>() {
        @Override
        public Lobby createFromParcel(Parcel in) {
            return new Lobby(in);
        }

        @Override
        public Lobby[] newArray(int size) {
            return new Lobby[size];
        }
    };

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getNumFree() {
        return numFree;
    }

    public void setNumFree(int numFree) {
        this.numFree = numFree;
    }

    public ArrayList<User> getLobbyList() {
        return lobbyList;
    }

    public void setLobbyList(ArrayList<User> lobbyList) {
        this.lobbyList = lobbyList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(owner);
        parcel.writeInt(numFree);
        parcel.writeList(lobbyList);
    }
}
