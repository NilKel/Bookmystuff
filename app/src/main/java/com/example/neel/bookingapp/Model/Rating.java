package com.example.neel.bookingapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sushrutshringarputale on 1/5/17.
 */

public class Rating implements Parcelable {
    public static final Creator<Rating> CREATOR = new Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
    private float rating;

    public Rating(float rating) {
        this.rating = rating;
    }

    public Rating() {
        this.rating = 0;
    }

    protected Rating(Parcel in) {
        rating = in.readInt();
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    private void validate() {
        if (this.rating < 0)
            this.rating = 0;
        else if (this.rating > 5)
            this.rating = 5;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(rating);
    }
}

