package com.example.neel.bookingapp.Other;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.neel.bookingapp.Model.Lobby;
import com.example.neel.bookingapp.Model.Sport;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sushrutshringarputale on 3/10/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */


public class NewLobbyDialogFragment extends DialogFragment {
    public RadioGroup sportSelector;
    public EditText lobbyName;
    public Button cancelButton;
    public Button confirmButton;
    public User user;
    public GoogleApiClient mGoogleApiClient;

    public interface OnCompleteListener {
        void onComplete(Lobby lobby);
    }

    private OnCompleteListener mListener;

    // make sure the Activity implemented it
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }


    public NewLobbyDialogFragment() {
    }

    public static NewLobbyDialogFragment newInstance(String title) {
        NewLobbyDialogFragment frag = new NewLobbyDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setTitle("Create New Lobby");
        dialog.setContentView(R.layout.fragment_new_lobby_dialog);
        lobbyName = (EditText) dialog.findViewById(R.id.lobbyNameEditText);
        final User user = (User) getArguments().get("user");
        sportSelector = (RadioGroup) dialog.findViewById(R.id.sportSelectorRadioGroup);
        confirmButton = (Button) dialog.findViewById(R.id.confirmLobbyButton);
        cancelButton = (Button) dialog.findViewById(R.id.cancelLobbyButton);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.i("Location Connection", "Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("Location Connection", "Failed " + connectionResult.getErrorMessage());
                    }
                })
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Lobby mLobby = new Lobby(user);
                    switch (sportSelector.getCheckedRadioButtonId()) {
                        case R.id.footballRadioButton:
                            mLobby.setSport(Sport.FOOTBALL);
                            break;
                        case R.id.badmintonRadioButton:
                            mLobby.setSport(Sport.BADMINTON);
                            break;
                        case R.id.tabletennisRadioButton:
                            mLobby.setSport(Sport.TABLETENNIS);
                            break;
                        default:
                            Log.d("Button", String.valueOf(sportSelector.getCheckedRadioButtonId()));
                            break;
                    }
                    mLobby.setName(lobbyName.getText().toString());
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Log.d("Created Lobby", mLobby.toString());
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("lobby", mLobby);
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(getActivity()), 1, bundle);
                        dialog.dismiss();
                    } else {
                        mLobby.setLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
                        Log.d("Created Lobby", mLobby.toString());
                        mLobby.saveLobby();
                        mListener.onComplete(mLobby);
                        dialog.dismiss();
                    }
            }catch(NullPointerException e){
                    Log.e("NPE at save", e.getMessage());
                    dialog.dismiss();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Lobby mLobby = data.getParcelableExtra("lobby");
                Place place = PlacePicker.getPlace(getContext(), data);
                LatLng latLng = place.getLatLng();
                Location mLocation = new Location("");
                mLocation.setLatitude(latLng.latitude);
                mLocation.setLongitude(latLng.longitude);
                mLobby.setLocation(mLocation);
                mLobby.saveLobby();
                mListener.onComplete(mLobby);
            }
        }
    }
}
