package com.example.neel.bookingapp.Other;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.DB.DatabaseConnector;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by sushrutshringarputale on 5/28/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */
public class DatabaseConnectorTest {
    DatabaseConnector databaseConnector;
    User user;

    @Before
    public void setUp() throws Exception {
        databaseConnector = new DatabaseConnector();
        user = new User(FirebaseAuth.getInstance().getCurrentUser());
    }

    @Test
    public void createLobby() throws Exception {
        Lobby lobby = new Lobby();
    }

    @Test
    public void createUser() throws Exception {
    }

    @Test
    public void createChatMessage() throws Exception {
    }

    @Test
    public void createBooking() throws Exception {
    }

    @Test
    public void createTurf() throws Exception {
    }


    @Test
    public void readLobby() throws Exception {
    }

    @Test
    public void updateLobby() throws Exception {
    }

    @Test
    public void deleteLobby() throws Exception {
    }


    @Test
    public void readUser() throws Exception {
    }

    @Test
    public void updateUser() throws Exception {
    }

    @Test
    public void deleteUser() throws Exception {
    }


    @Test
    public void readChatMessage() throws Exception {
    }

    @Test
    public void updateChatMessage() throws Exception {
    }

    @Test
    public void deleteChatMessage() throws Exception {
    }

    @Test
    public void getNextChatMessages() throws Exception {
    }

    @Test
    public void getLobbiesBySport() throws Exception {
    }

    @Test
    public void getLobbiesByUser() throws Exception {
    }

    @Test
    public void getFriends() throws Exception {
    }

    @Test
    public void newFriendRequest() throws Exception {
    }

    @Test
    public void readTurf() throws Exception {
    }

    @Test
    public void updateTurf() throws Exception {
    }

    @Test
    public void deleteTurf() throws Exception {
    }

    @Test
    public void getAvailabilityForLobby() throws Exception {
    }


    @Test
    public void readBooking() throws Exception {
    }

    @Test
    public void updateBooking() throws Exception {
    }

    @Test
    public void deleteBooking() throws Exception {
    }

}