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
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.R;

import java.util.ArrayList;

public class SportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static Sport sport;


    //PRIVATE VARIABLES
    private ArrayList<Lobby> lobbies = new ArrayList<>();




    public SportFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            switch ((Sport) getArguments().get("ARGUMENTS")) {
                case FOOTBALL:
                    //Query db for live football lobbies
                    break;
                case BADMINTON:
                    //Query db for live badminton lobbies
                    break;
                case TABLETENNIS:
                    //Query db for live tabletennis lobbies
                    break;
                default:
                    break;
            }
        }catch (NullPointerException e) {
            Log.e("NPE", "While getting arguments for Sprt Fragment");
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lobby_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView lobbyList = (ListView) getView().findViewById(R.id.lobbyListView);
//        lobbyList.setAdapter(new LobbyListAdapter(SportFragment.this, R.layout.lobby_list_row, (List) lobbies));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}