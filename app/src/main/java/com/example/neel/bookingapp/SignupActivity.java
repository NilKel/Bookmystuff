package com.example.neel.bookingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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

public class SignupActivity extends FragmentActivity{

    private Firebase myFirebaseRef;
    private User user;
    private EditText name;
    private EditText phoneNumber;
    private EditText email;
    private EditText password;
    private EditText passwordReenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Creates a reference for  your Firebase database
        //Add YOUR Firebase Reference URL instead of the following URL
        Firebase.setAndroidContext(this);
        myFirebaseRef =  new Firebase("https://bookmystuff-79c2e.firebaseio.com/");
    }


    @Override
    protected void onStart() {
        super.onStart();
        name = (EditText) findViewById(R.id.nameEditText);
        phoneNumber = (EditText) findViewById(R.id.phoneNoEditText);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        passwordReenter = (EditText) findViewById(R.id.passwordReenterEditText);
    }

    private boolean validate() {
        return !(name.getText().toString().isEmpty() ||
                phoneNumber.getText().toString().isEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() ||
                password.getText().toString().isEmpty() || passwordReenter.getText().toString().isEmpty() ||
                !password.getText().equals(passwordReenter.getText()));
    }

    protected void setUpUser() {
        user = new User(email.getText().toString(), name.getText().toString(), password.getText().toString(),
                Long.parseLong(phoneNumber.getText().toString()));
    }

//    public void onSignUpClicked(View view){
////        progressBar.setVisibility(View.VISIBLE);
//        setUpUser();
//        //createUser method creates a new user account with the given email and password.
//        //Parameters are :
//        // email - The email for the account to be created
//        // password - The password for the account to be created
//        // handler - A handler which is called with the result of the operation
//        myFirebaseRef.createUser(
//                user.getEmail(),
//                user.getPassword(),
//                new Firebase.ValueResultHandler<Map<String, Object>>() {
//                    @Override
//                    public void onSuccess(Map<String, Object> stringObjectMap) {
//                        user.setId(stringObjectMap.get("uid").toString());
//                        user.saveUser();
//                        myFirebaseRef.unauth();
//                        Toast.makeText(getApplicationContext(), "Your Account has been Created", Toast.LENGTH_LONG).show();
//                        Toast.makeText(getApplicationContext(), "Please Login With your Email and Password", Toast.LENGTH_LONG).show();
////                        progressBar = new ProgressBar(getApplicationContext());
////                        progressBar.setVisibility(View.GONE);
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(FirebaseError firebaseError) {
//                        Log.e("Firebase error", firebaseError.getMessage());
//                        Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
////                        progressBar.setVisibility(View.GONE);
//                    }
//                }
//        );
//    }

    public void clearButtonClicked(View view) {
        name.setText("");
        email.setText("");
        phoneNumber.setText("");
        password.setText("");
        passwordReenter.setText("");
    }

    public void SignUpClicked(View view) {
        if (validate()) {
            final ProgressDialog dialog = ProgressDialog.show(SignupActivity.this, "Processing", "Creating Account");
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
                            dialog.dismiss();
                            user.setId(stringObjectMap.get("uid").toString());
                            user.saveUser();
                            myFirebaseRef.unauth();
                            Toast.makeText(SignupActivity.this, "Your Account has been Created", Toast.LENGTH_LONG).show();
                            Toast.makeText(SignupActivity.this, "Please Login With your Email and Password", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            dialog.dismiss();
                            Log.e("Firebase error", firebaseError.getMessage());
                            Toast.makeText(SignupActivity.this, "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
        else{
            Toast.makeText(SignupActivity.this, "Please make sure all input is valid", Toast.LENGTH_SHORT).show();
        }
    }
}
