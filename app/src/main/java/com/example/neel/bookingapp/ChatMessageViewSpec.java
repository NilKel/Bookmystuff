package com.example.neel.bookingapp;

import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextUtils;

import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.ResType;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaEdge;

import java.util.Date;
import java.util.Random;

/**
 * Created by sushrutshringarputale on 6/3/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

@LayoutSpec
public class ChatMessageViewSpec {

    private static final int BACKGROUND_COLOR = 0x232323;
    private static final int TXT_COLOR = 0xc4c4c4;
    private static final int MAX_RAND_LIMIT = 0x999999;


    @OnCreateLayout
    static ComponentLayout onCreateLayout(
            ComponentContext c,
            @Prop(resType = ResType.STRING) String text,
            @Prop Date date,
            @Prop String senderName
    ) {
        return Column.create(c)
                .backgroundColor(BACKGROUND_COLOR)
                .maxWidthDip(150)
                .paddingDip(YogaEdge.ALL, 16)
                .marginDip(YogaEdge.ALL, 16)
                .child(
                        Row.create(c)
                                .child(
                                        Text.create(c)
                                                .isSingleLine(true)
                                                .textAlignment(Layout.Alignment.ALIGN_NORMAL)
                                                .text(senderName)
                                                .textColor(new Random().nextInt(MAX_RAND_LIMIT))
                                                .textSizeSp(14)
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
                                                .text(text)
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
                                                .text(date.toString())
                                                .textAlignment(Layout.Alignment.ALIGN_OPPOSITE)
                                                .build()
                                ).build()
                )
                .build();
    }
}
