package com.minicap.collarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class HeartrateActivity extends AppCompatActivity {

    private TextView heartrateVal;
    private TextView timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //initialize views
        heartrateVal = findViewById(R.id.heartrateActivityValueValue);
        timestamp = findViewById(R.id.heartrateActivityTimestampValue);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heartrate);
    }
}
