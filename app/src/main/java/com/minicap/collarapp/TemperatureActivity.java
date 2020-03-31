package com.minicap.collarapp;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class TemperatureActivity extends AppCompatActivity {

    private LineGraphSeries<DataPoint> extSeries,intSeries;
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("dogs/HpwWiJSGHNbOgJtYi2jM/");
    private CollectionReference mTempRef = mDocRef.collection("temperature");
    private CollectionReference mExtTempRef = mDocRef.collection("external_temperature");
    private static final String TAG = "TemperatureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        //Display navigation back button
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Display back button

        final GraphView graph = findViewById(R.id.graph);

        intSeries = new LineGraphSeries<DataPoint>();
        intSeries.setColor(Color.BLUE);
        intSeries.setDrawDataPoints(true);
        intSeries.setDataPointsRadius(10);
        intSeries.setThickness(15);
        intSeries.setTitle("Body Temperature °C");
        //intSeries.setDrawBackground(true);
        graph.addSeries(intSeries);

        extSeries = new LineGraphSeries<DataPoint>();
        extSeries.setColor(Color.MAGENTA);
        extSeries.setDrawDataPoints(true);
        extSeries.setTitle("External Temperature °C");
        graph.addSeries(extSeries);

        setupGraph(graph);

        //get internal temperature data from firebase
        mTempRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //if error has occurred
                    Log.e(TAG, "Error in internal temperature snapshotListener: ", e);
                    return;
                }

                ArrayList<Temperature> internalTemps = new ArrayList();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    //check if value is a string
                    if(documentSnapshot.getData().get("value") instanceof String)
                        internalTemps.add(new Temperature(
                                Double.parseDouble( (String) documentSnapshot.getData().get("value")),
                                (Timestamp) documentSnapshot.getData().get("timestamp")));
                    else
                        internalTemps.add(documentSnapshot.toObject(Temperature.class));
                Log.d(TAG, "Found " + internalTemps.size() + " internal temperatures in the firebase");

                //if no temperatures available, continue
                if (internalTemps.isEmpty()) {
                    Toast.makeText(TemperatureActivity.this, "No body temperature data available", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "No body temperature data available");
                    return;
                }

                //sort temperature list
                Collections.sort(internalTemps);

                //generate list of points
                intSeries.resetData(new DataPoint[0]);

                for (Temperature internalTemp : internalTemps)
                    intSeries.appendData(new DataPoint(internalTemp.getTimestamp().toDate().getTime(), internalTemp.getValue()), true, 24*60);
                setupGraph(graph, internalTemps.get(internalTemps.size()-1));
            }
        });

        //get external temperature data from firebase
        mExtTempRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //if error has occurred
                    Log.e(TAG, "Error in external temperature snapshotListener: ", e);
                    return;
                }

                ArrayList<Temperature> externalTemps = new ArrayList();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    //check if value is a string
                    if(documentSnapshot.getData().get("value") instanceof String)
                        externalTemps.add(new Temperature(
                                Double.parseDouble( (String) documentSnapshot.getData().get("value")),
                                (Timestamp) documentSnapshot.getData().get("timestamp")));
                    else
                        externalTemps.add(documentSnapshot.toObject(Temperature.class));
                Log.d(TAG, "Found " + externalTemps.size() + " external temperatures in the firebase");

                //if no temperatures available, continue
                if (externalTemps.isEmpty()) {
                    Toast.makeText(TemperatureActivity.this, "No external temperature data available", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "No external temperature data available");
                    return;
                }

                //sort temperature list
                Collections.sort(externalTemps);

                //generate list of points
                extSeries.resetData(new DataPoint[0]);
                extSeries.setColor(Color.MAGENTA);
                extSeries.setDrawDataPoints(true);
                extSeries.setDataPointsRadius(15);
                extSeries.setThickness(12);
                extSeries.setTitle("External Temperature °C");
                //extSeries.setDrawBackground(true);
                for (Temperature externalTemp : externalTemps)
                    extSeries.appendData(new DataPoint(externalTemp.getTimestamp().toDate().getTime(), externalTemp.getValue()), true, 24*60);

                setupGraph(graph, externalTemps.get(externalTemps.size()-1));
            }
        });
    }

    //Back button navigation return function to MainActivity
    @Override
    public boolean onSupportNavigateUp(){
        Intent returnMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(returnMain);
        finish();
        return true;
    }

    //On phone back button pressed return to MainActivity
    @Override
    public void onBackPressed() {
        Intent returnMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(returnMain);
        finish();
    }

    private void setupGraph(GraphView graph) {
        //sets up graph visualization
        //scale: 1 x axis unit is 1 ms (3600000 is 1 hour);
        graph.getViewport().setMinY(-60);
        graph.getViewport().setMaxY(60);

        graph.getViewport().setScalable(true);
        graph.getViewport().setMinX(0); //set range to 1 hour
        graph.getViewport().setMaxX(1800000);

        graph.getGridLabelRenderer().setVerticalAxisTitle("Temperature °C");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");

        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(48);
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(48);

        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLUE);


        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(TemperatureActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(18);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(45);



        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        //graph.getLegendRenderer().setFixedPosition(20,20);

        //custom label generation
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show time for x values
                    Date date = new Date((long)value);
                    String time = DateFormat.getTimeInstance().format(date) + " " + DateFormat.getDateInstance().format(date);
                    return time;
                } else {
                    // show normal y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    private void setupGraph(GraphView graph, Temperature max) {
        Log.d(TAG, "setupGraph called w/ max temperature");
        //sets up graph and sets x axis boundaries
        graph.getViewport().scrollToEnd();
    }
}
