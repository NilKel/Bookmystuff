package com.example.neel.bookingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

/**
 * Created by sushrutshringarputale on 9/21/16.
 */

public class SignupActivity extends AppCompatActivity {

    private Firebase myFirebaseRef;
    private User user;
    private EditText name;
    private EditText phoneNumber;
    private EditText email;
    private EditText password;
    private EditText passwordReenter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        //Creates a reference for  your Firebase database
        //Add YOUR Firebase Reference URL instead of the following URL
        Firebase.setAndroidContext(this);
        myFirebaseRef =  new Firebase("https://bookmystuff-79c2e.firebaseio.com/");
    }


    @Override
    protected void onStart() {
        super.onStart();
        name = (EditText) findViewById(R.id.editText0);
        phoneNumber = (EditText) findViewById(R.id.editText4);
        email = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
        passwordReenter = (EditText) findViewById(R.id.editText3);
    }

    protected void setUpUser() {
        user = new User(email.getText().toString(), name.getText().toString(), password.getText().toString(),
                Long.parseLong(phoneNumber.getText().toString()));
    }

    public void onSignUpClicked(View view){
//        progressBar.setVisibility(View.VISIBLE);
        setUpUser();
        //createUser method creates a new user account with the given email and password.
        //Parameters are :
        // email - The email for the account to be created
        // password - The password for the account to be created
        // handler - A handler which is called with the result of the operation
        myFirebaseRef.createUser(
                user.getEmail(),
                user.getPassword(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> stringObjectMap) {
                        user.setId(stringObjectMap.get("uid").toString());
                        user.saveUser();
                        myFirebaseRef.unauth();
                        Toast.makeText(getApplicationContext(), "Your Account has been Created", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Please Login With your Email and Password", Toast.LENGTH_LONG).show();
//                        progressBar = new ProgressBar(getApplicationContext());
//                        progressBar.setVisibility(View.GONE);
                        finish();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Log.e("Firebase error", firebaseError.getMessage());
                        Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
//                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }
}
