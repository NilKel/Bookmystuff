package com.example.neel.bookingapp.Other;

import android.location.Location;
import android.util.Log;

import com.example.neel.bookingapp.Model.Booking;
import com.example.neel.bookingapp.Model.ChatMessage;
import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Model.Turf;
import com.example.neel.bookingapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.codec.binary.Hex;
import org.jdeferred.Deferred;
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
                if (dataSnapshot != null) {
                    lobby.getLobbyFromRef(dataSnapshot.getValue(Lobby.LobbyRef.class));
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
                    Lobby.LobbyRef temp = dataSnapshot.getValue(Lobby.LobbyRef.class);
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
        ref.orderByChild("lobbyList/id").equalTo(user.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        mListenerMap.put(ref, this);
                        Log.d("Retrieved Lobby", dataSnapshot.toString());
                        if (dataSnapshot != null) {
                            Lobby.LobbyRef temp = dataSnapshot.getValue(Lobby.LobbyRef.class);
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
        FirebaseAuth.getInstance().getCurrentUser().getToken(true)
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
        FirebaseAuth.getInstance().getCurrentUser().getToken(true)
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
        FirebaseDatabase.getInstance().getReference("turfs").child(turf.getId()).setValue(turf).addOnSuccessListener(aVoid -> {
            deferred.resolve(turf);
        }).addOnFailureListener(f -> deferred.reject((DatabaseException) f));
        return deferred;
    }

    @Override
    public Deferred<Turf, DatabaseException, Void> readTurf(Turf turf) {
        Deferred<Turf, DatabaseException, Void> deferred = new DeferredObject<>();
        FirebaseDatabase.getInstance().getReference("turfs").child(turf.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Turf.getTurfFromRef(dataSnapshot.getValue(Turf.TurfRef.class))
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
    public Deferred<Turf, DatabaseException, Void> updateTurf(Turf turf) {
        Deferred<Turf, DatabaseException, Void> deferred = new DeferredObject<>();
        FirebaseDatabase.getInstance().getReference("turfs").child(turf.getId())
                .updateChildren(new Turf.TurfRef().copyData(turf).toMap())
                .addOnSuccessListener(aVoid -> deferred.resolve(turf))
                .addOnFailureListener(f -> deferred.reject((DatabaseException) f));
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
}
