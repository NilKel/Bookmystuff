package com.example.neel.bookingapp.Other;

import android.util.Log;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.LobbyRef;
import com.example.neel.bookingapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

/**
 * Created by sushrutshringarputale on 5/13/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

@SuppressWarnings("unchecked")
public final class DatabaseConnector {


    public static Deferred saveUser(User user) {
        Deferred deferred = new DeferredObject();
        Log.i("User class", "updating fields for " + user.id);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + user.id);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Log.d("Retrieved user data", dataSnapshot.toString());
                    User temp = dataSnapshot.getValue(User.class);
                    user.copyData(temp);
                }
                deferred.resolve(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("User data retrieval", "onCancelled", databaseError.toException());
                deferred.reject(false);
            }
        });
        return deferred;
    }

    public static Deferred saveLobby(Lobby lobby) {
        Deferred deferred = new DeferredObject();
        Log.d("Method: saveLobby", lobby.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("lobbies");
        DatabaseReference node = ref.push();
        node.setValue(new LobbyRef().copyData(lobby)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                deferred.resolve(true);
            } else {
                deferred.reject(false);
            }
        });
        return deferred;
    }

    public static Deferred updateUser(User user) {
        Deferred deferred = new DeferredObject();
        Log.i("User", "updating fields for " + user.id);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + user.id);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Log.d("Retrieved user data", dataSnapshot.toString());
                    User temp = dataSnapshot.getValue(User.class);
                    user.copyData(temp);
                }
                deferred.resolve(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.reject(databaseError);
            }
        });
        return deferred;
    }
}
