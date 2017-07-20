package com.example.neel.bookingapp.Deprecated;

import android.location.Location;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sushrutshringarputale on 5/13/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

/**
 * Wrapper class for {@link Location}
 */
@Deprecated
public class LocationPlus {


    /**
     * @param stringRepresentation Given a string representation, it returns a new {@link Location}
     *                             instance that contains all the data from the string
     * @throws IllegalArgumentException if the pattern does not match the location string
     */
    public static Location getLocationFromRepresentation(String stringRepresentation) throws IllegalArgumentException {
        Location location = new Location("");
        Pattern p = Pattern.compile("Location\\[fused\\s(-?\\d*\\.\\d*),(-?\\d*\\.\\d*) acc=(\\d*) et=[+|-]\\S*.*\\]");
        Matcher m = p.matcher(stringRepresentation);
        if (m.find()) {
            location.setLatitude(Double.parseDouble(m.group(1)));
            location.setLongitude(Double.parseDouble(m.group(2)));
            location.setAccuracy(Float.parseFloat(m.group(3)));
        } else {
            throw new IllegalArgumentException();
        }
        return location;
    }
}
