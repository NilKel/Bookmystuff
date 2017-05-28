package com.example.neel.bookingapp.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.ListView;

import com.example.neel.bookingapp.Activities.MainActivity;
import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Other.DatabaseConnector;
import com.example.neel.bookingapp.Other.LobbyListAdapter;
import com.example.neel.bookingapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

public class SportFragment extends Fragment {

    public GoogleApiClient mGoogleApiClient;
    public LocationManager locationManager;
    //PRIVATE VARIABLES
    private ArrayList<Lobby> lobbies = new ArrayList<>();
    private ListView lobbyList;
    private DatabaseConnector databaseConnector;

    public SportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.i("Location Connection", "Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i("Location Connection", "suspended. Code: " + i);
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> Log.e("Location Connection", "Failed " + connectionResult.getErrorMessage()))
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
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
        lobbyList.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("Lobby clicked", lobbies.get(position).toString());
            ((MainActivity) getActivity()).startLobby(lobbies.get(position));
        });

        databaseConnector = new DatabaseConnector();
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading lobbies");
        dialog.show();

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            databaseConnector.getLobbiesBySport((Sport) getArguments().getSerializable("ARGUMENT"), null)
                    .promise().done(lobbies1 -> {
                lobbies = (ArrayList<Lobby>) lobbies1;
                lobbyList.setAdapter(new LobbyListAdapter(getContext(), lobbies1));
                dialog.dismiss();
            }).fail(error -> {
                Log.e("SportFragment", error.getMessage());
            });
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i("Location Connection", "Connected");
                    try {
                        Sport sport = (Sport) getArguments().getSerializable("ARGUMENT");
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            location = null;
                        }
                        databaseConnector.getLobbiesBySport(sport, location).promise()
                                .done(lobbies1 -> {
                                    lobbies = (ArrayList<Lobby>) lobbies1;
                                    lobbyList.setAdapter(new LobbyListAdapter(getContext(), lobbies1));
                                    dialog.dismiss();
                                }).fail(error -> {
                            Log.e("SportFragment", error.getMessage());
                            dialog.dismiss();
                        });
                    } catch (NullPointerException e) {
                        Log.e("NPE", e.getMessage());
                        dialog.dismiss();
                    }
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i("Location Connection", "Connected. method: onStatusChanged");
                    Location location = null;
                    try {
                        Sport sport = (Sport) getArguments().getSerializable("ARGUMENT");
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            location = null;
                        }
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        databaseConnector.getLobbiesBySport(sport, location).promise().done(lobbies1 -> {
                            lobbies = (ArrayList<Lobby>) lobbies1;
                            lobbyList.setAdapter(new LobbyListAdapter(getContext(), lobbies1));
                            dialog.dismiss();
                        }).fail(error -> {
                            Log.e("SportFragment", error.getMessage());
                            dialog.dismiss();
                        });
                    } catch (NullPointerException e) {
                        Log.e("NPE", e.getMessage());
                        dialog.dismiss();
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
                            location = null;
                        }
                        databaseConnector.getLobbiesBySport(sport, location).promise().done(lobbies1 -> {
                            lobbies = (ArrayList<Lobby>) lobbies1;
                            lobbyList.setAdapter(new LobbyListAdapter(getContext(), lobbies1));
                            dialog.dismiss();
                        }).fail(error -> {
                            Log.e("SportFragment", error.getMessage());
                            dialog.dismiss();
                        });
                    } catch (NullPointerException e) {
                        Log.e("NPE", e.getMessage());
                        dialog.dismiss();
                    }
                    locationManager.removeUpdates(this);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }
}