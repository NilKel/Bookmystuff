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

    private ArrayList<Lobby> lobbyList;
    Context mContext;

    public LobbyListAdapter(@NonNull Context context, @NonNull List<Lobby> objects) {
        super(context, R.layout.lobby_list_row, objects);
        this.lobbyList = (ArrayList<Lobby>) objects;
        this.mContext = context;
    }

    private static class ViewHolder {
        ImageView sportType;
        TextView lobbyName;
    }


    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Lobby lobby = (Lobby) getItem(position);
        Snackbar.make(v, "Release date " +lobby.getName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Lobby lobby = (Lobby) getItem(position);

        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.lobby_list_row, parent, false);
            viewHolder.lobbyName = (TextView) convertView.findViewById(R.id.lobbyProfileName);
            viewHolder.sportType = (ImageView) convertView.findViewById(R.id.lobbyProfileImage);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
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
}
