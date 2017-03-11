package com.example.neel.bookingapp.Model;

/**
 * Created by sushrutshringarputale on 3/9/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class ChatMessage {
    public User sender;
    public Lobby lobby;
    public String id;


    public ChatMessage() {
    }

    public ChatMessage(User sender, Lobby lobby, String id) {
        this.sender = sender;
        this.lobby = lobby;
        this.id = id;
    }
}