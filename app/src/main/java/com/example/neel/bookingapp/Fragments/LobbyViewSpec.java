package com.example.neel.bookingapp.Fragments;

import android.support.v7.widget.RecyclerView;

import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Recycler;
import com.facebook.litho.widget.RecyclerBinder;
import com.facebook.yoga.YogaEdge;

/**
 * Created by sushrutshringarputale on 6/3/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

@LayoutSpec
public class LobbyViewSpec {

    @OnCreateLayout
    static ComponentLayout onCreateLayout(
            ComponentContext c,
            @Prop RecyclerBinder binder
    ) {
        return Column.create(c)
                .widthPercent(100)
                .heightPercent(100)
                .paddingDip(YogaEdge.ALL, 8)
                .child(
                        Row.create(c)
                                .heightPercent(100)
                                .widthPercent(100)
                                .child(
                                        Recycler.create(c)
                                                .hasFixedSize(false)
                                                .binder(binder)
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
                                                        //TODO: get more messages
                                                        //Pseudocode
//                                                         if (nearTop of list)
//                                                        dabaconn.getNextChatMessages(recyclerView.getChildAt(0/recyclerView.getChildCount()//Depending on how it lays out)).id)
//                                                                .then(addToRecycleView)

                                                    }
                                                }).build()
                                )
                ).build();
    }
}
