package com.minicap.collarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected TextView temperatureTextView;
    protected TextView heartRateTextView;
    protected TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        temperatureTextView = findViewById(R.id.temperatureTextView);
        heartRateTextView = findViewById(R.id.heartRateTextView);
        locationTextView = findViewById(R.id.locationTextView);

        Toast.makeText(MainActivity.this, "Firebase Connection Good", Toast.LENGTH_LONG).show();


    }
}
