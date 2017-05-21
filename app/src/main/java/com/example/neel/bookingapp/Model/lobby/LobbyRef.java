package com.example.neel.bookingapp.Model.lobby;

import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Model.User;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sushrutshringarputale on 3/10/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 * @author sushrutshringarputale
 * This class is a representation of the {@link Lobby} class as saved in Firebase. All properties are
 * as primitive as possible.
 */
public class LobbyRef {
    public String ownerName;
    public String ownerId;
    public int numFree;
    public HashMap<String, String> lobbyList;
    public String name;
    public Sport sport;
    private String location;

    public LobbyRef() {
    }

    /**
     *
     * @param lobby
     * This method copies all data from a {@link Lobby} object
     * @return LobbyRef
     */
    public LobbyRef copyData(Lobby lobby) {
        this.ownerName = lobby.getOwner().name;
        this.ownerId = lobby.getOwner().id;
        this.numFree = lobby.getNumFree();
        this.lobbyList = new HashMap<>();
        this.location = lobby.getLocation().toString();
        this.name = lobby.getName();
        for (User user : lobby.getLobbyList()) {
            this.lobbyList.put("id", user.id);
        }
        this.sport = lobby.getSport();
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
        return map;
    }

    public String getLocation(){
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
