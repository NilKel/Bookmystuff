package com.example.neel.bookingapp.Model;

import org.jdeferred.Deferred;

/**
 * Created by sushrutshringarputale on 3/9/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class ChatMessage {
    public User sender;
    public Lobby lobby;
    public String id;
    public String time;


    public ChatMessage(User sender, Lobby lobby, String id, String time) {
        this.sender = sender;
        this.lobby = lobby;
        this.id = id;
        this.time = time;
    }



    public ChatMessage() {
    }


    public ChatMessage(User sender, Lobby lobby, String id) {
        this.sender = sender;
        this.lobby = lobby;
        this.id = id;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "sender=" + sender +
                ", lobby=" + lobby +
                ", id='" + id + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public interface IChatMessageCrud {
        Deferred createChatMessage(ChatMessage chatMessage);

        Deferred readChatMessage(ChatMessage chatMessage);

        Deferred updateChatMessage(ChatMessage chatMessage);

        Deferred deleteChatMessage(ChatMessage chatMessage);
    }
}
