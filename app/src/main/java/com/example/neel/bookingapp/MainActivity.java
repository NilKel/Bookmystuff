package com.example.neel.bookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Added code by Sush
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
    public void openfooty(View v){
        startActivity(new Intent(MainActivity.this, Football.class));
    }

    public void badmint(View v){
        startActivity(new Intent(MainActivity.this, Badminton.class));
    }

    public void titty(View v){
        startActivity(new Intent(MainActivity.this, TableTennis.class));
    }
}
