package com.example.neel.bookingapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.DB.DatabaseConnector;
import com.example.neel.bookingapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "Splash activity";
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseConnector mDatabaseConnector = new DatabaseConnector();
        setContentView(R.layout.activity_splash);
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
        } else {
            mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                mDatabaseConnector.readUser(new User(user)).promise().done(mUser -> {
                    if (mUser.initialized) {
                        launchHomePage();
                    } else {
                        initializeUser(mUser);
                    }
                }).fail(error -> {
                    Log.e(TAG, error.getMessage());
                    mFirebaseAuth.signOut();
                    launchLoginPage();
                });
            } else {
                // User is signed out
                Log.d("Firebase", "onAuthStateChanged:signed_out");
                launchLoginPage();
            }
        };
    }

    private void launchLoginPage() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    }

    private void launchHomePage() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }

    private void initializeUser(User mUser) {
        Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        intent.putExtra("user", mUser);
        startActivity(intent);
    }
}
