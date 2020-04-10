package com.minicap.collarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class startingPage extends AppCompatActivity {

    protected ImageView enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);

        setupUI();
    }

    protected void setupUI()
    {
        enterButton=findViewById(R.id.enterButton);
        enterButton.setOnClickListener(onClickGoNextButton);
    }

    private void sendMessage()
    {
        Intent intent =new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private Button.OnClickListener onClickGoNextButton=new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            sendMessage();
        }
    };
}


