package com.example.neel.bookingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

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
    private TurfBottomSheetDialogFragment mBottomSheetDialogFragment;

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

//        mTurfLayout.setOnTouchListener((v, event) -> {
//            switch (event.getAction()){
//
//                case MotionEvent.ACTION_DOWN:
//                    startY = (int) event.getY();
//                    break;
//                case MotionEvent.ACTION_UP:
//                    switch (getMotionEventType(event)) {
//                        case 0:
//                            return false;
//                        case SWIPE_DOWN_EVENT:
//                            return false;
//                        case SWIPE_UP_EVENT:
//                            if (mTurfLayout.getVisibility() == View.VISIBLE) {
//                                mTurfLayout.animate().setListener(new Animator.AnimatorListener() {
//                                    @Override
//                                    public void onAnimationStart(Animator animation) {
//                                        mTurfLayout.setScaleY(1f);
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {
//                                        mTurfLayout.setScaleY(0f);
//                                        mTurfLayout.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onAnimationCancel(Animator animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animator animation) {
//
//                                    }
//                                });
//                                return true;
//                            }
//                            break;
//                        default:
//                            break;
//
//                    }
//                    break;
//                default:
//                    break;
//            }
//            return false;
//        });
//        mTurfLayout.setOnClickListener(v1 -> {
//            Log.d("TAG", "Lobby View CLICKED");
//        });
        messageRecyclerView.setAdapter(mMessageViewAdapter);
//        mDatabaseConnector.getNextChatMessages(lobby, null)
//                .promise().done(chatMessages -> {
//                    messages.addAll(chatMessages);
//                mMessageViewAdapter.notifyDataSetChanged();
//        }).fail(e -> ErrorHandler.handleError(getContext(), e, ERROR_CODES.MESSAGE_RECEIVE_FAILED));
        messageRecyclerView.scrollToPosition(0);
        cleaner = mDatabaseConnector.listenForMessages(lobby, new DatabaseConnector.MessageListener() {
            @Override
            public void onNewMessage(ChatMessage message) {
                messages.add(message);
                mMessageViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                ErrorHandler.handleError(getContext(), e, ERROR_CODES.MESSAGE_RECEIVE_FAILED);
            }
        });
        mBottomSheetDialogFragment = TurfBottomSheetDialogFragment.newInstance(lobby);
        if (mBottomSheetDialogFragment.getView() != null) {
            BottomSheetBehavior mBehavior = BottomSheetBehavior.from(mBottomSheetDialogFragment.getView());
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        mBottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), "Turf Bottom Sheet");
        return view;
    }




    @Override
    public void onDetach() {
        super.onDetach();
        mBottomSheetDialogFragment.onDetach();
        cleaner.cleanupListener();
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendMessageButton && messageSendEditText.getText() != null) {
            ChatMessage message = new ChatMessage(user, lobby, "", new Date().getTime(), messageSendEditText.getText().toString());
            //binder.insertItemAt(0, ChatMessageView.create(c).text(message.message).senderName(user.name).date(new Date(message.time)).own(true).build());
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
