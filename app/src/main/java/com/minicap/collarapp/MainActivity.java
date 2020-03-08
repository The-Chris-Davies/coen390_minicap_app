package com.minicap.collarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firestore.v1.Value;

import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected TextView temperatureTextView;
    protected TextView heartRateTextView;
    protected TextView locationTextView;
    protected TextView latestUpdateTextView;
    protected Button positionButton;

    public static final String POSITION_TIMESTAMP = "timestamp";
    public static final String POSITION_VALUE = "value";
    private static final String TAG = "MainActivity";

    DocumentReference mDocRef = FirebaseFirestore.getInstance().document("dogs/HpwWiJSGHNbOgJtYi2jM/position/YXT3F0MuJpiUzJStuqNN");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize text views
        temperatureTextView = findViewById(R.id.temperatureTextView);
        heartRateTextView = findViewById(R.id.heartRateTextView);
        locationTextView = findViewById(R.id.locationTextView);
        latestUpdateTextView = findViewById(R.id.latestUpdateTextView);
        positionButton = findViewById(R.id.positionButton);

        Toast.makeText(MainActivity.this, "Firebase Connection Good", Toast.LENGTH_LONG).show();

        //Clickable text view to switch to activities
        temperatureActivityIntent(temperatureTextView);
        heartRateActivityIntent(heartRateTextView);
        positionActivityIntent(locationTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Timestamp time = documentSnapshot.getTimestamp(POSITION_TIMESTAMP);
                    GeoPoint value = documentSnapshot.getGeoPoint(POSITION_VALUE);
                    Date date = time.toDate();

                    String longLat = value.toString();
//                    String[] longLatList = longLat.split(",");
//                    String latitude = longLatList[0].substring(longLatList[0].indexOf('=') + 1, longLatList[0].length());
//                    String longitude = longLatList[1].substring(longLatList[1].indexOf('=') + 1, longLatList[1].lastIndexOf(" "));
                    Log.i(TAG, "Geopoint: " + longLat);

                    //Get longitude and latitude from geopoint
                    Double latitudeGet = value.getLatitude();
                    Double longitudeGet = value.getLongitude();
                    Log.i(TAG, latitudeGet.toString());
                    Log.i(TAG, longitudeGet.toString());

                    //Display longitude and latitude
                    locationTextView.setText("Latitude: " + latitudeGet + " Longitude: " + longitudeGet);
                    latestUpdateTextView.setText("Latest update: " + date);
                }
            }
        });
    }

//    public void fetchData() {
//        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
////                    String timestamp = documentSnapshot.getString(POSITION_TIMESTAMP);
////                    String value = documentSnapshot.getString(POSITION_VALUE);
//
//                    Timestamp time = documentSnapshot.getTimestamp(POSITION_TIMESTAMP);
//                    GeoPoint value = documentSnapshot.getGeoPoint(POSITION_VALUE);
//
//                    locationTextView.setText("Timestamp: " + time + " value: " + value);
//                }
//            }
//        });
//    }

    public void temperatureActivityIntent(TextView temperatureTextView) {
        temperatureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TemperatureActivity.class);
                startActivity(intent);
            }
        });
    }

    public void heartRateActivityIntent(TextView heartRateTextView) {
        heartRateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HeartrateActivity.class);
                startActivity(intent);
            }
        });
    }

    public void positionActivityIntent(TextView locationTextView) {
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PositionActivity.class);
                startActivity(intent);
            }
        });
    }
}
