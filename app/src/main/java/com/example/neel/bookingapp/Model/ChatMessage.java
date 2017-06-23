package com.example.neel.bookingapp.Model;

import com.example.neel.bookingapp.Other.DatabaseConnector;
import com.google.firebase.database.DatabaseException;

import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

/**
 * Created by sushrutshringarputale on 3/9/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class ChatMessage {
    public User sender;
    public Lobby lobby;
    public String id;
    public Long time;
    public String message;


    public ChatMessage(User sender, Lobby lobby, String id, Long time, String message) {
        this.sender = sender;
        this.lobby = lobby;
        this.id = id;
        this.time = time;
        this.message = message;
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
        return "ChatMessage{" + "sender=" + sender +
                ", lobby=" + lobby +
                ", id='" + id + '\'' +
                ", time=" + time +
                ", message='" + message + '\'' +
                '}';
    }

    public ChatMessageRef toRef() {
        return new ChatMessageRef(this.sender.id, this.lobby.getKey(), this.id, this.time, this.message);
    }

    public interface IChatMessageCrud {
        Deferred createChatMessage(ChatMessage chatMessage);

        Deferred readChatMessage(ChatMessage chatMessage);

        Deferred updateChatMessage(ChatMessage chatMessage);

        Deferred deleteChatMessage(ChatMessage chatMessage);
    }

    public class ChatMessageRef {
        public String sender;
        public String lobby;
        public String id;
        public Long time;
        public String message;

        public ChatMessageRef(String sender, String lobby, String id, Long time, String message) {
            this.sender = sender;
            this.lobby = lobby;
            this.id = id;
            this.time = time;
            this.message = message;
        }

        public Deferred<ChatMessage, DatabaseException, Void> getChatMessageFromRef() {
            Deferred<ChatMessage, DatabaseException, Void> deferred = new DeferredObject<>();
            ChatMessage m = new ChatMessage(new User(this.sender), new Lobby(this.lobby), this.id, this.time, this.message);
            new DatabaseConnector().readUser(m.sender).promise().done(user -> {
                m.sender = user;
                deferred.resolve(m);
            }).fail(deferred::reject);
            return deferred;
        }
    }
}
