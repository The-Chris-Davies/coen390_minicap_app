package com.minicap.collarapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    protected TextView temperatureTextView;
    protected TextView heartRateTextView;
    protected TextView locationTextView;
    protected TextView latestUpdateTextView;

    public static final String POSITION_TIMESTAMP = "timestamp";
    public static final String POSITION_VALUE = "value";
    private static final String TAG = "MainActivity";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference mDocRef = db.document("dogs/HpwWiJSGHNbOgJtYi2jM/position/YXT3F0MuJpiUzJStuqNN");

    //First set to dogs (CHANGE TO USER'S DOG THAT IS INPUT)
    //private DocumentReference dogDocRef = db.document("dogs/HpwWiJSGHNbOgJtYi2jM");
    //private CollectionReference posRef = dogDocRef.collection("position");
    private CollectionReference posRef;
    private CollectionReference tempRef;
    private CollectionReference heartRef;
    private CollectionReference extTempRef;
    //private CollectionReference dogRef = db.collection("dogs");

    //List<String> dogIdList = new ArrayList<>();
    //List<String> positionIdList = new ArrayList<>();
    //List<String> posRefsList;

    private Position position;
    private Heartrate heartrate;
    private Temperature temperature;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize text views
        temperatureTextView = findViewById(R.id.temperatureTextView);
        heartRateTextView = findViewById(R.id.heartRateTextView);
        locationTextView = findViewById(R.id.locationTextView);
        latestUpdateTextView = findViewById(R.id.latestUpdateTextView);

        //Initialize position, heartrate and temperature objects
        position = new Position();
        heartrate = new Heartrate();
        temperature = new Temperature();

        //Connect to database and give toast
        Toast.makeText(MainActivity.this, "Firebase Connection Good", Toast.LENGTH_LONG).show();

        //Clickable text view to switch to activities
        temperatureActivityIntent(temperatureTextView);
        heartRateActivityIntent(heartRateTextView);
        positionActivityIntent(locationTextView);

        //Todo: create registration fragment
        //Todo: new path: UserID/user(1,2,3...)/dog/dog(1,2,3...)/position"
        //Todo: new path: UserID/dog1/position"
        //Todo: On Create initialize dog's ID and use it when referencing other collections (temp, heart, pos, etc.)
        //Todo: see below
        //Todo: Use USER'S dog document instead of HpwWiJSGHNbOgJtYi2jM
        posRef = db.collection("dogs/HpwWiJSGHNbOgJtYi2jM/position");
        extTempRef = db.collection("dogs/HpwWiJSGHNbOgJtYi2jM/external_temperature");
        heartRef = db.collection("dogs/HpwWiJSGHNbOgJtYi2jM/heartrate");
        tempRef = db.collection("dogs/HpwWiJSGHNbOgJtYi2jM/temperature");

        //Set up handler for page reloading after 5sec
        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable,5000);
    }

    @Override
    protected void onStart() {
        super.onStart();
            queryLatestPositionDocument();
            updateChangedPositionDocument();
            //queryLatestHeartrateDocument();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

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

    public void queryLatestPositionDocument() {
        Task<QuerySnapshot> query = posRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                        position = documentSnapshot.toObject(Position.class);
                        position.setDocumentID(documentSnapshot.getId());
                        Timestamp timestamp = position.getTimestamp();
                        GeoPoint value = position.getValue();
                        String ID = position.getDocumentID();
                        Log.i(TAG, "time: " + timestamp.toString() + " " + value.toString() + " " + ID);

                        locationTextView.setText("Latitude: " + value.getLatitude()+ " Longitude: " + value.getLongitude());
                        latestUpdateTextView.setText("Latest update: " + timestamp.toDate());
                    }
                });
    }

    public void updateChangedPositionDocument() {
        //Todo: Make an on event listener for updates on a specific dog
        String positionDocument = "dogs/HpwWiJSGHNbOgJtYi2jM/position/" + position.getDocumentID();
        db.document(positionDocument).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            //mDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Timestamp time = documentSnapshot.getTimestamp(POSITION_TIMESTAMP);
                    GeoPoint value = documentSnapshot.getGeoPoint(POSITION_VALUE);
                    Date date = time.toDate();

                    String longLat = value.toString();
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

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void queryLatestHeartrateDocument() {
        Task<QuerySnapshot> query = heartRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                        heartrate = documentSnapshot.toObject(Heartrate.class);
                        heartrate.setDocumentID(documentSnapshot.getId());
                        Timestamp timestamp = heartrate.getTimestamp();
                        Double value = heartrate.getValue();
                        String ID = heartrate.getDocumentID();
                        Log.i(TAG, "time: " + timestamp.toString() + " " + value.toString() + " " + ID);

                        heartRateTextView.setText("Heartrate: " + value);
                        latestUpdateTextView.setText("Latest update: " + timestamp.toDate());
                    }
                });
    }

    //Use to reload the activity page every 5 seconds to display any new information sent to DB
    private final Runnable m_Runnable = new Runnable() {
        public void run()
        {
            Toast.makeText(MainActivity.this,"in runnable",Toast.LENGTH_SHORT).show();
            queryLatestPositionDocument();
            updateChangedPositionDocument();

            //queryLatestHeartrateDocument();
            MainActivity.this.mHandler.postDelayed(m_Runnable, 5000); //Reload mainactivity after 5s
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(m_Runnable);
        finish();
    };
}
