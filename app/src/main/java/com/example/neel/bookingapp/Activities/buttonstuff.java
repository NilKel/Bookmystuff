package com.example.neel.bookingapp.Activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;

import com.example.neel.bookingapp.R;

/**
 * Created by Admin123 on 7/19/2017.
 */

public class buttonstuff extends TabActivity {
    private static int tabIndex = 0;
    private TabHost tabHost;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabHost = getTabHost();

        addTab();

        ((ImageButton)findViewById(R.id.add_tab)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tabIndex++;
                addTab();
            }
        });
    }
    private void addTab(){
        LayoutInflater layoutInflate = LayoutInflater.from(buttonstuff.this);

        Button tabBtn = (Button)layoutInflate.inflate(R.layout.tab_event, null);
        tabBtn.setText("Tab "+tabIndex);
        Intent tabIntent = new Intent(buttonstuff.this, Ctvt.class);

        setupTab(tabBtn, tabIntent,"Tab "+tabIndex);
    }
    protected void setupTab(View tabBtn, Intent setClass,String tag) {
        TabHost.TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabBtn).setContent(setClass);
        tabHost.addTab(setContent);
    }
}