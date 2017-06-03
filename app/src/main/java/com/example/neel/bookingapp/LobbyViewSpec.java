package com.example.neel.bookingapp;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.neel.bookingapp.Model.ChatMessage;
import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.Row;
import com.facebook.litho.StateValue;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateInitialState;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.widget.EditText;
import com.facebook.litho.widget.Recycler;
import com.facebook.litho.widget.RecyclerBinder;
import com.facebook.yoga.YogaEdge;

import java.util.ArrayList;

/**
 * Created by sushrutshringarputale on 6/3/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

@LayoutSpec
public class LobbyViewSpec {

    private static RecyclerBinder recyclerBinder;

    @OnCreateLayout
    static ComponentLayout onCreateLayout(
            ComponentContext c,
            @State ArrayList<ChatMessage> messages
    ) {

        recyclerBinder = new RecyclerBinder(c);
        return Column.create(c)
                .widthPercent(100)
                .heightPercent(100)
                .paddingDip(YogaEdge.ALL, 8)
                .backgroundColor(0xe1e1e1)
                .child(
                        Row.create(c)
                                .flexGrow(9)
                                .child(
                                        Recycler.create(c)
                                                .binder(recyclerBinder)
                                                .onScrollListener(new RecyclerView.OnScrollListener() {
                                                    /**
                                                     * Callback method to be invoked when the RecyclerView has been scrolled. This will be
                                                     * called after the scroll has completed.
                                                     * <p>
                                                     * This callback will also be called if visible item range changes after a layout
                                                     * calculation. In that case, dx and dy will be 0.
                                                     *
                                                     * @param recyclerView The RecyclerView which scrolled.
                                                     * @param dx           The amount of horizontal scroll.
                                                     * @param dy           The amount of vertical scroll.
                                                     */
                                                    @Override
                                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                        super.onScrolled(recyclerView, dx, dy);
                                                    }
                                                }).build()
                                )
                ).child(
                        Row.create(c)
                                .flexGrow(1)
                                .widthPercent(100)
                                .marginDip(YogaEdge.ALL, 8)
                                .paddingDip(YogaEdge.ALL, 8)
                                .child(
                                        Column.create(c)
                                                .flexGrow(4)
                                                .background(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? c.getDrawable(R.drawable.message_edittext_drawable) : null)
                                                .child(
                                                        EditText.create(c)
                                                                .maxLines(8)
                                                                .editable(true)
                                                                .build()
                                                ).build()
                                ).child(
                                Column.create(c)
                                        .flexGrow(1)
                                        .background(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? c.getDrawable(R.drawable.ic_message_send_button) : null)
                                        .clickHandler(LobbyView.onSendButtonClick(c))
                                        .build()
                        ).build()
                ).build();
    }

    @OnCreateInitialState
    static void createInitialState(
            ComponentContext c,
            StateValue<ArrayList<ChatMessage>> messages,
            @Prop ArrayList<ChatMessage> initMessages
    ) {
        messages.set(initMessages);
    }

    @OnUpdateState
    static void updateMessagesList(
            StateValue<ArrayList<ChatMessage>> messages,
            @Param ArrayList<ChatMessage> newMessages
    ) {
        newMessages.addAll(messages.get());
        messages.set(newMessages);
        //TODO: Create new message views and commit to recyclerBinder
    }

    @OnUpdateState
    static void addMessage(
            StateValue<ArrayList<ChatMessage>> messages,
            @Param ChatMessage newMessage
    ) {
        ArrayList<ChatMessage> temp = messages.get();
        temp.add(newMessage);
        messages.set(temp);
    }

    @OnEvent(ClickEvent.class)
    static void onSendButtonClick(
            ComponentContext c,
            @FromEvent View view,
            @Prop String message
    ) {
        //TODO: Handle message send
    }


}
