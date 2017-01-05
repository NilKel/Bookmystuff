package com.example.neel.bookingapp.Model;

/**
 * Created by sushrutshringarputale on 1/5/17.
 */

public class Rating {
    private float rating;

    public Rating(float rating) {
        this.rating = rating;
        validate();
    }

    public Rating() {
        this.rating = 0;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
        validate();
    }

    private void validate() {
        if (this.rating < 0)
            this.rating = 0;
        else if (this.rating > 5)
            this.rating = 5;
    }

}

