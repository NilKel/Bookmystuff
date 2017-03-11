package com.example.neel.bookingapp.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.LobbyRef;
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Other.LobbyListAdapter;
import com.example.neel.bookingapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SportFragment extends Fragment {

    //PRIVATE VARIABLES
    private ArrayList<Lobby> lobbies = new ArrayList<>();
    public GoogleApiClient mGoogleApiClient;

    public SportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sport = (Sport) savedInstanceState.getSerializable("ARGUMENT");
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lobby_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        final ListView lobbyList = (ListView) getView().findViewById(R.id.lobbyListView);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.i("Location Connection", "Connected");
                        try {
                            Sport sport = (Sport) getArguments().getSerializable("ARGUMENT");
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                getLobbies(sport, lobbyList);
                            } else {
                                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                getLobbies(sport, location, lobbyList);
                            }
                        }catch (NullPointerException e) {
                            Log.e("NPE", "While getting arguments for Sprt Fragment");
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e("Connection Suspended", "Failed");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("Location Connection", "Failed " + connectionResult.getErrorMessage());
                        Sport sport = (Sport) getArguments().getSerializable("ARGUMENT");
                        getLobbies(sport,lobbyList);
                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getLobbies(Sport sport, Location location, final ListView lobbyList) throws NullPointerException {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies");
        ref.orderByChild("sport").equalTo(sport.name()).orderByChild("location")
                .startAt("Location[fused "+ (location.getLatitude() - 0.5) + ", " + (location.getLongitude() - 0.5))
                .endAt("Location[fused "+ Double.toString(location.getLatitude() + 0.5) + ", " + Double.toString(location.getLongitude() + 0.5))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot != null) {
                            LobbyRef temp = dataSnapshot.getValue(LobbyRef.class);
                            lobbies.add(new Lobby().getLobbyFromRef(temp));
                            lobbyList.setAdapter(new LobbyListAdapter(getContext(), lobbies));
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getLobbies(Sport sport, final ListView lobbyList) throws NullPointerException {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies");
        ref.orderByChild("sport").equalTo(sport.name())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot != null) {
                            LobbyRef temp = dataSnapshot.getValue(LobbyRef.class);
                            lobbies.add(new Lobby().getLobbyFromRef(temp));
                            lobbyList.setAdapter(new LobbyListAdapter(getContext(), lobbies));
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}