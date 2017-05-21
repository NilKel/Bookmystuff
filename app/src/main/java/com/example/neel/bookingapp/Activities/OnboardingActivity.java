package com.example.neel.bookingapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.DatabaseConnector;

/**
 * Created by sushrutshringarputale on 5/21/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class OnboardingActivity extends Activity {

    private User mUser;
    private DatabaseConnector mDatabaseConnector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = getIntent().getParcelableExtra("user");
        mDatabaseConnector = new DatabaseConnector();

        //TODO: Add onboarding and also a screen to collect user data.
        /*NOTE: Only show onboarding screens if:
        // Get the shared preferences
        SharedPreferences preferences =  getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Check if onboarding_complete is false
        if(!preferences.getBoolean("onboarding_complete",false)) {..... }

        Once onboarded, set the onboarded status for the device as follows:
        SharedPreferences preferences =
            getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Set onboarding_complete to true
        preferences.edit()
            .putBoolean("onboarding_complete",true).apply();
         */

        /*
        Always show this part if this activity launches.
        Have the user add all their details and update those details in mUser.
        Also for this user now, set initialized to true
        Then call:
        mDatabaseConnector.updateUser(mUser).promise().done(user -> {launch mainActivity. Ensure that the intent it expects is satisfied})
        .fail(error -> {Deal with error};


        * */
        startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
    }
}
