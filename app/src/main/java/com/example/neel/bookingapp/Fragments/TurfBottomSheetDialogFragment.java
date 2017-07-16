package com.example.neel.bookingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.R;

/**
 * Created by sushrutshringarputale on 7/16/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class TurfBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private Lobby mLobby;

    public static TurfBottomSheetDialogFragment newInstance(Lobby lobby) {
        TurfBottomSheetDialogFragment mFragment = new TurfBottomSheetDialogFragment();
        mFragment.mLobby = lobby;
        return mFragment;
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
        LinearLayout mLayout = (LinearLayout) inflater.inflate(R.layout.bottom_sheet_layout, container);
        //TODO: Set all content based on the lobby and turf information
        return mLayout;
    }
}
