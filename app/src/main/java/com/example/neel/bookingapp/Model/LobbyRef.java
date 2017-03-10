package com.example.neel.bookingapp.Model;

import android.location.Location;

import java.util.HashMap;

/**
 * Created by sushrutshringarputale on 3/10/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class LobbyRef {
    public String ownerName;
    public String ownerId;
    public int numFree;
    public HashMap<String, String> lobbyList;
    public String name;
    public Sport sport;
    private Location location;

    public LobbyRef() {
    }

    public LobbyRef copyData(Lobby lobby) {
        this.ownerName = lobby.getOwner().name;
        this.ownerId = lobby.getOwner().id;
        this.numFree = lobby.getNumFree();
        this.lobbyList = new HashMap<>();
        this.location = lobby.getLocation();
        this.name = lobby.getName();
        for (User user : lobby.getLobbyList()) {
            this.lobbyList.put("id", user.id);
        }
        this.sport = lobby.getSport();
        return this;
    }

    public void setLocation(String location) {
        this.location = new Location(location);
    }

    public String getLocation(){
        return this.location.toString();
    }
}
