package com.example.neel.bookingapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
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

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    //TODO: MAIN APP LEVEL: Create three fragments for the three sports that will generate the list of lobbies. Create the layout files for those as well. Cleanup all old code files and remove them from the application. git push when done.
}
