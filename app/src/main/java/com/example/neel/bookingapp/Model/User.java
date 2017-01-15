package com.example.neel.bookingapp.Model;

import android.media.Image;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.internal.FacebookDialogFragment.TAG;

/**
 * Created by sushrutshringarputale on 9/19/16.
 */

public class User {
    private String Id;
    private String name;
    private String email;
    private long phNo;
    private Uri profPic;
    private String password;
    private boolean isOwner;

    public User(FirebaseUser fUser) {
        this.email = fUser.getEmail();
        this.name = fUser.getDisplayName();
        this.Id = fUser.getUid();

    }

    public User(String email, String name, String password, long phNo, boolean isOwner) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phNo = phNo;
    }

    public User(String email, String name, long phNo, Uri profPicture) {
        this.email = email;
        this.name = name;
        this.phNo = phNo;
        this.profPic = profPicture;
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhNo() {
        return phNo;
    }

    public void setPhNo(long phNo) {
        this.phNo = phNo;
    }

    public Uri getProfPicture() {
        return profPic;
    }

    public void setProfPicture(Uri profPicture) {
        this.profPic = profPicture;
    }

    @Override
    public String toString() {
        return "User: " + getName();
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public User updateFields() {
        final User[] user = {this};
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query query = db.child("users").orderByChild("id").equalTo(this.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    user[0] = singleSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("User data retrieval", "onCancelled", databaseError.toException());
            }
        });
        return user[0];
    }


    public void saveUser() {
        //Add YOUR Firebase Reference URL instead of the following URL
//        Firebase myFirebaseRef = new Firebase("https://bookmystuff-79c2e.firebaseio.com/");
//        myFirebaseRef = myFirebaseRef.child("users").child(getId());
//        myFirebaseRef.setValue(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("users").child(this.getId()).child("email").setValue(this.getEmail());
        db.child("users").child(this.getId()).child("name").setValue(this.getName());
        db.child("users").child(this.getId()).child("PhNo").setValue(this.getPhNo());
        if (user != null) {
            db.child("users").child(this.getId()).child("profPic").setValue(user.getPhotoUrl());
        }
//        db.child("users").child(this.getId()).child("id").setValue(this.getId());
        db.child("users").child(this.getId()).child("isOwner").setValue(false);

//        try {
//            UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
//                    .setDisplayName(this.getName())
//                    .build();
//            user.updateProfile(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Log.d("Profile update: ", "User profile updated.");
//                    }
//                    else {
//                        Log.d("Profile update", "Failed");
//                    }
//                }
//            });
//        } catch (NullPointerException e) {
//            Log.e("User update: ", e.getMessage());
//        }
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}
