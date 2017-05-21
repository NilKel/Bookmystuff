package com.example.neel.bookingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.neel.bookingapp.Activities.MainActivity;
import com.example.neel.bookingapp.Model.lobby.Lobby;
import com.example.neel.bookingapp.Other.DatabaseConnector;
import com.example.neel.bookingapp.Other.LobbyListAdapter;
import com.example.neel.bookingapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by sushrutshringarputale on 1/5/17.
 */

public class HomeFragment extends Fragment {
    //    public LobbyLauncherInterface lobbyLauncherInterface;
    private String TAG = "HomeFragment";
    private ArrayList<Lobby> lobbyArrayList;
    private DatabaseConnector databaseConnector;

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

        databaseConnector = new DatabaseConnector();

        //Configure event listeners for listView
        lobbyList.setOnItemClickListener((parent, view1, position, id) -> {
            ((MainActivity) getActivity()).startLobby(lobbyArrayList.get(position));
        });

        databaseConnector.getLobbiesByUser(FirebaseAuth.getInstance().getCurrentUser())
                .promise().done(lobbyArrayList -> {
            lobbyList.setAdapter(new LobbyListAdapter(getActivity(), lobbyArrayList));
        }).fail(error -> {
            Log.e(TAG, error.getMessage());
        });
        //Get User's lobbies and display them in a list

    }
}
