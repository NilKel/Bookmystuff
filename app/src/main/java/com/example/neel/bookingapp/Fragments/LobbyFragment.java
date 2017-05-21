package com.example.neel.bookingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.neel.bookingapp.Model.lobby.Lobby;
import com.example.neel.bookingapp.R;

public class LobbyFragment extends Fragment implements View.OnClickListener {
    private Lobby lobby;
    private ListView messageList;


//    private OnFragmentInteractionListener mListener;

    public LobbyFragment() {
        // Required empty public constructor
    }

    /**
     * @param {@link Lobby} lobby
     * @return new {@link Lobby}
     */
    @NonNull
    public static LobbyFragment newInstance(Lobby lobby) {
        return new LobbyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lobby = getArguments().getParcelable("lobby");
//            lobby.
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        messageList = (ListView) container.findViewById(R.id.msgListView);
        return inflater.inflate(R.layout.fragment_lobby, container, false);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.messageSendButton)
    }
}
