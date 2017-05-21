package com.example.neel.bookingapp.Model.lobby;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.neel.bookingapp.Model.ChatMessage;
import com.example.neel.bookingapp.Model.LocationPlus;
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Model.User;
import com.google.firebase.database.Exclude;

import org.jdeferred.Deferred;

import java.util.ArrayList;

/**
 * Created by sushrutshringarputale on 1/6/17.
 */

/**
 * This is a model representation of a Lobby implemented in the app.
 */
public class Lobby implements Parcelable {
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
    private User owner;
    private int numFree;
    private ArrayList<User> lobbyList;
    private String name;
    private Sport sport;
    private Location location;
    private ArrayList<ChatMessage> messages;
    @Exclude
    private String key;

    //Constructors
    public Lobby(User owner, int numFree, ArrayList<User> lobbyList, String name, Sport sport, Location location, ArrayList<ChatMessage> messages) {
        this.owner = owner;
        this.numFree = numFree;
        this.lobbyList = lobbyList;
        this.name = name;
        this.sport = sport;
        this.location = location;
        this.messages = messages;
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

    @SuppressWarnings("unchecked")
    protected Lobby(Parcel in) {
        numFree = in.readInt();
        owner = in.readParcelable(User.class.getClassLoader());
        lobbyList = in.readArrayList(Lobby.class.getClassLoader());
        name = in.readString();
        sport = (Sport) in.readSerializable();
        location = in.readParcelable(LocationPlus.class.getClassLoader());
        messages = in.readArrayList(ChatMessage.class.getClassLoader());
    }

    //Getters/Setters
    public ArrayList<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<ChatMessage> messages) {
        this.messages = messages;
    }

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
        parcel.writeList(messages);
        parcel.writeString(name);
        parcel.writeSerializable(sport);
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    /**
     *
     * @param ref {@link LobbyRef}
     * This method takes a {@link LobbyRef} object and returns a new lobby.
     * @return Lobby
     */
    public Lobby getLobbyFromRef(LobbyRef ref) {
        ArrayList<User> mArList = new ArrayList<>();
        for (String id : ref.lobbyList.keySet()) {
            mArList.add(new User(ref.lobbyList.get("id")));
        }
        try {
            return new Lobby(new User(ref.ownerId, ref.ownerName), ref.numFree, mArList, ref.name, ref.sport, LocationPlus.getLocationFromRepresentation(ref.getLocation()), new ArrayList<>());
        } catch (NullPointerException e) {
            Log.e("NPE", e.getMessage());
            return new Lobby(new User(ref.ownerId, ref.ownerName), ref.numFree, mArList, ref.name, ref.sport);
        } catch (IllegalArgumentException e) {
            Log.e("IAE", e.getMessage());
        }
        return null;
    }


//    public Deferred initializeLobby() { return new DatabaseConnector().createLobby(this);}

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "owner=" + owner +
                ", numFree=" + numFree +
                ", lobbyList=" + lobbyList +
                ", name='" + name + '\'' +
                ", sport=" + sport +
                ", location=" + location +
                ", messages=" + messages +
                ", key='" + key + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public interface LobbyCrud {
        Deferred createLobby(Lobby lobby);

        Deferred readLobby(Lobby lobby);

        Deferred updateLobby(Lobby lobby);

        Deferred deleteLobby(Lobby lobby);
    }
}
