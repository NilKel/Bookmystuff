package com.example.neel.bookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseAuth.getInstance().signOut();
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
