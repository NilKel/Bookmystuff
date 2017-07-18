package com.example.neel.bookingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.util.SortedList;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextSwitcher;

import com.example.neel.bookingapp.Model.ChatMessage;
import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.DatabaseConnector;
import com.example.neel.bookingapp.Other.ERROR_CODES;
import com.example.neel.bookingapp.Other.ErrorHandler;
import com.example.neel.bookingapp.Other.MessageViewAdapter;
import com.example.neel.bookingapp.R;

import java.util.Date;

public class LobbyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LOBBY_FRAGMENT";

    private static final int SWIPE_UP_EVENT = 1;
    private static final int SWIPE_DOWN_EVENT = 2;
    private static int startY = 0;


    private Lobby lobby;
    private User user;

    private EditText messageSendEditText;

    private DatabaseConnector mDatabaseConnector;

    private SortedList<ChatMessage> messages;

    private RecyclerView messageRecyclerView;
    public MessageViewAdapter mMessageViewAdapter;
    private ImageButton mImageButton;
    private DatabaseConnector.MessageCleaner cleaner;
    private FrameLayout mTurfLayout;
    private BottomSheetBehavior mBottomSheetBehavior;

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
        messages = new SortedList<>(ChatMessage.class, new SortedList.Callback<ChatMessage>() {
            @Override
            public int compare(ChatMessage o1, ChatMessage o2) {
                return o1.id.compareTo(o2.id);
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(ChatMessage oldItem, ChatMessage newItem) {
                return false;
            }

            @Override
            public boolean areItemsTheSame(ChatMessage item1, ChatMessage item2) {
                return item1 == item2;
            }

            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }
        });
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
        View view = inflater.inflate(R.layout.fragment_lobby, container, false);
        messageSendEditText = (EditText) view.findViewById(R.id.messageEditText);
        mImageButton = (ImageButton) view.findViewById(R.id.sendMessageButton);
        messageSendEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mImageButton.setAnimation(s.length() == 0 ? AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out) : AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                mImageButton.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE); //TODO: TRIGGER ONLY ON STATE CHANGE
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        messageRecyclerView = (RecyclerView) view.findViewById(R.id.msgListView);
        view.findViewById(R.id.sendMessageButton).setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        mMessageViewAdapter = new MessageViewAdapter(getContext(), messages, user);
        messageRecyclerView.setAdapter(mMessageViewAdapter);
        cleaner = mDatabaseConnector.listenForMessages(lobby, new DatabaseConnector.MessageListener() {
            @Override
            public void onNewMessage(ChatMessage message) {
                messages.add(message);
                messageRecyclerView.smoothScrollToPosition(mMessageViewAdapter.getItemCount());
                mMessageViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                ErrorHandler.handleError(getContext(), e, ERROR_CODES.MESSAGE_RECEIVE_FAILED);
            }
        });
        NestedScrollView nestedScrollView = (NestedScrollView) getActivity().findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);
        mBottomSheetBehavior.setHideable(false);
        TextSwitcher mTurfToggle = (TextSwitcher) nestedScrollView.findViewById(R.id.view_turf_info_textview);
        mTurfToggle.setFactory(() -> new AppCompatTextView(new ContextThemeWrapper(getContext(), R.style.AppTheme), null, 0));
        mTurfToggle.setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        mTurfToggle.setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        mTurfToggle.setText(getActivity().getString(R.string.string_view_turf_information));
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        mTurfToggle.setText(getActivity().getString(R.string.string_browse_turfs));
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        return view;
    }




    @Override
    public void onDetach() {
        super.onDetach();
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        cleaner.cleanupListener();
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendMessageButton && messageSendEditText.getText() != null && messageSendEditText.getText().length() > 0) {
            ChatMessage message = new ChatMessage(user, lobby, "", new Date().getTime(), messageSendEditText.getText().toString());
            //TODO: PROD: add sending animation to the message itself and update that once the message is successfully written. Or add an error message
            mDatabaseConnector.createChatMessage(message);
            messageSendEditText.setText("");
        }
    }

    private static int getMotionEventType(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getY() - startY > 150) {
                return SWIPE_DOWN_EVENT;
            } else if (event.getY() - startY < -150) {
                return SWIPE_UP_EVENT;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
