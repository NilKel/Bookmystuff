package com.example.neel.bookingapp.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by sushrutshringarputale on 1/6/17.
 */

public class Lobby implements Parcelable{
    private User owner;
    private int numFree;
    private ArrayList<User> lobbyList;
    private String name;
    private Sport sport;
    private Location location;

    public Lobby(User owner, int numFree, ArrayList<User> lobbyList, String name, Sport sport, Location location) {
        this.owner = owner;
        this.numFree = numFree;
        this.lobbyList = lobbyList;
        this.name = name;
        this.sport = sport;
        this.location = location;
    }

    public Lobby(User owner, int numFree, ArrayList<User> lobbyList, String name, Sport sport) {
        this.owner = owner;
        this.numFree = numFree;
        this.lobbyList = lobbyList;
        this.name = name;
        this.sport = sport;
    }

    public Lobby(User owner) {
        this.owner = owner;
        this.lobbyList = new ArrayList<>();
        this.lobbyList.add(owner);
    }

    public Lobby() {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Lobby{" +
                "owner=" + owner +
                ", numFree=" + numFree +
                ", lobbyList=" + lobbyList.toString() +
                ", name='" + name + '\'' +
                ", sport=" + sport +
                '}';
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public void saveLobby() {
        Log.d("Method: saveLobby", this.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("lobbies");
        DatabaseReference node = ref.push();
        node.setValue(new LobbyRef().copyData(this));
    }

    public Lobby getLobbyFromRef(LobbyRef ref) {
        ArrayList<User> mArList = new ArrayList<>();
        for (String id : ref.lobbyList.keySet()) {
            mArList.add(new User(ref.lobbyList.get("id")));
        }
        try {
            return new Lobby(new User(ref.ownerId, ref.ownerName), ref.numFree, mArList, ref.name, ref.sport, new Location(ref.getLocation()));
        } catch (NullPointerException e) {
            Log.e("NPE", e.getMessage());
            return new Lobby(new User(ref.ownerId, ref.ownerName), ref.numFree, mArList, ref.name, ref.sport);
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
