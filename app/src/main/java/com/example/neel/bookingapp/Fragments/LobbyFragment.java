package com.example.neel.bookingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.neel.bookingapp.Model.ChatMessage;
import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.DatabaseConnector;
import com.example.neel.bookingapp.Other.ERROR_CODES;
import com.example.neel.bookingapp.Other.ErrorHandler;
import com.example.neel.bookingapp.R;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.widget.LinearLayoutInfo;
import com.facebook.litho.widget.RecyclerBinder;

import java.util.ArrayList;
import java.util.Date;

public class LobbyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LOBBY_FRAGMENT";


    private Lobby lobby;
    private User user;

    private ComponentContext c;
    private EditText messageSendEditText;
    private LithoView lithoView;

    private DatabaseConnector mDatabaseConnector;

    private ArrayList<ChatMessage> messages;

    private RecyclerBinder binder;

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
            user = getArguments().getParcelable("user");
        } else {
            throw new IllegalArgumentException();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        c = new ComponentContext(getContext());
        binder = new RecyclerBinder(c, new LinearLayoutInfo(c.getBaseContext(), LinearLayout.VERTICAL, true));
        lithoView = LithoView.create(
                getContext(),
                LobbyView.create(c)
                        .binder(binder)
                        .build());
        mDatabaseConnector.getNextChatMessages(lobby, null)
                .promise().done(chatMessages -> {
            for (ChatMessage message : chatMessages) {
                binder.insertItemAt(0, ChatMessageView.create(c).date(new Date(message.time)).senderName(message.sender.name).own(message.sender.id == user.id).build());
            }
        }).fail(e -> ErrorHandler.handleError(getContext(), e, ERROR_CODES.MESSAGE_RECEIVE_FAILED));

        mDatabaseConnector.listenForMessages(lobby, new DatabaseConnector.MessageListener() {
            @Override
            public void onNewMessage(ChatMessage message) {
                binder.insertItemAt(0, ChatMessageView.create(c).date(new Date(message.time)).senderName(message.sender.name).own(user.id.equals(message.sender.id)).text(message.message).build());
            }

            @Override
            public void onError(Exception e) {
                ErrorHandler.handleError(getContext(), e, ERROR_CODES.MESSAGE_RECEIVE_FAILED);
            }
        });
        View view = inflater.inflate(R.layout.fragment_lobby, container);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.msgListView);
        layout.addView(lithoView);
        messageSendEditText = (EditText) view.findViewById(R.id.messageEditText);
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.messageSendButton && messageSendEditText.getText() != null) {
            ChatMessage message = new ChatMessage(user, lobby, "", new Date().getTime(), messageSendEditText.getText().toString());
            binder.insertItemAt(0, ChatMessageView.create(c).text(message.message).senderName(user.name).date(new Date(message.time)).own(true).build());
            //TODO: PROD: add sending animation to the message itself and update that once the message is successfully written. Or add an error message
            mDatabaseConnector.createChatMessage(message);
        }
    }
}
