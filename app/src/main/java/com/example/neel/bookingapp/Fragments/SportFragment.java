package com.example.neel.bookingapp.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.LobbyRef;
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Other.LobbyListAdapter;
import com.example.neel.bookingapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
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
    public LocationManager locationManager;
    private ListView lobbyList;

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
        lobbyList = (ListView) getView().findViewById(R.id.lobbyListView);
        lobbyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Lobby clicked", lobbies.get(position).toString());
                //NOTE: Will start LobbyFragment once that is designed and functional. This
            }
        });

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location Connection", "Connected");
                try {
                    Sport sport = (Sport) getArguments().getSerializable("ARGUMENT");
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        getLobbies(sport, lobbyList);
                    } else {
                        getLobbies(sport, location, lobbyList);
                    }
                } catch (NullPointerException e) {
                    Log.e("NPE", e.getMessage());
                }
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("Location Connection", "Connected");
                try {
                    Sport sport = (Sport) getArguments().getSerializable("ARGUMENT");
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        getLobbies(sport, lobbyList);
                    } else {
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        getLobbies(sport, location, lobbyList);
                    }
                } catch (NullPointerException e) {
                    Log.e("NPE", e.getMessage());
                }
                locationManager.removeUpdates(this);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("Location Connection", "provider enabled");

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("Location Connection", "provider disabled");
                try {
                    Sport sport = (Sport) getArguments().getSerializable("ARGUMENT");
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || location == null) {
                        getLobbies(sport, lobbyList);
                    } else {
                        getLobbies(sport, location, lobbyList);
                    }
                } catch (NullPointerException e) {
                    Log.e("NPE", e.getMessage());
                }
                locationManager.removeUpdates(this);
            }
        });
//        locationManager.requestSingleUpdate(Criteria.ACCURACY_FINE, );
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getLobbies(final Sport sport, Location location, final ListView lobbyList) throws NullPointerException {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("lobbies");
        Log.d("Location", location.toString());
        ref.orderByChild("location")
                .startAt("Location[fused "+ (location.getLatitude() - 0.5) + ", " + (location.getLongitude() - 0.5))
                .endAt("Location[fused "+ Double.toString(location.getLatitude() + 0.5) + ", " + Double.toString(location.getLongitude() + 0.5))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot != null) {
                            LobbyRef temp = dataSnapshot.getValue(LobbyRef.class);
                            if (temp.sport == sport) {
                                lobbies.add(new Lobby().getLobbyFromRef(temp));
                                lobbyList.setAdapter(new LobbyListAdapter(getContext(), lobbies));
                            }
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
        ref.orderByChild("sport").equalTo(sport.name()).limitToFirst(20)
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