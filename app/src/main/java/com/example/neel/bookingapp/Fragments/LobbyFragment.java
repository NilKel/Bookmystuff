package com.example.neel.bookingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.neel.bookingapp.Model.ChatMessage;
import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Other.DatabaseConnector;
import com.example.neel.bookingapp.Other.ERROR_CODES;
import com.example.neel.bookingapp.Other.ErrorHandler;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.EventHandler;
import com.facebook.litho.LithoView;

import java.util.ArrayList;

public class LobbyFragment extends Fragment {
    private static final String TAG = "LOBBY_FRAGMENT";
    private Lobby lobby;
    private ListView messageList;
    private ComponentContext c;
    private EditText messageSendEditText;
    private LithoView lithoView;

    private DatabaseConnector mDatabaseConnector;

    private ArrayList<ChatMessage> messages;

    private EventHandler messageReceivedEventHandler;

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
        messages = new ArrayList<>();
        mDatabaseConnector = new DatabaseConnector();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lobby = getArguments().getParcelable("lobby");
        } else {
            throw new IllegalArgumentException();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        c = new ComponentContext(getContext());
        lithoView = LithoView.create(
                getContext(),
                LobbyView.
                        create(c)
                        .lobbyName(lobby.getName())
                        .initMessages(new ArrayList<>())
                        .build());
        mDatabaseConnector.getNextChatMessages(lobby, null)
                .promise().done(chatMessages -> {
            LobbyView.updateMessagesListAsync(c, new ArrayList<>(chatMessages));
        }).fail(e -> ErrorHandler.handleError(getContext(), e, ERROR_CODES.MESSAGE_RECEIVE_FAILED));

        mDatabaseConnector.listenForMessages(lobby, new DatabaseConnector.MessageListener() {
            @Override
            public void onNewMessage(ChatMessage message) {
                LobbyView.addMessageAsync(c, message);
            }

            @Override
            public void onError(Exception e) {
                ErrorHandler.handleError(getContext(), e, ERROR_CODES.MESSAGE_RECEIVE_FAILED);
            }
        });
        return lithoView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void sendMessage() {

    }
}
