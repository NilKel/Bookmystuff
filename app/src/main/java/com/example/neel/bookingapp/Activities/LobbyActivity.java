package com.example.neel.bookingapp.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.LobbyUserListAdapter;
import com.example.neel.bookingapp.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by sushrutshringarputale on 1/6/17.
 */
public class LobbyActivity extends AppCompatActivity {

    private Lobby lobby = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        lobby = getIntent().getParcelableExtra("lobby");
        if (lobby == null) { //Lobby is brand new
            lobby = new Lobby(new User(FirebaseAuth.getInstance().getCurrentUser()));
//            lobby.setLobbyList(new ArrayList<User>());
//            lobby.setOwner(new User(FirebaseAuth.getInstance().getCurrentUser()));
        }

        ListView lobbyList = (ListView) findViewById(R.id.lobbyList);

        LobbyUserListAdapter adapter = new LobbyUserListAdapter(this, R.layout.lobby_list_row, lobby.getLobbyList());

        lobbyList.setAdapter(adapter);

        lobbyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView name = (TextView) adapterView.getChildAt(1);
                if ((name.getText().toString().equals(getResources().getString(R.string.lobbyViewStdText)))){
                    lobby.setNumFree(lobby.getNumFree()+1);
                    name.setText("Searching");
                    view.setClickable(false);
                }
            }
        });
    }
}
