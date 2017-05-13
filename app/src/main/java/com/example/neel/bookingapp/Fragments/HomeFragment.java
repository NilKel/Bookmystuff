package com.example.neel.bookingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.LobbyRef;
import com.example.neel.bookingapp.Other.LobbyLauncherInterface;
import com.example.neel.bookingapp.Other.LobbyListAdapter;
import com.example.neel.bookingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by sushrutshringarputale on 1/5/17.
 */

public class HomeFragment extends Fragment {
    private String TAG = "HomeFragment";
    private ArrayList<Lobby> lobbyArrayList;
    public LobbyLauncherInterface lobbyLauncherInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lobby_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        lobbyArrayList = new ArrayList<>();
        final ListView lobbyList = (ListView) view.findViewById(R.id.lobbyListView);
        Log.d(TAG, "Loading");

        //Configure event listeners for listView
        lobbyList.setOnItemClickListener((parent, view1, position, id) -> {
            lobbyLauncherInterface.startLobby(lobbyArrayList.get(position));
        });

        //Get User's lobbies and display them in a list
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies");
        ref.orderByChild("ownerId").equalTo(user.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("Retrieved Lobby", dataSnapshot.toString());
                        if (dataSnapshot != null) {
                            LobbyRef temp = dataSnapshot.getValue(LobbyRef.class);
                            lobbyArrayList.add(new Lobby().getLobbyFromRef(temp));
                            lobbyList.setAdapter(new LobbyListAdapter(getActivity(), lobbyArrayList));
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

                    }
                });
    }
}
