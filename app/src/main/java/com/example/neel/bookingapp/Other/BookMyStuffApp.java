package com.example.neel.bookingapp.Other;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import io.fabric.sdk.android.Fabric;

/**
 * Created by sushrutshringarputale on 12/21/16.
 */

public class BookMyStuffApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        //TODO: APP LEVEL: Configure all onFails to route to Error Handler
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef =  new Firebase("https://bookmystuff-79c2e.firebaseio.com/");
//        SoLoader.init(this, false);
//        Stetho.initializeWithDefaults(this);
        AppEventsLogger.activateApp(this);
    }
}
