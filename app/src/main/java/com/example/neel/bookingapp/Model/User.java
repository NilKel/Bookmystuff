package com.example.neel.bookingapp.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;

import org.jdeferred.Deferred;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public User(String id, String name, String email, long phNo, String profPic, boolean isOwner, Date birthday, boolean initialized) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phNo = phNo;
        this.profPic = profPic;
        this.isOwner = isOwner;
        this.birthday = birthday;
        this.initialized = initialized;
    }

    public User(String id, String name, String email, long phNo, String profPic, boolean isOwner) {
        this(id, name, email, phNo, profPic, isOwner, new Date(), false);
    }

    public User(FirebaseUser fUser) {
        this(fUser.getUid(), fUser.getDisplayName(), fUser.getEmail(), 0, "", false, new Date(), false);
        try {
            this.profPic = fUser.getPhotoUrl().toString();
        } catch (NullPointerException e) {
            Log.e("Saving User", "User does not have profile picture." + e.getMessage());
        }
    }

    public User() {
        this("", "", "", 0, "", false, new Date(), false);
    }

    public User(String name, String email, long phNo, boolean isOwner) {
        this("", name, email, phNo, "", isOwner, new Date(""), false);
    }

    public User(String id, String name) {
        this(id, name, "", 0, "", false, new Date(), false);
    }


    public User(String id) {
        this(id, "", "", 0, "", false, new Date(), false);
    }

    public User(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.phNo = in.readLong();
        this.profPic = in.readString();
        this.isOwner = in.readByte() == 1;
        this.birthday = new Date(in.readLong());
        this.initialized = in.readByte() == 1;
    }

    public void setBirthday(String date) {
        this.birthday = new Date(date);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phNo=" + phNo +
                ", profPic='" + profPic + '\'' +
                ", isOwner=" + isOwner +
                ", birthday=" + birthday +
                ", initialized=" + initialized +
                '}';
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
            this.initialized = user.initialized;

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
        dest.writeByte((byte) (this.initialized ? 1 : 0));
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", this.id);
        result.put("name", this.name);
        result.put("email", this.email);
        result.put("phNo", this.phNo);
        result.put("profPic", this.profPic);
        result.put("isOwner", this.isOwner);
        result.put("birthday", this.birthday.toString());
        result.put("initialized", this.initialized);
        return result;
    }

    public interface UserCrud {
        Deferred createUser(User user);

        Deferred readUser(User user);

        Deferred updateUser(User user);

        Deferred deleteUser(User user);
    }
}
