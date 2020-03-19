package com.minicap.collarapp;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

        final GraphView graph = findViewById(R.id.graph);

        graph.getViewport().setMaxY(50);
        graph.getViewport().setMinY(-50);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScalable(true);
        graph.setTitle("Interal/External Temperature");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(TemperatureActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

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
                intSeries = new LineGraphSeries<DataPoint>();
                intSeries.setColor(Color.GREEN);
                for (Temperature internalTemp : internalTemps)
                    intSeries.appendData(new DataPoint(new Date(internalTemp.getTimestamp().getSeconds()), internalTemp.getValue()), true, 25);
                //add series to the graph
                //TODO: replace with a more elegant solution
                graph.removeSeries(intSeries);
                graph.addSeries(intSeries);

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
                extSeries = new LineGraphSeries<DataPoint>();
                extSeries.setColor(Color.RED);
                for (Temperature externalTemp : externalTemps)
                    extSeries.appendData(new DataPoint(new Date(externalTemp.getTimestamp().getSeconds()), externalTemp.getValue()), true, 25);
                //add series to the graph
                //TODO: replace with a more elegant solution
                graph.removeSeries(extSeries);
                graph.addSeries(extSeries);

            }
        });



    }
}
