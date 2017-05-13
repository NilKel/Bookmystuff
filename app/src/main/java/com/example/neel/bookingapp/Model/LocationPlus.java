package com.example.neel.bookingapp.Model;

import android.location.Location;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sushrutshringarputale on 5/13/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class LocationPlus extends Location {

    public LocationPlus(Location l) {
        super(l);
    }

    public LocationPlus(String provider) {
        super(provider);
    }

    /**
     * @param stringRepresentation Given a string representation, it returns a new {@link Location}
     *                             instance that contains all the data from the string
     */
    public static Location getLocationFromRepresentation(String stringRepresentation) throws IllegalArgumentException {
        Location locationPlus = new LocationPlus("");
        Pattern p = Pattern.compile("$fused\\s(\\d+.\\d+),(\\d+.\\d+) acc=(\\d+)");
        Matcher m = p.matcher(stringRepresentation);
        if (m.find()) {
            locationPlus.setLatitude(Double.parseDouble(m.group(1)));
            locationPlus.setLongitude(Double.parseDouble(m.group(2)));
            locationPlus.setAccuracy(Float.parseFloat(m.group(3)));
        } else {
            throw new IllegalArgumentException();
        }
        return locationPlus;
    }
}
