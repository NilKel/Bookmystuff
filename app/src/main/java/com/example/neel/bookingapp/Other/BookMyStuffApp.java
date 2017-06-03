package com.example.neel.bookingapp.Other;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.soloader.SoLoader;
import com.firebase.client.Firebase;

/**
 * Created by sushrutshringarputale on 12/21/16.
 */

public class BookMyStuffApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef =  new Firebase("https://bookmystuff-79c2e.firebaseio.com/");

        SoLoader.init(this, false);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);



    }
}
