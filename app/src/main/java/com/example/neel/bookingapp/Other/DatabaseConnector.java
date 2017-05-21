package com.example.neel.bookingapp.Other;

import android.location.Location;
import android.util.Log;

import com.example.neel.bookingapp.Model.ChatMessage;
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Model.lobby.Lobby;
import com.example.neel.bookingapp.Model.lobby.LobbyRef;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by sushrutshringarputale on 5/13/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

@SuppressWarnings("unchecked")
public final class DatabaseConnector implements User.UserCrud, ChatMessage.ChatMessageCrud, Lobby.LobbyCrud {
    private static final String TAG = "Database connector";
    private Map<DatabaseReference, ChildEventListener> mListenerMap = new HashMap<>();

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
                    ref.removeEventListener(this);
                } else {
                    deferred.reject(new DatabaseException("Lobby not found"));
                    ref.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.reject(databaseError.toException());
                ref.removeEventListener(this);
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
                db.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("User data retrieval", "onCancelled", databaseError.toException());
                deferred.reject(databaseError.toException());
                db.removeEventListener(this);
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
                mListenerMap.put(ref, this);
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                deferred.resolve(message);
                cleanupReferences();
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
                cleanupReferences();
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

    /**
     * @param lobby
     * @param lastChatID
     * @return
     * @apiNote lobby.key cannot be null
     */
    public Deferred<List<ChatMessage>, DatabaseException, Void> getNextChatMessages(Lobby lobby, @Nullable String lastChatID) throws IllegalArgumentException {
        Deferred<List<ChatMessage>, DatabaseException, Void> deferred = new DeferredObject<>();
        List<ChatMessage> messages = new ArrayList<>();

        try {
            lobby.getKey();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }

        Log.d(TAG, "retreiving 50 messages" + (lastChatID == null ? "" : "after chatMessageID: " + lastChatID));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + lobby.getKey() + "messages");
        if (lastChatID == null) {//First 20
            final int[] counter = {0};
            ref.orderByKey().limitToFirst(20)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            mListenerMap.put(ref, this);
                            messages.add(dataSnapshot.getValue(ChatMessage.class));
                            counter[0]++;
                            if (counter[0] >= 20) {
                                deferred.resolve(messages);
                                cleanupReferences();
                            }
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
                            cleanupReferences();
                        }
                    });
        } else {
            final int[] counter = {0};
            ref.orderByKey().startAt(lastChatID).limitToFirst(20)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            mListenerMap.put(ref, this);
                            messages.add(dataSnapshot.getValue(ChatMessage.class));
                            counter[0]++;
                            if (counter[0] >= 20) {
                                deferred.resolve(messages);
                                cleanupReferences();
                            }
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
                            cleanupReferences();
                        }
                    });
        }
        return deferred;
    }

    /**
     * @param sport
     * @param location
     * @return
     * @throws NullPointerException
     */
    public Deferred<List<Lobby>, DatabaseException, Void> getLobbiesBySport(final Sport sport, @Nullable Location location) throws NullPointerException {
        Deferred<List<Lobby>, DatabaseException, Void> deferred = new DeferredObject<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies");
        Log.d("Location", location.toString());
        List<Lobby> lobbies = new ArrayList<>();
        Query q;
        if (location != null) {
            q = ref.orderByChild("location")
                    .startAt("Location[fused " + (location.getLatitude() - 0.5) + ", " + (location.getLongitude() - 0.5))
                    .endAt("Location[fused " + Double.toString(location.getLatitude() + 0.5) + ", " + Double.toString(location.getLongitude() + 0.5));
        } else {
            q = ref.orderByChild("sport").equalTo(sport.name()).limitToFirst(20);
        }
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mListenerMap.put(ref, this);
                if (dataSnapshot != null) {
                    LobbyRef temp = dataSnapshot.getValue(LobbyRef.class);
                    if (temp.sport == sport) {
                        lobbies.add(new Lobby().getLobbyFromRef(temp));
                    }
                }
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
                cleanupReferences();
            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                deferred.resolve(lobbies);
                cleanupReferences();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.reject(databaseError.toException());
                cleanupReferences();
            }
        });
        return deferred;
    }

    public Deferred<List<Lobby>, DatabaseException, Void> getLobbiesByUser(FirebaseUser user) {
        Deferred<List<Lobby>, DatabaseException, Void> deferred = new DeferredObject<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies");
        List<Lobby> lobbyArrayList = new ArrayList<>();
        ref.orderByChild("ownerId").equalTo(user.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        mListenerMap.put(ref, this);
                        Log.d("Retrieved Lobby", dataSnapshot.toString());
                        if (dataSnapshot != null) {
                            LobbyRef temp = dataSnapshot.getValue(LobbyRef.class);
                            lobbyArrayList.add(new Lobby().getLobbyFromRef(temp));
                        }
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
                        cleanupReferences();
                    }
                });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                deferred.resolve(lobbyArrayList);
                cleanupReferences();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.reject(databaseError.toException());
                cleanupReferences();
            }
        });
        return deferred;
    }

    public void cleanupReferences() {
        for (Map.Entry<DatabaseReference, ChildEventListener> entry : mListenerMap.entrySet()) {
            entry.getKey().removeEventListener(entry.getValue());
        }
    }
}
