package com.example.neel.bookingapp.Other.UIAdapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by sushrutshringarputale on 1/6/17.
 *
 */

public class LobbyUserListAdapter extends ArrayAdapter<User> {

    public ArrayList<User> userList = null;

    public LobbyUserListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public LobbyUserListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public LobbyUserListAdapter(Context context, int resource, User[] objects) {
        super(context, resource, objects);
    }

    public LobbyUserListAdapter(Context context, int resource, int textViewResourceId, User[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public LobbyUserListAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.userList = (ArrayList<User>) objects;
    }

    public LobbyUserListAdapter(Context context, int resource, int textViewResourceId, List<User> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;
        Log.d("trying", "getView called");

        User i = null;
        if (row == null) {
            Log.d("trying", "debug");
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.lobby_list_row, null);
            try {
                i = userList.get(position);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            holder = new UserHolder();

            holder.profileImage = (ImageView)row.findViewById(R.id.lobbyProfileImage);
            holder.name = (TextView)row.findViewById(R.id.lobbyProfileName);

            row.setTag(holder);
        } else {
            holder = (UserHolder) row.getTag();
        }

        if (i != null) {
            try {
                i = userList.get(position);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            Glide.with(parent.getContext()).load(i.profPic)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.profileImage);
            holder.name.setText(i.name);
        } else {
            holder.name.setText(R.string.lobbyViewStdText);
        }
        return row;

    }

    private static class UserHolder {
        ImageView profileImage;
        TextView name;
    }



}
