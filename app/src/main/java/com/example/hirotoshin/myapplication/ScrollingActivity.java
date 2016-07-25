package com.example.hirotoshin.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;

public class ScrollingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        setView();
    }

    private void setView(){
        ImageButton emotional = (ImageButton)findViewById(R.id.stampEmotional);
        ImageButton physics = (ImageButton)findViewById(R.id.stampphysics);
        ImageButton calture = (ImageButton)findViewById(R.id.stampcalture);
        emotional.setImageResource(R.drawable.stampemotional2);
        physics.setImageResource(R.drawable.stampphysical2);
        calture.setImageResource(R.drawable.stumpculture2);
    }
}
