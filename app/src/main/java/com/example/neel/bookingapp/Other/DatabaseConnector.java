package com.example.neel.bookingapp.Other;

import android.util.Log;

import com.example.neel.bookingapp.Model.ChatMessage;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Model.lobby.Lobby;
import com.example.neel.bookingapp.Model.lobby.LobbyRef;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
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
public final class DatabaseConnector implements User.UserCrud, ChatMessage.ChatMessageCrud, Lobby.LobbyCrud {
    private static final String TAG = "Database connector";

    @Override
    public Deferred<Lobby, DatabaseException, Void> createLobby(Lobby lobby) {
        Deferred<Lobby, DatabaseException, Void> deferred = new DeferredObject();
        Log.d(TAG, "Creating lobby" + lobby.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("lobbies");
        DatabaseReference node = ref.push();
        node.setValue(new LobbyRef().copyData(lobby)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                lobby.setKey(node.getKey());
                deferred.resolve(lobby);
            } else {
                deferred.reject(new DatabaseException(task.getException().toString()));
            }
        });
        return deferred;
    }

    /**
     * @throws NullPointerException     if key is invalid
     * @throws IllegalArgumentException if key is invalid
     * @apiNote The lobby parameter must have a valid key.
     */
    @Override
    public Deferred<Lobby, DatabaseException, Void> readLobby(Lobby lobby) throws NullPointerException, IllegalArgumentException {
        Deferred<Lobby, DatabaseException, Void> deferred = new DeferredObject();
        Log.d(TAG, "reading lobby");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + lobby.getKey());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    lobby.getLobbyFromRef(dataSnapshot.getValue(LobbyRef.class));
                    deferred.resolve(lobby);
                } else {
                    deferred.reject(new DatabaseException("Lobby not found"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }


    /**
     * @param lobby
     * @return Promise. If it succeeds, then the lobby is returned. Else an exception is returned
     * @throws NullPointerException
     * @throws IllegalArgumentException
     * @implNote This method will not update any messages inside the lobby
     * @apiNote This method must receive a valid lobby with a valid key in it
     */
    @Override
    public Deferred<Lobby, DatabaseException, Void> updateLobby(Lobby lobby) throws NullPointerException, IllegalArgumentException {
        Deferred<Lobby, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "updating lobby" + lobby.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + lobby.getKey());
        ref.updateChildren(new LobbyRef().copyData(lobby).toMap(), (databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(lobby);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    /**
     *
     * @apiNote This method must receive a valid lobby with a valid key in it
     * @param lobby
     * @return Promise. If it succeeds, then the lobby is returned. Else an exception is returned
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    @Override
    public Deferred<Void, DatabaseException, Void> deleteLobby(Lobby lobby) throws NullPointerException, IllegalArgumentException {
        Deferred<Void, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "deleting lobby" + lobby.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + lobby.getKey());
        ref.removeValue((databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(null);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    /**
     * @param user
     * @return
     * @throws NullPointerException if user.id is null
     * @apiNote User must have id property set through FirebaseUser
     */
    @Override
    public Deferred<User, DatabaseException, Void> createUser(User user) throws NullPointerException {
        Deferred<User, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "Creating user: " + user.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        if (user.id == null) {
            throw new NullPointerException("User.id cannot be null");
        }
        ref.child(user.id).setValue(user, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(user);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    /**
     * @param user
     * @return returns a deferred object. The promise will resolve with the updated user, or will reject with null
     */
    @Override
    public Deferred<User, DatabaseException, Void> readUser(User user) {
        Deferred deferred = new DeferredObject();
        Log.d(TAG, "updating fields for " + user.id);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + user.id);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Log.d("Retrieved user data", dataSnapshot.toString());
                    user.copyData(dataSnapshot.getValue(User.class));
                    deferred.resolve(user);
                } else {
                    deferred.reject("User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("User data retrieval", "onCancelled", databaseError.toException());
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    @Override
    public Deferred<User, DatabaseException, Void> updateUser(User user) {
        Deferred<User, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "Updating user" + user.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.id);
        ref.updateChildren(user.toMap(), (databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(user);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    @Override
    public Deferred<Void, DatabaseException, Void> deleteUser(User user) {
        Deferred<Void, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "Deleting user: " + user.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + user.id);
        ref.removeValue((databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(null);
            } else {
                deferred.reject(databaseError.toException());
            }
        });

        return deferred;
    }

    /**
     * @param chatMessage
     * @return
     */
    @Override
    public Deferred<ChatMessage, DatabaseException, Void> createChatMessage(ChatMessage chatMessage) {
        Deferred<ChatMessage, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "creating message: " + chatMessage.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + chatMessage.lobby.getKey() + "/messages");
        ref.push().setValue(chatMessage, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                chatMessage.id = databaseReference.getKey();
                deferred.resolve(chatMessage);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    /**
     * @param chatMessage
     * @return
     * @throws NullPointerException if chatMessage.id is null or is chatMessage.lobby.getKey is null
     */
    @Override
    public Deferred<ChatMessage, DatabaseException, Void> readChatMessage(ChatMessage chatMessage) throws NullPointerException {
        Deferred<ChatMessage, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "reading message: " + chatMessage.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + chatMessage.lobby.getKey() + "/messages");
        ref.orderByChild("id").equalTo(chatMessage.id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                deferred.resolve(message);
                ref.removeEventListener(this);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.reject(databaseError.toException());
                ref.removeEventListener(this);
            }
        });
        return deferred;
    }

    @Override
    public Deferred updateChatMessage(ChatMessage chatMessage) {
        Deferred<ChatMessage, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "updating message: " + chatMessage.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + chatMessage.lobby.getKey() + "/messages/" + chatMessage.id);
        ref.setValue(chatMessage, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(chatMessage);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    @Override
    public Deferred deleteChatMessage(ChatMessage chatMessage) {
        Deferred<ChatMessage, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "deleting message: " + chatMessage.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + chatMessage.lobby.getKey() + "/messages/" + chatMessage.id);
        ref.removeValue((databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(null);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }
}
