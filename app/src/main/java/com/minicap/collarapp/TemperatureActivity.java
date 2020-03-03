package com.minicap.collarapp;


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
import java.util.List;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class TemperatureActivity extends AppCompatActivity {

    InputStream inputStream1;
    InputStream inputStream2;
    String[] ids;
    String[] ids1;


    LineGraphSeries<DataPoint> series,series1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        inputStream1 = getResources().openRawResource(R.raw.data1);


        inputStream2 = getResources().openRawResource(R.raw.data0);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream1));
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(inputStream2));
        GraphView graph = findViewById(R.id.graph);


        series = new LineGraphSeries<DataPoint>();
        series1 = new LineGraphSeries<DataPoint>();
        series.setColor(Color.RED);
        series1.setColor(Color.GREEN);


        double x, y;
        double x1, y1;
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {

                ids = csvLine.split(",");


                try {
                    Log.d(" Data ", ":" + ids[0] + "  " + ids[1]);


                    x = Double.parseDouble(ids[0]);
                    y = Double.parseDouble(ids[1]);

                    series.appendData(new DataPoint(x, y), true, 25);


                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMinX(0);
                    graph.getViewport().setMaxX(50);
                    graph.getViewport().setMaxY(50);
                    graph.getViewport().setMinY(-50);
                    graph.getViewport().setScalableY(true);
                    graph.getViewport().setScalable(true);
                    graph.addSeries(series);
                    // graph.addSeries(series1);


                    graph.setTitle("Interal/External Temperature vs Hourly");


                } catch (Exception e) {
                    Log.d("BADDDD", e.toString());
                }
            }

        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }


        try {
            String csvLine;
            while ((csvLine = reader1.readLine()) != null) {
                ids1 = csvLine.split(",");

                try {

                    Log.d(" Data ", ":" + ids1[0] + "  " + ids1[1]);


                    x1 = Double.parseDouble(ids1[0]);
                    y1 = Double.parseDouble(ids1[1]);

                    // series.appendData(new DataPoint(x,y),true,25);
                    series1.appendData(new DataPoint(x1, y1), true, 25);


                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMinX(0);
                    graph.getViewport().setMaxX(25);
                    graph.getViewport().setMaxY(50);
                    graph.getViewport().setScalableY(true);
                    graph.getViewport().setScalable(true);
                    graph.addSeries(series1);


                    graph.setTitle("Interal/External Temperature vs Hourly");


                } catch (Exception e) {
                    Log.d("BADDDD", e.toString());
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }


    }
    }
