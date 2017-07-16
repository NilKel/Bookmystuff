package com.example.neel.bookingapp.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.neel.bookingapp.Fragments.HomeFragment;
import com.example.neel.bookingapp.Fragments.LobbyFragment;
import com.example.neel.bookingapp.Fragments.SportFragment;
import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.CircleTransform;
import com.example.neel.bookingapp.Other.DatabaseConnector;
import com.example.neel.bookingapp.Other.NewLobbyDialogFragment;
import com.example.neel.bookingapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity implements NewLobbyDialogFragment.OnCompleteListener {

    // urls to load navigation header background image
    private static final String urlNavHeaderBg = "http://i.imgur.com/fe8SLNa.png";


    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_FOOTBALL = "football";
    private static final String TAG_BADMINTON = "badminton";
    private static final String TAG_TABLETENNIS = "tabletennis";
    private static final String TAG_SETTINGS = "settings";


    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;
    public User currentUser;

    //UI elements
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;


    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private boolean inLobby = false;
    private DatabaseConnector mDatabaseConnector;
    private ProgressBar mProgressBar;
    private WeakReference<LobbyFragment> lobbyFragment;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseConnector = new DatabaseConnector();
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        showProgress(true);

        currentUser = new User(FirebaseAuth.getInstance().getCurrentUser());
        //Update the on-device user object from all the data from the database
        mDatabaseConnector.readUser(currentUser).promise().done((user) -> {
            Log.d("Current user", currentUser.toString());
            Log.d("MainActivity", "Started");
            showProgress(false);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            mHandler = new Handler();
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            // Navigation view header
            navHeader = navigationView.getHeaderView(0);
            txtName = (TextView) navHeader.findViewById(R.id.name);
            txtWebsite = (TextView) navHeader.findViewById(R.id.website);
            imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
            imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

            imgProfile.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                startActivity(new Intent(MainActivity.this, UserEditActivity.class), bundle);
            });


            if (!currentUser.isOwner()) {
                navigationView.getMenu().getItem(5).getSubMenu().getItem(2).setVisible(false);
            }

            // load toolbar titles from string resources
            activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createLobby();
                }

                private void createLobby() {
                    FragmentManager fm = getSupportFragmentManager();
                    NewLobbyDialogFragment editNameDialogFragment = NewLobbyDialogFragment.newInstance("Create Lobby");
                    Bundle args = new Bundle();
                    args.putParcelable("user", currentUser);
                    editNameDialogFragment.setArguments(args);
                    editNameDialogFragment.show(fm, "fragment_edit_name");
                }
            });

            // load nav menu header data
            loadNavHeader();


            // initializing navigation menu
            setUpNavigationView();

            if (savedInstanceState == null) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
            } else {
                CURRENT_TAG = savedInstanceState.getString("title");
                navItemIndex = savedInstanceState.getInt("navItemIndex");
                inLobby = savedInstanceState.getBoolean("inLobby");
                //Set header title
                setToolbarTitle();
                selectNavMenu();
                toggleFab();
            }
        }).fail((error) -> {
            Log.e("User data retrieval", error.getMessage());
        });
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText(currentUser.name);

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

//        // Loading profile image
        Glide.with(this).load(currentUser.profPic)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        Runnable mPendingRunnable = () -> {
            // update the main content by replacing fragments
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment(Lobby newLobby) {
        Bundle bundle = new Bundle();
        LobbyFragment mLobby = new LobbyFragment();
        lobbyFragment = new WeakReference<>(mLobby);
        bundle.putParcelable("lobby", newLobby);
        return mLobby;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("title", CURRENT_TAG);
        outState.putInt("navItemIndex", navItemIndex);
        outState.putBoolean("inLobby", inLobby);
        super.onSaveInstanceState(outState);

    }

    private Fragment getHomeFragment() {
        Bundle bundle = new Bundle();
        SportFragment sportFragment = new SportFragment();
        inLobby = false;
        switch (navItemIndex) {
            case 0:
//                 home
                return new HomeFragment();
            case 1:
                // settings
                return new Fragment();
            case 2:
                // football fragment
                bundle.putSerializable("ARGUMENT", Sport.FOOTBALL);
                sportFragment.setArguments(bundle);
                return sportFragment;
            case 3:
                // badminton fragment
                bundle.putSerializable("ARGUMENT", Sport.BADMINTON);
                sportFragment.setArguments(bundle);
                return sportFragment;
            case 4:
                // table tennis fragment
                bundle.putSerializable("ARGUMENT", Sport.TABLETENNIS);
                sportFragment.setArguments(bundle);
                return sportFragment;
            default:
                return new Fragment();
        }
    }

    private void setToolbarTitle() {
        if (navItemIndex >= 0) {
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        }
    }

    private void selectNavMenu() {
        if (navItemIndex >= 0) {
            navigationView.getMenu().getItem(navItemIndex).setChecked(true);
        } else {
            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        // This method will trigger on item Click of navigation menu
        navigationView.setNavigationItemSelectedListener(menuItem -> {

            //Check to see which item was being clicked and perform appropriate action
            switch (menuItem.getItemId()) {
                //Replacing the main content with ContentFragment Which is our Inbox View;
                case R.id.nav_home:
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    break;
                case R.id.nav_football:
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_FOOTBALL;
                    break;
                case R.id.nav_badminton:
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_BADMINTON;
                    break;
                case R.id.nav_table_tennis:
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_TABLETENNIS;
                    break;
                case R.id.nav_settings:
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_SETTINGS;
                    break;
                case R.id.nav_about_us:
                    // launch new intent instead of loading fragment
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")));
                    drawer.closeDrawers();
                    return true;
                case R.id.nav_privacy_policy:
                    // launch new intent instead of loading fragment
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")));
                    drawer.closeDrawers();
                    return true;
                case R.id.manager_mode:
                    //launch manager mode
                    return true;
                default:
                    navItemIndex = 0;
            }

            //Checking if the item is in checked state or not, if not make it in checked state
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
            } else {
                menuItem.setChecked(true);
            }
            //TODO: Integrate friends and update to MySQL DB on first login CODE BELOW. Preferably run async
//            AsyncTask.execute(() -> {
//                for (UserInfo info : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
//                if (info.getProviderId() == FacebookAuthProvider.PROVIDER_ID) {
//                    info.getUid();
//                    new GraphRequest(AccessToken.getCurrentAccessToken(), info.getUid()+"/friends")
//                            .setCallback(graphResponse -> {
//                                graphResponse.getJSONArray()
//                            });
//                }
//            }
//            });
            menuItem.setChecked(true);

            if (menuItem.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            } else {
                loadHomeFragment();
            }

            return true;
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex <= 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
        invalidateOptionsMenu();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.menu, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
//            getMenuInflater().inflate(R.menu.notifications, menu);
        }

        if (inLobby){
            getMenuInflater().inflate(R.menu.menu_lobby, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return true;
        }

        //if user is in a lobby fragment
        if (id == R.id.lobby_share) {
            //TODO: Add shareable functionality with deep linking
            return true;
        }

        if (id == R.id.lobby_members) {
            //TODO: Show fragment with members list
            return true;
        }

        if (id == R.id.lobby_delete) {
            //TODO: show dialog fragment asking if user wants to delete, and then delete
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
//        if (id == R.id.action_mark_all_read) {
//            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
//        }

        // user is in notifications fragment
        // and selected 'Clear All'
//        if (id == R.id.action_clear_notifications) {
//            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
//        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex != 1 && !inLobby)
            fab.show();
        else
            fab.hide();
    }

    /**
     * OnComplete method that is called after newLobbyDialogFragment finishes.
     *
     * @param lobby
     */
    @Override
    public void onComplete(Lobby lobby) {
        lobbyLauncherHelper(lobby);
    }


    public void startLobby(Lobby lobby) {
        lobbyLauncherHelper(lobby);
    }

    private void lobbyLauncherHelper(Lobby lobby) {
        Log.d("Method: onComplete", lobby.toString());
        Runnable mPendingRunnable = () -> {
            // update the main content by replacing fragments
            Fragment fragment = getHomeFragment(lobby);
            Bundle bundle = new Bundle();
            bundle.putParcelable("lobby", lobby);
            bundle.putParcelable("user", currentUser);
            fragment.setArguments(bundle);
            CURRENT_TAG = lobby.getName();
            inLobby = true;
            toggleFab();
            navItemIndex = -1;
            setToolbarTitle();
            selectNavMenu();
            invalidateOptionsMenu();
            getSupportActionBar().setTitle(CURRENT_TAG);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


//    public CoordinatorLayout.LayoutParams getCoordinatorLayoutParams() {
//        return (CoordinatorLayout.LayoutParams) ((CoordinatorLayout) findViewById(R.id.main_coordinator_layout)).getLayoutParams();
//    }
}
