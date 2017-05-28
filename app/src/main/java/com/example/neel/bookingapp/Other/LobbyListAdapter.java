package com.example.neel.bookingapp.Other;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sushrutshringarputale on 1/14/17.
 */

public class LobbyListAdapter extends ArrayAdapter<Lobby> implements View.OnClickListener{

    Context mContext;
    private ArrayList<Lobby> lobbyList;

    public LobbyListAdapter(@NonNull Context context, @NonNull List<Lobby> objects) {
        super(context, R.layout.lobby_list_row, objects);
        this.lobbyList = (ArrayList<Lobby>) objects;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Lobby lobby = getItem(position);
        Snackbar.make(v, "Release date " + lobby.getName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Lobby lobby = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.lobby_list_row, parent, false);
            viewHolder.lobbyName = (TextView) convertView.findViewById(R.id.lobbyProfileName);
            viewHolder.sportType = (ImageView) convertView.findViewById(R.id.lobbyProfileImage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (lobby != null) {
            viewHolder.lobbyName.setText(lobby.getName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                switch (lobby.getSport()) {
                case FOOTBALL:
                    viewHolder.sportType.setImageDrawable(getContext().getDrawable(R.drawable.ic_football));
                    break;
                case BADMINTON:
                    viewHolder.sportType.setImageDrawable(getContext().getDrawable(R.drawable.ic_badminton));
                    break;
                case TABLETENNIS:
                    viewHolder.sportType.setImageDrawable(getContext().getDrawable(R.drawable.ic_table_tennis));
                    break;
                default:
                    viewHolder.sportType.setImageDrawable(getContext().getDrawable(R.drawable.com_facebook_button_background));
                    break;
                }
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView sportType;
        TextView lobbyName;
    }
}
