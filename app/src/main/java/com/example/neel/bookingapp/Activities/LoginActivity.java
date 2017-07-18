package com.example.neel.bookingapp.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.DB.DatabaseConnector;
import com.example.neel.bookingapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.DeviceLoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseException;

import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String TAG = "LoginActivity";
    public final int RC_SIGN_IN = 1;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private TextInputEditText mPasswordView;
    private View mLoginFormView;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private CallbackManager callbackManager;
    private ProgressBar mProgressView;
    private DatabaseConnector mDatabaseConnector;
    private AtomicBoolean loggingIn;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        populateAutoComplete();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseConnector = new DatabaseConnector();

        mProgressView = (ProgressBar) findViewById(R.id.progress_bar);
        loggingIn = new AtomicBoolean(false);


        mEmailView = (AutoCompleteTextView) findViewById(R.id.usernameEditText);
        mPasswordView = (TextInputEditText) findViewById(R.id.passwordEditText);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(Scopes.PROFILE))
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, connectionResult -> {
                    Log.e("Connection Error", connectionResult.getErrorMessage());
                    Toast.makeText(LoginActivity.this, "Network issues. Please check your connection or try again later.", Toast.LENGTH_SHORT).show();
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //The listener for authentication. Once a user completes logging in, this will get
        //called. This method is also called when the user logs out, which is handled appropriately.
        mAuthStateListener = firebaseAuth -> {
            if (loggingIn.get()) {
                return;
            }
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                showProgress(true);
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                mDatabaseConnector.readUser(new User(user)).promise().done(mUser -> {
                    if (mUser.initialized) {
                        launchHomePage();
                    } else {
                        initializeUser(mUser);
                    }
                }).fail(error -> {
                    Log.e(TAG, error.getMessage());
                    Toast.makeText(this, "Something went wrong with retrieving your information. Please contact support", Toast.LENGTH_SHORT).show();
                    mFirebaseAuth.signOut();
                }).always((Promise.State state, User user1, DatabaseException e) -> {
                    showProgress(false);
                    loggingIn.set(false);
                });
            } else {
                // User is signed out
                Log.d("Firebase", "onAuthStateChanged:signed_out");
                Toast.makeText(LoginActivity.this, "Please login to start", Toast.LENGTH_SHORT).show();
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);


        SignInButton signInButton = (SignInButton) findViewById(R.id.googleSignInButtonLogin);
        Log.d("SignInButton", signInButton.toString());

        ClickHandler clickHandler = new ClickHandler();
        signInButton.setOnClickListener(clickHandler);

        Button createAccountButton = (Button) findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(clickHandler);
        DeviceLoginButton loginButton = (DeviceLoginButton) findViewById(R.id.facebookLoginButton);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Login", "Success");
                firebaseAuth(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()), loginResult.getAccessToken(), null);
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


        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.loginButton);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (!loggingIn.getAndSet(true)) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            regularLogin(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private void initializeUser(User mUser) {
        Intent intent = new Intent(LoginActivity.this, OnboardingActivity.class);
        intent.putExtra("user", mUser);
        startActivity(intent);
    }

    private void launchHomePage() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    public void fbLogin(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "user_birthday", "public_profile"));
    }

    private void createAccount() {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
    }

    /**
     * @param username
     * @param password This method is triggered when a user inputs their email and password manually
     */
    private void regularLogin(String username, String password) {
        final ProgressDialog dialog = ProgressDialog.show(this, "Logging in", "Authenticating");
        mFirebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    dialog.dismiss();
                    Log.d("Sign In", "signInWithEmail:Complete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.w("Sign In", "signInWithEmail:failed", task.getException());
                        Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                Toast.LENGTH_SHORT).show();
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
            Log.d("Sign In", "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.d("Result", acct.getDisplayName());
                Log.d("firebase auth", "firebaseAuthWithGooogle:" + acct.getId());
                firebaseAuth(GoogleAuthProvider.getCredential(acct.getIdToken(), null), null, acct);
            } else {
                // Signed out, show unauthenticated UI.
                Toast.makeText(this, "Could not login. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("Could not authenticate", result.toString());
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * @param credential {@link AuthCredential} is the credential that the {@link FirebaseAuth} needs to continue signin
     * @param token
     * @param account
     */
    private void firebaseAuth(AuthCredential credential, @Nullable final AccessToken token, @Nullable final GoogleSignInAccount account) {
        showProgress(true);
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.d("Firebase callback", "signInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w("Sign in failed", "signInWithCredential", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed. Error: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (token != null || account != null) { //If authentication provider was facebook, retrieve user information from facebook.
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            try {
                                User mUser = new User(user.getUid(), user.getDisplayName(), user.getEmail(), 0, user.getPhotoUrl().toString(), false);
                                mDatabaseConnector.createUser(mUser).promise().done(savedUser -> {
                                    mFirebaseAuth.addAuthStateListener(mAuthStateListener);
                                    Log.d(TAG, user.toString());
                                }).fail(error -> {
                                    Log.e(TAG, error.getMessage());
                                });
                            } catch (NullPointerException e) {
                                Log.e("Facebook SignIn NPE: ", e.getMessage());
                            }
                        }
                    }
                });
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private class ClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.googleSignInButtonLogin:
                    googleSignIn();
                    break;
                case R.id.createAccountButton:
                    createAccount();
                    break;
            }
        }
    }
}

