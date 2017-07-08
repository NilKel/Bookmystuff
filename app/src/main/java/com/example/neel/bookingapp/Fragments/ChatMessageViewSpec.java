package com.example.neel.bookingapp.Fragments;

import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;

import com.example.neel.bookingapp.Model.ChatMessage;
import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaEdge;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by sushrutshringarputale on 6/3/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

@LayoutSpec
public class ChatMessageViewSpec {

    private static final int BACKGROUND_COLOR = 0xcdcdcd;
    private static final int TXT_COLOR = 0x000000;
    private static final int MAX_RAND_LIMIT = 0x999999;


    @OnCreateLayout
    static ComponentLayout onCreateLayout(
            ComponentContext c,
            @Prop ChatMessage chatMessage,
            @Prop boolean own
    ) {
        return Column.create(c)
                .backgroundColor(own ? 0x123456 : BACKGROUND_COLOR)
                .clickHandler(ChatMessageView.onClick(c))
                .maxWidthDip(200)
                .alignSelf(own ? YogaAlign.FLEX_END : YogaAlign.FLEX_START)
                .alignItems(own ? YogaAlign.FLEX_END : YogaAlign.FLEX_START)
                .marginDip(YogaEdge.ALL, 16)
                .child(
                        Row.create(c)
                                .child(
                                        Text.create(c)
                                                .isSingleLine(true)
                                                .textAlignment(Layout.Alignment.ALIGN_OPPOSITE)
                                                .text(chatMessage.sender.name)
                                                .textColor(new Random().nextInt(MAX_RAND_LIMIT))
                                                .textSizeSp(10)
                                                .ellipsize(TextUtils.TruncateAt.END)
                                                .build()
                                )
                                .build()
                )
                .child(
                        Row.create(c)
                                .widthPercent(100)
                                .marginDip(YogaEdge.ALL, 8)
                                .child(
                                        Text.create(c)
                                                .textSizeSp(15)
                                                .textColor(TXT_COLOR)
                                                .text(chatMessage.message)
                                                .typeface(Typeface.SANS_SERIF)
                                                .build()
                                ).build()
                )
                .child(
                        Row.create(c)
                                .widthPercent(100)
                                .marginDip(YogaEdge.TOP, 8)
                                .child(
                                        Text.create(c)
                                                .text(DateFormat.getDateInstance().format(new Date(chatMessage.time)))
                                                .textAlignment(Layout.Alignment.ALIGN_OPPOSITE)
                                                .build()
                                ).build()
                )
                .build();
    }

    @OnEvent(ClickEvent.class)
    static void onClick(
            ComponentContext c
    ) {
        Log.d("Message clicked", "Yay");
    }
}
