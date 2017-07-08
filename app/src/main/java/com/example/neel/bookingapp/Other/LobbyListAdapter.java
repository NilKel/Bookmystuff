package com.example.neel.bookingapp.Other;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sushrutshringarputale on 1/14/17.
 */

public class LobbyListAdapter extends RecyclerView.Adapter<LobbyListAdapter.LobbyViewHolder> {

    public ArrayList<Lobby> lobbyList;
    Context mContext;
    private onItemClickListener onItemClickListener;


    public LobbyListAdapter(@NonNull Context context, @NonNull List<Lobby> objects, onItemClickListener onItemClickListener) {
        super();
        this.lobbyList = (ArrayList<Lobby>) objects;
        this.mContext = context;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public LobbyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView mCardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lobby_list_row, parent, false);
        return new LobbyViewHolder(mCardView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link LobbyViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link LobbyViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     *
     * @param holder   The LobbyViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(LobbyViewHolder holder, int position) {
        Lobby lobby = lobbyList.get(position);
        holder.lobbyName.setText(lobby.getName());
        holder.cardView.setOnClickListener(v -> onItemClickListener.onClick(v, position));
        switch (lobby.getSport()) {
            case FOOTBALL:
                holder.sportType.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_football));
                break;
            case BADMINTON:
                holder.sportType.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_badminton));
                break;
            case TABLETENNIS:
                holder.sportType.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_table_tennis));
                break;
            default:
                holder.sportType.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.com_facebook_button_background));
                break;
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return lobbyList.size();
    }

    public interface onItemClickListener {
        void onClick(View v, int position);
    }

    public static class LobbyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView sportType;
        TextView lobbyName;

        LobbyViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            this.sportType = (ImageView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(0);
            this.lobbyName = (TextView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(1);
        }
    }

}
