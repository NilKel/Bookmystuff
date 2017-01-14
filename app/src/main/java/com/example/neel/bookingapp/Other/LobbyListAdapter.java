package com.example.neel.bookingapp.Other;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.neel.bookingapp.Model.Lobby;

import java.util.List;

/**
 * Created by sushrutshringarputale on 1/14/17.
 */

public class LobbyListAdapter extends ArrayAdapter<Lobby> {
    public LobbyListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public LobbyListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public LobbyListAdapter(Context context, int resource, Lobby[] objects) {
        super(context, resource, objects);
    }

    public LobbyListAdapter(Context context, int resource, int textViewResourceId, Lobby[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public LobbyListAdapter(Context context, int resource, List<Lobby> objects) {
        super(context, resource, objects);
    }

    public LobbyListAdapter(Context context, int resource, int textViewResourceId, List<Lobby> objects) {
        super(context, resource, textViewResourceId, objects);
    }
}
