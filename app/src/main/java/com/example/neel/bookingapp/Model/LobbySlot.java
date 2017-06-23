package com.example.neel.bookingapp.Model;

import java.util.Arrays;

/**
 * Created by sushrutshringarputale on 6/22/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class LobbySlot extends User {

    private String storedString;


    public LobbySlot(String s) {
        this.storedString = s;
    }

    public boolean isUser() {
        return !(Arrays.asList(SlotState.values()).contains(storedString));
    }

    public User getUser() {
        return isUser() ? this : null;
    }

    public SlotState getSlotState() {
        return isUser() ? null : SlotState.valueOf(storedString);
    }

    public enum SlotState {
        OPEN, CLOSED, FRIEND
    }


}
