package com.example.neel.bookingapp.Other.UIAdapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.neel.bookingapp.Model.ChatMessage;
import com.example.neel.bookingapp.Model.User;
import com.example.neel.bookingapp.Other.CircleTransform;
import com.example.neel.bookingapp.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sushrutshringarputale on 7/9/17.
 * Copyright (c) Sushrut Shringarputale 2017. All rights reserved.
 */

public class MessageViewAdapter extends RecyclerView.Adapter {


    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final ColorHashMap<Integer> colorMap = new ColorHashMap<>();
    private static final int MAX_COLORS = 6;

    private Context mContext;
    private SortedList<ChatMessage> mMessageList;
    private User currentUser;

    public MessageViewAdapter(Context mContext, SortedList<ChatMessage> mMessageList, User currentUser) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;
        this.currentUser = currentUser;
        colorMap.put(0, 0xffc62828);
        colorMap.put(1, 0xffad1457);
        colorMap.put(2, 0xff6a1b9a);
        colorMap.put(3, 0xff4e342e);
        colorMap.put(4, 0xff1565c0);
        colorMap.put(5, 0xff00838f);
    }


    @Override
    public int getItemViewType(int position) {
        return mMessageList.get(position).sender.id.equals(currentUser.id) ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch(viewType) {
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(mContext).inflate(R.layout.mesg_item_recd, parent, false);
                return new ReceivedMessageHolder(view);
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(mContext).inflate(R.layout.mesg_item_sent, parent, false);
                return new SentMessageHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        AppCompatTextView messageText, timeText, nameText;
        AppCompatImageView profileImage;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (AppCompatTextView) itemView.findViewById(R.id.text_message_body);
            timeText = (AppCompatTextView) itemView.findViewById(R.id.text_message_time);
            nameText = (AppCompatTextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (AppCompatImageView) itemView.findViewById(R.id.image_message_profile);
        }

        public void bind(ChatMessage message) {
            Drawable background = messageText.getBackground();
            int parsed;
            try {
                parsed = Integer.parseInt(message.sender.id.replaceAll("[\\D]", ""));
            } catch (NumberFormatException e) {
                parsed = 0;
            } catch (NullPointerException e) {
                parsed = 0;
            }
            if (background instanceof ShapeDrawable) {
                // cast to 'ShapeDrawable'
                ShapeDrawable shapeDrawable = (ShapeDrawable) background;

                shapeDrawable.getPaint().setColor(colorMap.get(parsed));
            } else if (background instanceof GradientDrawable) {
                // cast to 'GradientDrawable'
                GradientDrawable gradientDrawable = (GradientDrawable) background;
                gradientDrawable.setColor(colorMap.get(parsed));
            } else if (background instanceof ColorDrawable) {
                // alpha value may need to be set again after this call
                ColorDrawable colorDrawable = (ColorDrawable) background;
                colorDrawable.setColor(colorMap.get(parsed));
            }
            messageText.setText(message.message);
//            messageText.setText(AES.decrypt(message.message, message.sender.id + message.time));
            timeText.setText(DateFormat.getDateInstance().format(new Date(message.time)));
            nameText.setText(message.sender.name);
            Glide.with(mContext).load(message.sender.profPic)
                    .crossFade()
                    .bitmapTransform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileImage);
        }
    }

    public class SentMessageHolder extends RecyclerView.ViewHolder {
        AppCompatTextView messageText, timeText;

        public SentMessageHolder (View itemView) {
            super(itemView);
            messageText = (AppCompatTextView) itemView.findViewById(R.id.text_message_body);
            timeText = (AppCompatTextView) itemView.findViewById(R.id.text_message_time);
        }

        public void bind(ChatMessage message) {
            messageText.setText(message.message);
//            messageText.setText(AES.decrypt(message.message, message.sender.id + message.time));
            timeText.setText(DateFormat.getDateInstance().format(new Date(message.time)));
        }
    }

    private static class ColorHashMap<V> extends HashMap<Integer, V> {
        /**
         * Associates the specified value with the specified key in this map.
         * If the map previously contained a mapping for the key, the old
         * value is replaced.
         *
         * @param key   key with which the specified value is to be associated
         * @param value value to be associated with the specified key
         * @return the previous value associated with <tt>key</tt>, or
         * <tt>null</tt> if there was no mapping for <tt>key</tt>.
         * (A <tt>null</tt> return can also indicate that the map
         * previously associated <tt>null</tt> with <tt>key</tt>.)
         */
        @Override
        public V put(Integer key, V value) {
            return super.put(hashColorCode(key), value);
        }

        @Override
        public V get(Object key) {
            return super.get(hashColorCode((Integer) key));
        }

        private int hashColorCode(int input) {
            return input % MAX_COLORS;
        }

    }
}
