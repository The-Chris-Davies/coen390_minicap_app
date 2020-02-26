package com.minicap.collarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class TemperatureActivity extends AppCompatActivity {

    private TextView temperatureVal;
    private TextView timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        temperatureVal = findViewById(R.id.temperatureActivityValueValue);
        timestamp = findViewById(R.id.temperatureActivityTimestampValue);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
    }
}
