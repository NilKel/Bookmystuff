package com.example.neel.bookingapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by sushrutshringarputale on 9/21/16.
 */

public class SignupActivity extends FragmentActivity{

    private FirebaseAuth mAuth;
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
        Log.d("SignupActivity", "Started");

        mAuth = FirebaseAuth.getInstance();
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
        Log.d("Validation: name", Boolean.toString(!name.getText().toString().isEmpty()));
        Log.d("Validation: phone", Boolean.toString(!phoneNumber.getText().toString().isEmpty()));
        Log.d("Validation: email", Boolean.toString(android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()));
        Log.d("Validation: password", Boolean.toString(!(password.getText().toString().isEmpty() || passwordReenter.getText().toString().isEmpty())));
        Log.d("Validation: re-entry", Boolean.toString(password.getText().toString().equals(passwordReenter.getText().toString())));
        return !(name.getText().toString().isEmpty() ||
                phoneNumber.getText().toString().isEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() ||
                password.getText().toString().isEmpty() || passwordReenter.getText().toString().isEmpty() ||
                !password.getText().toString().equals(passwordReenter.getText().toString()));
    }

    protected void setUpUser() {
        user = new User(email.getText().toString(), name.getText().toString(), password.getText().toString(),
                Long.parseLong(phoneNumber.getText().toString()), false);
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


    public void SignUpClicked(final View view) {
        if (validate()) {
            final ProgressDialog dialog = ProgressDialog.show(SignupActivity.this, "Processing", "Creating Account");
            dialog.setCancelable(true);
            setUpUser();

            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();
                            Log.d("createUser:onComplete:", Boolean.toString(task.isSuccessful()));

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                user.setId(task.getResult().getUser().getUid());
                                user.saveUser();
                                Toast.makeText(SignupActivity.this, "Your Account has been Created", Toast.LENGTH_LONG).show();
                                Toast.makeText(SignupActivity.this, "Please Login With your Email and Password", Toast.LENGTH_LONG).show();
                                cancelSignup(view);
                            }
                        }
                    });
        }
            //createUser method creates a new user account with the given email and password.
            //Parameters are :
            // email - The email for the account to be created
            // password - The password for the account to be created
            // handler - A handler which is called with the result of the operation
//            myFirebaseRef.createUser(
//                    user.getEmail(),
//                    user.getPassword(),
//                    new Firebase.ValueResultHandler<Map<String, Object>>() {
//                        @Override
//                        public void onSuccess(Map<String, Object> stringObjectMap) {
//                            dialog.dismiss();
//                            user.setId(stringObjectMap.get("uid").toString());
//                            user.saveUser();
//                            myFirebaseRef.unauth();
//                            Toast.makeText(SignupActivity.this, "Your Account has been Created", Toast.LENGTH_LONG).show();
//                            Toast.makeText(SignupActivity.this, "Please Login With your Email and Password", Toast.LENGTH_LONG).show();
//                            finish();
//                        }
//
//                        @Override
//                        public void onError(FirebaseError firebaseError) {
//                            dialog.dismiss();
//                            Log.e("Firebase error", firebaseError.getMessage());
//                            Toast.makeText(SignupActivity.this, "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//            );
//        }
        else{
            Toast.makeText(SignupActivity.this, "Please make sure all input is valid", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelSignup(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
