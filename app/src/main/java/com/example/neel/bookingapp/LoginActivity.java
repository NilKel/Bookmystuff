package com.example.neel.bookingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

/**
 * Created by sushrutshringarputale on 9/20/16.
 */

public class LoginActivity extends FragmentActivity {

    public final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private EditText username, password;
    private Button loginButton;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

//        loginButton = (Button) findViewById(R.id.facebookLoginButton);
//        loginButton.setReadPermissions("email","public_profile");
//        callbackManager = CallbackManager.Factory.create();
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d("Login", "facebook:onSuccess:" + loginResult);
//                firebaseAuth(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d("Login", "facebook:onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d("Login", "facebook:onError", error);
//                Toast.makeText(LoginActivity.this, "There was an error logging in. Please try again.", Toast.LENGTH_SHORT).show();
//            }
//        });

        mfirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Firebase", "onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // User is signed out
                    Log.d("Firebase", "onAuthStateChanged:signed_out");
                    Toast.makeText(LoginActivity.this, "Please login to start", Toast.LENGTH_SHORT).show();
                }
            }
        };

        username = (EditText) findViewById(R.id.usernameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("Connection Error", connectionResult.getErrorMessage());
                        Toast.makeText(LoginActivity.this, "Network issues. Please check your connection or try again later.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mfirebaseAuth.addAuthStateListener(mAuthStateListener);


        SignInButton signInButton = (SignInButton) findViewById(R.id.googleSignInButtonLogin);
        signInButton.setSize(SignInButton.SIZE_ICON_ONLY);
        Log.d("SignInButton", signInButton.toString());

        ClickHandler clickHandler = new ClickHandler();
        signInButton.setOnClickListener(clickHandler);

        Button createAccountButton = (Button) findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(clickHandler);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(clickHandler);

        Button facebookButton = (Button) findViewById(R.id.facebookLoginButton);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Login", "Success");
            }

            @Override
            public void onCancel() {
                Log.d("Facebook Login", "Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Facebook Login", error.getMessage());
            }
        });
//        facebookButton.setBackgroundResource(R.mipmap.facebook_icon);
//        facebookButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//        facebookButton.setCompoundDrawablePadding(0);
//        facebookButton.setPadding(0, 0, 0, 0);
//        facebookButton.setText("");
    }

    public void fbLogin(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("user_photos", "email", "user_birthday", "public_profile"));
    }


    private class ClickHandler implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.googleSignInButtonLogin:
                    googleSignIn();
                    break;
                case R.id.loginButton:
                    if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
                        regularLogin(username.getText().toString(), password.getText().toString());
                    else {
                        Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.createAccountButton:
                    createAccount();
                    break;
            }
        }
    }

    private void createAccount() {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
    }

    private void regularLogin(String username, String password) {
        final ProgressDialog dialog = ProgressDialog.show(this, "Logging in", "Authenticating");
        mfirebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        Log.d("Sign In", "signInWithEmail:Complete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("Sign In", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("Sign In","handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.d("Result", acct.getDisplayName());
                Log.d("firebase auth", "firebaseAuthWithGooogle:" + acct.getId());
                firebaseAuth(GoogleAuthProvider.getCredential(acct.getIdToken(), null));
            } else {
                // Signed out, show unauthenticated UI.
//            updateUI(false);
                Toast.makeText(this, "Could not login. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("Could not authenticate", result.toString());
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuth(AuthCredential credential) {
        final ProgressDialog dialog = ProgressDialog.show(this, "Logging in", "Authenticating");
        mfirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        Log.d("Firebase callback", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Sign in failed", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }


}
