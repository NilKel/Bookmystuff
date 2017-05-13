package com.example.neel.bookingapp.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.neel.bookingapp.Other.DatabaseConnector;
import com.google.firebase.auth.FirebaseUser;

import org.jdeferred.Deferred;

import java.util.Date;

/**
 * Created by sushrutshringarputale on 9/19/16.
 */

public class User implements Parcelable{
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
    public String id;
    public String name;
    public String email;
    public long phNo;
    public String profPic;
    public boolean isOwner;
    public Date birthday;
    public boolean initialized;
    //TODO: Add the initialized support to all methods below as well as in {DatabaseConnector} class. The initialized variable will be set to true if we have all information from a user.


    public User(String id, String name, String email, long phNo, String profPic, boolean isOwner) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phNo = phNo;
        this.profPic = profPic;
        this.isOwner = isOwner;
    }

    public User(FirebaseUser fUser) {
        this.email = fUser.getEmail();
        this.name = fUser.getDisplayName();
        this.id = fUser.getUid();
        try {
            this.profPic = fUser.getPhotoUrl().toString();
        } catch (NullPointerException e) {
            Log.e("Saving User", "User does not have profile picture." + e.getMessage());
        }
    }

    public User() {
    }

    public User(String email, String name, long phNo, boolean isOwner) {
        this.email = email;
        this.name = name;
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
        isOwner = in.readByte() != 0;
    }

    public void setBirthday(String date) {
        this.birthday = new Date(date);
    }

    public void setId(String Id) {
        return;
    }

    @Override
    public String toString() {
        return "User: " + name + " " + id;
    }

    public Deferred saveUser() {
        return DatabaseConnector.saveUser(this);
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        isOwner = isOwner;
    }

    public void copyData(User user) {
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
        dest.writeByte((byte) (this.isOwner ? 1 : 0));
        dest.writeLong(this.birthday.getTime());
    }
}
