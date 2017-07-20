package com.example.neel.bookingapp.Activities;



import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Ctvt extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(Ctvt.this);
        tv.setText("Ctvt");
        setContentView(tv);
    }
}