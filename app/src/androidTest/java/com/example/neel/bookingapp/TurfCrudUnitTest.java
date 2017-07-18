package com.example.neel.bookingapp;

import android.net.Uri;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.neel.bookingapp.Model.Rating;
import com.example.neel.bookingapp.Model.Turf;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.DB.DatabaseConnector;
import com.firebase.client.Firebase;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by sushrutshringarputale on 7/18/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class TurfCrudUnitTest {
    DatabaseConnector databaseConnector;
    Turf turf;
    private Firebase myFirebaseRef;

    @Before
    public void setUp() throws Exception {
        databaseConnector = new DatabaseConnector();
        myFirebaseRef = new Firebase("https://bookmystuff-79c2e.firebaseio.com/");
        AtomicBoolean completed = new AtomicBoolean(false);
        FirebaseAuth.getInstance().signInWithEmailAndPassword("test@email.com", "testpass")
                .addOnSuccessListener(authResult -> {
                    authResult.getUser().updateProfile(new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse("http://lorempixel.com/400/400"))
                            .setDisplayName("Test User")
                            .build()
                    ).addOnSuccessListener(command -> {
                        turf = new Turf(null,
                                "Test turf",
                                new User(FirebaseAuth.getInstance().getCurrentUser()),
                                "Testing a new turf",
                                new Rating(4.5),
                                new HashMap<>(),
                                "",
                                new HashMap<>());
                        completed.set(true);
                    });
                });

        while (!completed.get()) {
            Thread.sleep(1000);
        }
    }

    @Test
    public void createTurfTest() throws Exception {
        for (int i = 0; i < 100; i++) {
            AtomicBoolean completed = new AtomicBoolean(false);
            turf.setName(UUID.randomUUID().toString());
            turf.setDescription(UUID.randomUUID().toString());
            turf.setRating(new Rating((5)*new Random().nextDouble()));
            databaseConnector.createTurf(turf)
                    .promise().done(result -> {
                Log.d("Create turf result", result.toString());
                databaseConnector.updateTurfLocation(result, new GeoLocation(16.485 + (20.485 - (16.485)) * new Random().nextDouble(), 71.8227 + (75.8227 - (71.8227)) * new Random().nextDouble()))
                        .promise().done(result1 -> {
                    completed.set(true);
                    assertNotNull(result1);
                    Log.d("Update Location Result", result1.toString());
                }).fail(result1 -> {

                    throw result1;
                });
            }).fail(result -> {
                throw result;
            });
            while (!completed.get()) {
                Thread.sleep(10);
            }
        }
    }

    @Test
    public void readTurfTest() throws Exception {
        databaseConnector.readTurf(new Turf("-KpK8kNiYe9un1ZiFvQ4")).promise()
                .done(result -> {
                    Log.d("Read turf", result.toString());
                })
                .fail(result -> {
                    throw result;
                });
    }

    @Test
    public void turfLocationRetrievalTest() throws Exception {
        AtomicBoolean completed = new AtomicBoolean(false);
        databaseConnector.readTurf(new Turf("-KpKLm80thFasjS-SRsm"))
                .promise().done(result -> {
                    result.getLocation().queryAtLocation(new GeoLocation(18.485, 73.8227), 0.0004235)
                            .addGeoQueryEventListener(new GeoQueryEventListener() {
                                @Override
                                public void onKeyEntered(String key, GeoLocation location) {
                                    Log.d(key, location.toString());
                                }

                                @Override
                                public void onKeyExited(String key) {
                                    Log.d(key, "exited");
                                }

                                @Override
                                public void onKeyMoved(String key, GeoLocation location) {
                                    Log.d(key, location.toString());
                                }

                                @Override
                                public void onGeoQueryReady() {
                                    completed.set(true);
                                }

                                @Override
                                public void onGeoQueryError(DatabaseError error) {
                                    throw error.toException();
                                }
                            });
        }).fail(result -> {
            throw result;
        });
        while (!completed.get()) {
            Thread.sleep(1000);
        }
    }
}
