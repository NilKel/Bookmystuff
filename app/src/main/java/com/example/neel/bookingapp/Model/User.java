package com.example.neel.bookingapp.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by sushrutshringarputale on 9/19/16.
 */

public class User implements Parcelable{
    public String id;
    public String name;
    public String email;
    public long phNo;
    public String profPic;
    public String password;
    public boolean isOwner;
    public Date birthday;

    public UserUpdateInterface mUpdateInterface;

    public interface UserUpdateInterface {
        void onCompleteUpdate();
    }


    public User(String id, String name, String email, long phNo, String profPic, String password, boolean isOwner) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phNo = phNo;
        this.profPic = profPic;
        this.password = password;
        this.isOwner = isOwner;
    }

    public User(FirebaseUser fUser) {
        this.email = fUser.getEmail();
        this.name = fUser.getDisplayName();
        this.id = fUser.getUid();
        this.profPic = fUser.getPhotoUrl().toString();
    }

    public User() {
    }

    public void setBirthday(String date) {
        this.birthday = new Date(date);
    }


    public User(String email, String name, String password, long phNo, boolean isOwner) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phNo = phNo;
    }



    public User(String email, String name, long phNo, String profPicture) {
        this.email = email;
        this.name = name;
        this.phNo = phNo;
        this.profPic = profPicture;
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String id) {
        this.id = id;
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        phNo = in.readLong();
        profPic = in.readString();
        password = in.readString();
        isOwner = in.readByte() != 0;
    }

    public void setId(String Id) {
        return;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User: " + name + " " + id;
    }


    public void updateFields(final User user) {
        Log.i("User class", "updating fields for " + user.id);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/"+user.id);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Log.d("Retrieved user data", dataSnapshot.toString());
                    User temp = dataSnapshot.getValue(User.class);
                    user.copyData(temp);
                }
                    mUpdateInterface.onCompleteUpdate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("User data retrieval", "onCancelled", databaseError.toException());
            }
        });
    }


    public void saveUser() {
        //Add YOUR Firebase Reference URL instead of the following URL
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("users").child(this.id).child("email").setValue(this.email);
        db.child("users").child(this.id).child("name").setValue(this.name);
        db.child("users").child(this.id).child("PhNo").setValue(this.phNo);
        if (user != null && user.getPhotoUrl() != null) {
            db.child("users").child(this.id).child("profPic").setValue(user.getPhotoUrl().toString());
        }
//        db.child("users").child(this.getId()).child("id").setValue(this.getId());
        db.child("users").child(this.id).child("isOwner").setValue(false);

    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        isOwner = isOwner;
    }

    private void copyData(User user) {
        try {
            if (this.id == null)
            this.id = user.id;
            if (this.name == null)
            this.name = user.name;
            if (this.email == null)
            this.email = user.email;
            if (this.phNo < user.phNo)
            this.phNo = user.phNo;
            if (this.profPic == null)
            this.profPic = user.profPic;
            this.password = user.password;
            if (this.isOwner != user.isOwner)
            this.isOwner = user.isOwner;
            if (this.birthday==null)
                this.birthday = user.birthday;
        }catch (NullPointerException e) {
            Log.e("NPE: copyData", e.getMessage());
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeLong(this.phNo);
        dest.writeString(this.profPic);
        dest.writeString(this.password);
        dest.writeByte((byte) (this.isOwner ? 1 : 0));
        dest.writeLong(this.birthday.getTime());
    }
}
