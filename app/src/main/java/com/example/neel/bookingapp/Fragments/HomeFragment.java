package com.example.neel.bookingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.neel.bookingapp.Activities.MainActivity;
import com.example.neel.bookingapp.Model.Lobby;
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
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        final RecyclerView lobbyList = (RecyclerView) view.findViewById(R.id.lobbyListView);
        Log.d(TAG, "Loading");
        LobbyListAdapter adapter = new LobbyListAdapter(getContext(), lobbyArrayList, (v, position) -> {
            ((MainActivity) getActivity()).startLobby(lobbyArrayList.get(position));
        });
        lobbyList.setAdapter(adapter);
        lobbyList.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseConnector = new DatabaseConnector();
        refreshLayout.setOnRefreshListener(() -> {
            databaseConnector.getLobbiesByUser(FirebaseAuth.getInstance().getCurrentUser())
                    .promise().done(lobbyArrayList -> {
                this.lobbyArrayList = (ArrayList<Lobby>) lobbyArrayList;
                adapter.lobbyList = this.lobbyArrayList;
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }).fail(error -> {
                refreshLayout.setRefreshing(false);
                Log.e(TAG, error.getMessage());
            });
        });
        databaseConnector.getLobbiesByUser(FirebaseAuth.getInstance().getCurrentUser())
                .promise().done(lobbyArrayList -> {
            this.lobbyArrayList = (ArrayList<Lobby>) lobbyArrayList;
            adapter.lobbyList = this.lobbyArrayList;
            adapter.notifyDataSetChanged();
        }).fail(error -> {
            Log.e(TAG, error.getMessage());
        });
        //Get User's lobbies and display them in a list

    }
}
