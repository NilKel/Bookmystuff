package com.example.neel.bookingapp.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.Exclude;

import org.jdeferred.Deferred;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sushrutshringarputale on 1/6/17.
 */

/**
 * This is a model representation of a Lobby implemented in the app.
 *
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
    private Map<String, LobbySlot> lobbyList;
    private String name;
    private Sport sport;
    private Location location; //TODO: Change to GeoFire
    private GeoFire geoFire;
    private ArrayList<ChatMessage> messages;
    @Exclude
    private String key;

    //Constructors
    public Lobby(User owner, int numFree, Map<String, LobbySlot> lobbyList, String name, Sport sport, Location location, ArrayList<ChatMessage> messages, String key) {
        this.owner = owner;
        this.numFree = numFree;
        this.lobbyList = lobbyList;
        this.name = name;
        this.sport = sport;
        this.location = location;
        this.messages = messages;
        this.key = key;
    }

    public Lobby(User owner, int numFree, Map<String, LobbySlot> lobbyList, String name, Sport sport, String key) {
        this.owner = owner;
        this.numFree = numFree;
        this.lobbyList = lobbyList;
        this.name = name;
        this.sport = sport;
        this.key = key;
    }

    public Lobby(User owner) {
        this.owner = owner;
        this.lobbyList = new HashMap<>();
        this.lobbyList.put(Integer.toString(1), (LobbySlot) owner);
    }

    public Lobby() {

    }

    public Lobby(String key) {
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    protected Lobby(Parcel in) {
        owner = in.readParcelable(User.class.getClassLoader());
        numFree = in.readInt();
        lobbyList = in.readHashMap(LobbySlot.class.getClassLoader());
        messages = in.readArrayList(ChatMessage.class.getClassLoader());
        name = in.readString();
        sport = (Sport) in.readSerializable();
        key = in.readString();
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

    public Map<String, LobbySlot> getLobbyList() {
        return lobbyList;
    }

    public void setLobbyList(Map<String, LobbySlot> lobbyList) {
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
        parcel.writeMap(lobbyList);
        parcel.writeList(messages);
        parcel.writeString(name);
        parcel.writeSerializable(sport);
        parcel.writeString(key);
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
        Map<String, LobbySlot> lobbyList = new HashMap<>();
        for (String id : ref.lobbyList.keySet()) {
            LobbySlot slot = new LobbySlot(ref.lobbyList.get(id));
            if (slot.checkIsUser()) {
                lobbyList.put(id, slot);
            } else {
                lobbyList.put(id, new LobbySlot().setString(ref.lobbyList.get(id)));
            }
        }
        try {
            return new Lobby(new User(ref.ownerId, ref.ownerName), ref.numFree, lobbyList, ref.name, ref.sport, LocationPlus.getLocationFromRepresentation(ref.getLocation()), new ArrayList<>(), ref.key);
        } catch (NullPointerException e) {
            Log.e("NPE", e.getMessage());
            return new Lobby(new User(ref.ownerId, ref.ownerName), ref.numFree, lobbyList, ref.name, ref.sport, ref.key);
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

    public interface ILobbyCrud {
        Deferred createLobby(Lobby lobby);

        Deferred readLobby(Lobby lobby);

        Deferred updateLobby(Lobby lobby);

        Deferred deleteLobby(Lobby lobby);
    }

    /**
     * Created by sushrutshringarputale on 3/10/17.
     * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
     *
     * @author sushrutshringarputale
     *         This class is a representation of the {@link Lobby} class as saved in Firebase. All properties are
     *         as primitive as possible.
     */
    public static class LobbyRef {
        public String ownerName;
        public String ownerId;
        public int numFree;
        public HashMap<String, String> lobbyList;
        public String name;
        public Sport sport;
        public String location;
        @Exclude
        public String key;

        public LobbyRef() {
        }

        /**
         * @param lobby This method copies all data from a {@link Lobby} object
         * @return LobbyRef
         */
        public LobbyRef copyData(Lobby lobby) {
            this.ownerName = lobby.getOwner().name;
            this.ownerId = lobby.getOwner().id;
            this.numFree = lobby.getNumFree();
            this.lobbyList = new HashMap<>();
            this.location = lobby.getLocation().toString();
            this.name = lobby.getName();
            for (String index : lobby.getLobbyList().keySet()) {
                this.lobbyList.put(index, lobby.getLobbyList().get(index).id);
            }
            this.sport = lobby.getSport();
            this.key = lobby.key;
            return this;
        }

        @Exclude
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("ownerName", this.ownerName);
            map.put("ownerId", this.ownerId);
            map.put("numFree", this.numFree);
            map.put("lobbyList", this.lobbyList);
            map.put("name", this.name);
            map.put("sport", this.sport);
            map.put("location", this.location);
            map.put("lobbyList", this.lobbyList);
            return map;
        }

        public String getLocation() {
            return this.location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    /**
     * Created by sushrutshringarputale on 6/22/17.
     * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
     */

    public static class LobbySlot extends User {

        private String storedString;

        public LobbySlot(String id) {
            super(id);
        }

        public LobbySlot() {
        }

        public LobbySlot setString(String s) {
            this.storedString = s;
            return this;
        }

        public boolean checkIsUser() {
            return !(Arrays.asList(SlotState.values()).contains(storedString));
        }

        public User getUser() {
            return checkIsUser() ? this : null;
        }

        public SlotState slotState() {
            return checkIsUser() ? null : SlotState.valueOf(storedString);
        }

        public enum SlotState {
            OPEN, CLOSED, FRIEND
        }


    }
}
