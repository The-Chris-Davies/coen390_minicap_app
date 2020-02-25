package com.minicap.collarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class PositionActivity extends AppCompatActivity {

    private TextView latitudeVal;
    private TextView longitudeVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //initialize views
        latitudeVal = findViewById(R.id.positionActivityLatitudeValue);
        longitudeVal = findViewById(R.id.positionActivityLongitudeValue);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
    }

}
