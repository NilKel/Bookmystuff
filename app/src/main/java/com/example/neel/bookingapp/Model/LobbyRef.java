package com.example.neel.bookingapp.Model;

import java.util.HashMap;

/**
 * Created by sushrutshringarputale on 3/10/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 * @author sushrutshringarputale
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
        lobby.setLocation(LocationPlus.getLocationFromRepresentation(this.location));
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation(){
        return this.location.toString();
    }
}
