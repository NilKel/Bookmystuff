package com.example.neel.bookingapp.Other;

import android.app.Application;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.soloader.SoLoader;
import com.facebook.stetho.Stetho;
import com.firebase.client.Firebase;

/**
 * Created by sushrutshringarputale on 12/21/16.
 */

public class BookMyStuffApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //TODO: APP LEVEL: Configure all onFails to route to Error Handler
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef =  new Firebase("https://bookmystuff-79c2e.firebaseio.com/");
        SoLoader.init(this, false);
        Stetho.initializeWithDefaults(this);
        AppEventsLogger.activateApp(this);
    }
}
