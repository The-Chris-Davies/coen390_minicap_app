package com.minicap.collarapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected TextView temperatureTextView;
    protected TextView heartRateTextView;
    protected TextView locationTextView;
    protected TextView temperatureExternalTextView;
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

    ArrayList<String> dogs;
    Boolean defFlag;

    String dog1Id;
    String dog2Id;

    private Position position;
    private Heartrate heartrate;
    private Temperature temperature;
    private Temperature temperatureExternal;
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
        temperatureExternalTextView = findViewById(R.id.temperatureExternalTextView);

        //Initialize position, heartrate and temperature objects
        position = new Position();
        heartrate = new Heartrate();
        temperature = new Temperature();
        temperatureExternal = new Temperature();
        dogs = new ArrayList<>();
        dog1Id = new String();
        dog2Id = new String();
        defFlag = false;


        //Connect to database and give toast
        //Toast.makeText(MainActivity.this, "Firebase Connection Good", Toast.LENGTH_LONG).show();

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

        //Todo: Set NULL conditions for dogs and queries (so no crashing occurs)

        db.collection("dogs").addSnapshotListener(this, new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    //if error has occurred
                    Log.e(TAG, "Error in dogs snapshotListener: ", e);
                    return;
                }
//                dog1Id = queryDocumentSnapshots.get(0);
//                dog2Id = queryDocumentSnapshots.get(1);
                dogs.clear();

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    dogs.add(documentSnapshot.getId());
                }
                for (String dog : dogs) {
                    Log.i(TAG, dog);
                }

                if (defFlag == false) {
                    //Set dog default path
                    //set default UI
                    defFlag = true;
                }

            }
        });


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
        //Position
        queryLatestPositionDocument();  //Find latest document
        //updateChangedPositionDocument();    //Update text with document data that was changed

        //Heartrate
        queryLatestHeartrateDocument();

        //Temperature
        queryLatestDogTemperatureDocument();

        //External temperature
        queryLatestExternalTemperatureDocument();
    }

    //Todo: Create method to update whole UI, pass dog key as argument

    //On phone back button pressed return to MainActivity
    @Override
    public void onBackPressed() {
        Intent returnMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(returnMain);
        finish();
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

    //Dog position
    public void queryLatestPositionDocument() {
        Task<QuerySnapshot> posQuery = posRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() < 1) {
                            //if no data is returned, raise a toast and continue
                            Toast.makeText(MainActivity.this, "No location data currently available", Toast.LENGTH_LONG).show();
                            Log.i(TAG, "No location data available for tracking feature");
                            return;
                        }

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

    //Dog heartrate
    public void queryLatestHeartrateDocument() {
        Task<QuerySnapshot> heartQuery = heartRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);

                        //Get timestamp, Id and value
                        Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");
                        String Id = documentSnapshot.getId();
                        heartrate.setTimestamp(timestamp);
                        heartrate.setDocumentID(Id);
                        //Check if value is string or number and then set to object
                        if(documentSnapshot.getData().get("value") instanceof String) {
                            Double value = Double.valueOf(documentSnapshot.getString("value"));
                            heartrate.setValue(value);
                        }
                        else {
                            Double value = documentSnapshot.getDouble("value");
                            heartrate.setValue(value);
                        }

                        Double value = heartrate.getValue();
                        Log.i(TAG, "time: " + timestamp.toString() + " " + Double.toString(value) + " " + Id);

                        heartRateTextView.setText("Heartrate: " + value + "BPM");
                        latestUpdateTextView.setText("Latest update: " + timestamp.toDate());
                    }
                });
    }

    //Dog temperature
    public void queryLatestDogTemperatureDocument() {
        Task<QuerySnapshot> dogTempQuery = tempRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);

                        //Get timestamp, Id and value
                        Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");
                        String Id = documentSnapshot.getId();
                        temperature.setTimestamp(timestamp);
                        temperature.setDocumentID(Id);
                        //Check if value is string or number and then set to object
                        if(documentSnapshot.getData().get("value") instanceof String) {
                            Double value = Double.valueOf(documentSnapshot.getString("value"));
                            temperature.setValue(value);
                        }
                        else {
                            Double value = documentSnapshot.getDouble("value");
                            temperature.setValue(value);
                        }

                        Double value = temperature.getValue();
                        Log.i(TAG, "time: " + timestamp.toString() + " " + Double.toString(value) + " " + Id);

                        temperatureTextView.setText("Body Temperature: " + value + "°C");
                        latestUpdateTextView.setText("Latest update: " + timestamp.toDate());
                    }
                });
    }

    //External temperature
    public void queryLatestExternalTemperatureDocument() {
        Task<QuerySnapshot> dogTempQuery = extTempRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);

                        //Get timestamp, Id and value
                        Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");
                        String Id = documentSnapshot.getId();
                        temperatureExternal.setTimestamp(timestamp);
                        temperatureExternal.setDocumentID(Id);
                        //Check if value is string or number and then set to object
                        if(documentSnapshot.getData().get("value") instanceof String) {
                            Double value = Double.valueOf(documentSnapshot.getString("value"));
                            temperatureExternal.setValue(value);
                        }
                        else {
                            Double value = documentSnapshot.getDouble("value");
                            temperatureExternal.setValue(value);
                        }

                        Double value = temperatureExternal.getValue();
                        Log.i(TAG, "time: " + timestamp.toString() + " " + Double.toString(value) + " " + Id);

                        temperatureExternalTextView.setText("Environmental Temperature: " + value + "°C");
                        latestUpdateTextView.setText("Latest update: " + timestamp.toDate());
                    }
                });
    }

    //Use to reload the activity page every 5 seconds to display any new information sent to DB
    private final Runnable m_Runnable = new Runnable() {
        public void run()
        {
            Toast.makeText(MainActivity.this,"in runnable",Toast.LENGTH_SHORT).show();
            //Position
            queryLatestPositionDocument();  //Find latest document
            updateChangedPositionDocument();    //Update text with document data that was changed

            //Heartrate
            queryLatestHeartrateDocument();

            //Temperature
            queryLatestDogTemperatureDocument();

            //External temperature
            queryLatestExternalTemperatureDocument();

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
