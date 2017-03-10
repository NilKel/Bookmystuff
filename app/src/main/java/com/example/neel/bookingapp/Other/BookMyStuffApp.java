package com.example.neel.bookingapp.Other;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by sushrutshringarputale on 12/21/16.
 */

public class BookMyStuffApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef =  new Firebase("https://bookmystuff-79c2e.firebaseio.com/");

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);



    }
}
