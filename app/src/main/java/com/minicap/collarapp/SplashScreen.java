package com.minicap.collarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT=4875;
    protected PulsatorLayout pulsatorLogo1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        pulsatorLogo1 = findViewById(R.id.pulsatorLogo1);

        pulsatorLogo1.start();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent homeIntent=new Intent(SplashScreen.this,SplashPage.class);
                    startActivity(homeIntent);
                    finish();
                }

            },SPLASH_TIME_OUT);
        }
    }

