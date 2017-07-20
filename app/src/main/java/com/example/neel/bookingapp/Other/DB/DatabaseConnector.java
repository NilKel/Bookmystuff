package com.example.neel.bookingapp.Other.DB;

import android.location.Location;
import android.util.Log;

import com.example.neel.bookingapp.Model.Booking;
import com.example.neel.bookingapp.Model.ChatMessage;
import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Model.Turf;
import com.example.neel.bookingapp.Model.User;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.codec.binary.Hex;
import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sushrutshringarputale on 5/13/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

@SuppressWarnings("unchecked")
public final class DatabaseConnector implements User.IUserCrud, ChatMessage.IChatMessageCrud,
        Lobby.ILobbyCrud, Turf.ITurfCrud, Booking.IBookingCrud {

    //TODO: Setup cleanup with listenerMap for Turf and Booking Cruds

    private static final String TAG = "Database connector";
    private Map<DatabaseReference, ChildEventListener> mListenerMap = new HashMap<>();

    @Override
    public Deferred<Lobby, DatabaseException, Void> createLobby(Lobby lobby) {
        Deferred<Lobby, DatabaseException, Void> deferred = new DeferredObject();
        Log.d(TAG, "Creating lobby" + lobby.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("lobbies");
        DatabaseReference node = ref.push();
        lobby.setLocation(new GeoFire(FirebaseDatabase.getInstance().getReference("lobby-locations/"+node.getKey())));
        node.setValue(new Lobby.LobbyRef().copyData(lobby)).addOnCompleteListener(task -> {
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
                if (dataSnapshot.getValue() != null) {
                    Lobby.LobbyRef mLobbyRef = dataSnapshot.getValue(Lobby.LobbyRef.class);
                    mLobbyRef.location = FirebaseDatabase.getInstance().getReference("lobby-locations");
                    lobby.getLobbyFromRef(mLobbyRef);
                    lobby.setKey(dataSnapshot.getKey());
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
        ref.updateChildren(new Lobby.LobbyRef().copyData(lobby).toMap(), (databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(lobby);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    @Override
    public Deferred<Lobby, DatabaseException, Void> updateLobbyLocation(Lobby lobby, GeoLocation location) {
        Deferred<Lobby, DatabaseException, Void> deferred = new DeferredObject<>();
        lobby.getLocation().setLocation(lobby.getKey(), location, (key, error) -> {
            if (error != null) {
                deferred.reject(error.toException());
            } else {
                deferred.resolve(lobby);
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
        DatabaseReference ref1 = ref.push();
        ref1.updateChildren(chatMessage.toRef().toMap(), (databaseError, databaseReference) -> {
            if (databaseError == null) {
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
                ChatMessage.ChatMessageRef m = dataSnapshot.getValue(ChatMessage.ChatMessageRef.class);
                if (m != null) {
                    m.getChatMessageFromRef().promise().done(deferred::resolve).fail(deferred::reject);
                } else {
                    deferred.reject(new DatabaseException("Chat message not found"));
                }
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + lobby.getKey() + "/messages");
        DeferredManager manager = new DefaultDeferredManager();
        ArrayList<Promise> promises = new ArrayList<>();
        if (lastChatID == null) {//First 20
            final int[] counter = {0};
            ref.orderByKey().limitToFirst(20)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            mListenerMap.put(ref, this);
                            if (dataSnapshot != null) {
                                Promise<ChatMessage, DatabaseException, Void> promise = dataSnapshot.getValue(ChatMessage.ChatMessageRef.class) //TODO: Load key into chatMessage
                                        .getChatMessageFromRef()
                                        .promise();
                                promises.add(promise);
                                        promise.done(messages::add)
                                        .fail(e -> Log.e(TAG, e.getLocalizedMessage()));
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
                            ref.removeEventListener(this);
                            cleanupReferences();
                        }
                    });
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Promise[] promises1 = new Promise[promises.size()];
                    manager.when(promises.toArray(promises1)).done(result -> {
                        deferred.resolve(messages);
                        ref.removeEventListener(this);
                        cleanupReferences();
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    deferred.reject(databaseError.toException());
                    ref.removeEventListener(this);
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
                            dataSnapshot.getValue(ChatMessage.ChatMessageRef.class).getChatMessageFromRef().promise().done(messages::add).fail(e -> Log.e(TAG, e.getLocalizedMessage()));
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

    public MessageCleaner listenForMessages(final Lobby lobby, MessageListener mMessageListener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies/" + lobby.getKey() + "/messages");
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    ChatMessage.ChatMessageRef chatMessageRef = dataSnapshot.getValue(ChatMessage.ChatMessageRef.class);
                    chatMessageRef.key = dataSnapshot.getKey();
                    chatMessageRef.getChatMessageFromRef().promise().done(mMessageListener::onNewMessage).fail(mMessageListener::onError);
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
                mMessageListener.onError(databaseError.toException());
            }
        };
        ref.limitToLast(50).addChildEventListener(eventListener);
        return () -> ref.removeEventListener(eventListener);
    }

    /**
     * @param sport
     * @param location
     * @return
     * @throws NullPointerException
     */
    public Deferred<List<Lobby>, DatabaseException, Void> getLobbiesBySport(final Sport sport, @Nullable Location location, int offset, @Nullable Integer radius) throws NullPointerException {
        Deferred<List<Lobby>, DatabaseException, Void> deferred = new DeferredObject<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies");
        List<Lobby> lobbies = new ArrayList<>();
        if (location != null) {
            new GeoFire(FirebaseDatabase.getInstance().getReference("lobby-locations"))
                    .queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), radius == null ? 15 : radius)
                    .addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            ref.orderByChild("sport").equalTo(sport.name()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot != null) {
                                        Lobby.LobbyRef temp = dataSnapshot.getValue(Lobby.LobbyRef.class);
                                        temp.key = dataSnapshot.getKey();
                                        if (temp.sport == sport && temp.numFree > 0) {
                                            lobbies.add(new Lobby().getLobbyFromRef(temp));
                                        }
                                    }
                                    ref.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    deferred.reject(databaseError.toException());
                                }
                            });
                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {
                            deferred.resolve(lobbies);
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            deferred.reject(error.toException());
                        }
                    });
        } else {
            ref.orderByChild("sport").equalTo(sport.name()).startAt(offset).limitToFirst(20).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    mListenerMap.put(ref, this);
                    if (dataSnapshot != null) {
                        Lobby.LobbyRef temp = dataSnapshot.getValue(Lobby.LobbyRef.class);
                        temp.key = dataSnapshot.getKey();
                        if (temp.sport == sport && temp.numFree > 0) {
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
                    ref.removeEventListener(this);
                    cleanupReferences();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    deferred.reject(databaseError.toException());
                    ref.removeEventListener(this);
                    cleanupReferences();
                }
            });
        }
        return deferred;
    }

    public Deferred<List<Lobby>, DatabaseException, Void> getLobbiesByUser(FirebaseUser user) {
        Deferred<List<Lobby>, DatabaseException, Void> deferred = new DeferredObject<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies");
        List<Lobby> lobbyArrayList = new ArrayList<>();
        ref.orderByChild("lobbyList/id").equalTo(user.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        mListenerMap.put(ref, this);
                        Log.d("Retrieved Lobby", dataSnapshot.toString());
                        if (dataSnapshot.getValue() != null) {
                            Lobby.LobbyRef temp = dataSnapshot.getValue(Lobby.LobbyRef.class);
                            temp.key = dataSnapshot.getKey();
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
                if (!deferred.isResolved()) {
                    deferred.resolve(lobbyArrayList);
                }
                cleanupReferences();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (deferred.isPending()) {
                    deferred.reject(databaseError.toException());
                }
                cleanupReferences();
                //TODO: OnLogout reference cleanup deferred already rejected/resolved
            }
        });
        return deferred;
    }

    private void cleanupReferences() {
        for (Map.Entry<DatabaseReference, ChildEventListener> entry : mListenerMap.entrySet()) {
            entry.getKey().removeEventListener(entry.getValue());
        }
    }

    public Deferred<JSONArray, Exception, Void> getFriends(User user) {
        Deferred<JSONArray, Exception, Void> deferred = new DeferredObject<>();
        Map<String, String> map = new HashMap<>();
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        map.put("token", task.getResult().getToken());
                        ExpressRestClient.get("/friends" + user.id,
                                new RequestParams(),
                                map,
                                new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                        Log.d(TAG, "Status code: " + statusCode);
                                        deferred.resolve(response);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        deferred.reject((Exception) throwable);
                                        Log.d(TAG, errorResponse.toString());
                                    }
                                });
                    }
                });
        return deferred;
    }

    public Deferred<JSONObject, Exception, Void> newFriendRequest(User user1, User user2) {
        Deferred<JSONObject, Exception, Void> deferred = new DeferredObject<>();
        Map<String, String> map = new HashMap<>();
        RequestParams requestParams = new RequestParams();
        requestParams.add("status", "new");
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        map.put("token", task.getResult().getToken());
                        ExpressRestClient.post("/friends/" + user1.id + "/" + user2.id,
                                requestParams,
                                map, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        deferred.resolve(response);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        deferred.reject((Exception) throwable);
                                    }
                                });
                    } else {
                        deferred.reject(task.getException());
                    }
                });
        return deferred;
    }

    private String getDigest(String string) {
        try {
            Mac sha256_hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(("nodejsistherealprogramminglanguage").getBytes("UTF-8"), "HmacSHA256");
            sha256_hmac.init(secret_key);
            return Hex.encodeHexString(sha256_hmac.doFinal(string.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    public Deferred<Turf, DatabaseException, Void> createTurf(Turf turf) {
        Deferred<Turf, DatabaseException, Void> deferred = new DeferredObject<>();
        turf.setId(FirebaseDatabase.getInstance().getReference("turfs").push().getKey());
        turf.setLocation(new GeoFire(FirebaseDatabase.getInstance().getReference("turf-locations")));
        FirebaseDatabase.getInstance().getReference("turfs").child(turf.getId()).updateChildren(new Turf.TurfRef().copyData(turf).toMap()).addOnSuccessListener(aVoid -> {
            deferred.resolve(turf);
        }).addOnFailureListener(f -> deferred.reject((DatabaseException) f));
        return deferred;
    }

    @Override
    public Deferred<Turf, DatabaseException, Void> readTurf(Turf turf) {
        Deferred<Turf, DatabaseException, Void> deferred = new DeferredObject<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("turfs").child(turf.getId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            deferred.reject(new DatabaseException("Data not found"));
                        } else {
                            Turf.TurfRef mTurfRef = dataSnapshot.getValue(Turf.TurfRef.class);
                            mTurfRef.id = dataSnapshot.getKey();
                            mTurfRef.location = FirebaseDatabase.getInstance().getReference("turf-locations");
                            Turf.getTurfFromRef(mTurfRef)
                                    .promise().done(deferred::resolve).fail(deferred::reject).always((state, resolved, rejected) -> {
                                ref.removeEventListener(this);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        deferred.reject(databaseError.toException());
                    }
                });
        return deferred;
    }

    @Override
    public Deferred<Turf, DatabaseException, Void> updateTurf(Turf turf) {
        Deferred<Turf, DatabaseException, Void> deferred = new DeferredObject<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("turfs").child(turf.getId());
        ref
                .updateChildren(new Turf.TurfRef().copyData(turf).toMap())
                .addOnSuccessListener(aVoid -> {
                    turf.setLocation(new GeoFire(ref.child("location")));
                    deferred.resolve(turf);
                })
                .addOnFailureListener(f -> deferred.reject((DatabaseException) f));

        return deferred;
    }

    @Override
    public Deferred<Turf, DatabaseException, Void> updateTurfLocation(Turf turf, GeoLocation location) {
        Deferred<Turf, DatabaseException, Void> deferred = new DeferredObject<>();
        turf.getLocation().setLocation(turf.getId(), location, (key, error) -> {
            if (error == null) {
                deferred.resolve(turf);
            } else {
                deferred.reject(error.toException());
            }
        });
        return deferred;
    }


    @Override
    public Deferred deleteTurf(Turf turf) {
        Deferred<Void, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "deleting turf" + turf.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("turfs/" + turf.getId());
        ref.removeValue((databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(null);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    public Deferred<HashMap<Long, String>, DatabaseException, Void> getAvailabilityForLobby(Turf turf, Date date) {
        Deferred<HashMap<Long, String>, DatabaseException, Void> deferred = new DeferredObject<>();
        FirebaseDatabase.getInstance().getReference("turfs")
                .child(turf.getId()).child("availability").child(Long.toString(date.getTime()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        deferred.resolve(dataSnapshot.getValue(HashMap.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        deferred.reject(databaseError.toException());
                    }
                });
        return deferred;
    }

    @Override
    public Deferred<Booking, DatabaseException, Void> createBooking(Booking booking) {
        Deferred<Booking, DatabaseException, Void> deferred = new DeferredObject<>();
        booking.id = FirebaseDatabase.getInstance().getReference("bookings").push().getKey();
        FirebaseDatabase.getInstance().getReference("bookings")
                .child(booking.id).setValue(new Booking.BookingRef(booking))
                .addOnSuccessListener(aVoid -> deferred.resolve(booking))
                .addOnFailureListener(f -> deferred.reject((DatabaseException) f));
        return deferred;
    }

    @Override
    public Deferred<Booking, DatabaseException, Void> readBooking(Booking booking) {
        Deferred<Booking, DatabaseException, Void> deferred = new DeferredObject<>();
        FirebaseDatabase.getInstance().getReference("bookings").child(booking.id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Booking.getBookingFromRef(dataSnapshot.getValue(Booking.BookingRef.class))
                                .promise().done(deferred::resolve).fail(deferred::reject);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        deferred.reject(databaseError.toException());
                    }
                });
        return deferred;
    }

    @Override
    public Deferred<Booking, DatabaseException, Void> updateBooking(Booking booking) {
        Deferred<Booking, DatabaseException, Void> deferred = new DeferredObject<>();
        FirebaseDatabase.getInstance().getReference("bookings").child(booking.id)
                .updateChildren(new Booking.BookingRef(booking).toMap())
                .addOnSuccessListener(aVoid -> deferred.resolve(booking))
                .addOnFailureListener(f -> deferred.reject((DatabaseException) f));
        return deferred;
    }

    /**
     * @param booking
     * @return
     * @apiNote Generally do not use if you want to keep a history of bookings
     */
    @Override
    public Deferred<Void, DatabaseException, Void> deleteBooking(Booking booking) {
        Deferred<Void, DatabaseException, Void> deferred = new DeferredObject<>();
        Log.d(TAG, "deleting booking" + booking.toString());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bookings/" + booking.id);
        ref.removeValue((databaseError, databaseReference) -> {
            if (databaseError == null) {
                deferred.resolve(null);
            } else {
                deferred.reject(databaseError.toException());
            }
        });
        return deferred;
    }

    public interface MessageListener {
        void onNewMessage(ChatMessage message);
        void onError(Exception e);
    }
}
