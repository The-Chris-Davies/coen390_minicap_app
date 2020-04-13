package com.minicap.collarapp;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class HeartrateActivity extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;
    //private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("dogs/HpwWiJSGHNbOgJtYi2jM/");
    //private CollectionReference mHRRef = mDocRef.collection("heartrate");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mHRRef;
    private static final String TAG = "HeartrateActivity";

    private TextView valDisp;
    private TextView timeDisp;

    private ArrayList<Heartrate> heartrates;
    private String currDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heartrate);

        //Display navigation back button
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Display back button

        //Initialize views
        valDisp = findViewById(R.id.value);
        timeDisp = findViewById(R.id.timestamp);
        final GraphView graph = findViewById(R.id.graph);

        //Get current dog from main activity
        getCurrentDog();

        //Set path for heartrate using currently selected dog
        mHRRef = db.collection("dogs/" + currDog + "/heartrate");

        series = new LineGraphSeries<DataPoint>();
        series.setColor(Color.RED);
        series.setDrawDataPoints(true);
        series.setTitle("heartrate");

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                for(int i = heartrates.size()-1; i >= 0; i--) {
                    if(heartrates.get(i).getTimestamp().toDate().getTime() == dataPoint.getX()) {
                        //run button's callback after recyclerView has drawn it (to prevent null reference)
                        final int finalI = i;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG,"clicked on index " + finalI);
                                Heartrate currentHr = heartrates.get(finalI);
                                valDisp.setText(currentHr.getValue() + " BPM");
                                timeDisp.setText(DateFormat.getTimeInstance().format(currentHr.getTimestamp().toDate())
                                        + "\n" + DateFormat.getDateInstance().format(currentHr.getTimestamp().toDate())
                                );
                            }
                        },10);
                    }
                }
            }
        });

        graph.addSeries(series);

        setupGraph(graph);

        //get internal temperature data from firebase
        mHRRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //if error has occurred
                    Log.e(TAG, "Error in internal heartrate snapshotListener: ", e);
                    return;
                }

                heartrates = new ArrayList();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    //check if value is a string
                    if(documentSnapshot.getData().get("value") instanceof String) {
                        if (((String) documentSnapshot.getData().get("value")).isEmpty())
                            continue;

                        heartrates.add(new Heartrate(
                                Double.parseDouble((String) documentSnapshot.getData().get("value")),
                                (Timestamp) documentSnapshot.getData().get("timestamp")));
                    }
                    else
                        heartrates.add(documentSnapshot.toObject(Heartrate.class));
                Log.d(TAG, "Found " + heartrates.size() + " heartrates in the firebase");

                //if no temperatures available, continue
                if (heartrates.isEmpty()) {
                    Toast.makeText(HeartrateActivity.this, "No heartrate data available", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "No heartrate data available");
                    return;
                }

                //sort temperature list
                Collections.sort(heartrates, Collections.reverseOrder());

                //generate list of points
                series.resetData(new DataPoint[0]);
                for (int i = heartrates.size()-1; i >= 0; i--)
                    series.appendData(new DataPoint(heartrates.get(i).getTimestamp().toDate().getTime(), heartrates.get(i).getValue()), true, 24*60);
            }
        });
    }

    //Back button navigation return function to MainActivity
    @Override
    public boolean onSupportNavigateUp(){
        Intent returnMain = new Intent(getApplicationContext(), MainActivity.class);
        returnMain.putExtra("dogID", currDog);
        startActivity(returnMain);
        finish();
        return true;
    }

    //On phone back button pressed return to MainActivity
    @Override
    public void onBackPressed() {
        Intent returnMain = new Intent(getApplicationContext(), MainActivity.class);
        returnMain.putExtra("dogID", currDog);
        startActivity(returnMain);
        finish();
    }

    //Get dog from main activity and pass to position activity
    public void getCurrentDog() {
        currDog = new String();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currDog = bundle.getString("dogID");
            Toast.makeText(this, "Map view", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupGraph(GraphView graph) {
        //sets up graph visualization
        //scale: 1 x axis unit is 1 ms (3600000 is 1 hour);
        graph.getViewport().setMinY(-50);
        graph.getViewport().setMaxY(50);

        graph.getViewport().setScalable(true);
        graph.getViewport().setMinX(0); //set range to 1 hour
        graph.getViewport().setMaxX(1800000);

        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(HeartrateActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(8);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(45);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setFixedPosition(20,20);
        graph.getLegendRenderer().setTextSize(26);

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
}
