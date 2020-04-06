package com.minicap.collarapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class PositionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("dogs/HpwWiJSGHNbOgJtYi2jM/");
    private CollectionReference mPosRef = mDocRef.collection("position");

    private RecyclerView positionList;
    private RecyclerView.Adapter positionAdapter;
    private RecyclerView.LayoutManager positionLayoutManager;

    private static final String POSITION_VALUE = "value";
    private static final String TAG = "PositionActivity";
    private ArrayList<Position> positions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        //Display navigation back button
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Display back button

        //get recyclerview
        positionList = findViewById(R.id.positionList);
        positionLayoutManager = new LinearLayoutManager(this);


        // Obtain the mapView and get notified when the map is ready to be used.
        MapView mapView = (MapView) findViewById(R.id.map);
        if (mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //set minimum / maximum zoom
        //mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(20.0f);

        //add on marker click callback
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                positionList.scrollToPosition(positions.indexOf(marker.getTag()));
                //run button's callback after recyclerView has drawn it (to prevent null reference)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,"clicked on index " + positions.indexOf(marker.getTag()));
                        positionList.findViewHolderForAdapterPosition(positions.indexOf(marker.getTag())).itemView.performClick();
                    }
                },10);
                return false;
            }
        });

        mPosRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            if(e != null) {
                //if error has occurred
                Log.e(TAG, "Error in snapshotListener: ", e);
                return;
            }

            //add the positions to the array
            positions= new ArrayList();
            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                positions.add(documentSnapshot.toObject(Position.class));
            Log.d(TAG, "Found " + positions.size() + " positions in the firebase");

            //if no positions available, continue
            if(positions.isEmpty()){
                Toast.makeText(PositionActivity.this, "No location data available for tracking feature", Toast.LENGTH_LONG).show();
                Log.i(TAG, "No location data available for tracking feature");
                return;
            }

            //sort the array (based on timestamp)
            //TODO: get data from firebase pre-sorted!
            Collections.sort(positions, Collections.reverseOrder());

            //add the positions to the arrayList
            positionAdapter = new PositionListAdapter(PositionActivity.this, positions, mMap);
            positionList.setAdapter(positionAdapter);
            positionList.setLayoutManager(positionLayoutManager);
            positionList.getAdapter().notifyDataSetChanged();   //probably not necessary

            //clear the map's markers and lines
            mMap.clear();

            //create markers on map
            for(Position pos : positions) {
                Double latitudeGet = pos.getValue().getLatitude();
                Double longitudeGet = pos.getValue().getLongitude();
                LatLng dogPos = new LatLng(latitudeGet, longitudeGet);
                //todo: set the marker image differently for most recent and other points.
                mMap.addMarker(new MarkerOptions().title(DateFormat.getTimeInstance().format(pos.getTimestamp().toDate())).position(dogPos)).setTag(pos);
                //zoom to most recent position
                if(pos == positions.get(0))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dogPos, 14.0f));
            }

            //draw lines between previous data points
            if(positions.size() > 1) {
                ArrayList<LatLng> linePts = new ArrayList();
                for(int i = 0; i < positions.size(); i++) {
                    //if timestamp difference is over 15 minutes, disconnect the line
                    if(i >= 1 && (positions.get(i).getTimestamp().getSeconds() + 60*15 < positions.get(i-1).getTimestamp().getSeconds())) {
                        mMap.addPolyline(new PolylineOptions().color(Color.RED).jointType(1)).setPoints(linePts);
                        linePts = new ArrayList();
                    }
                    linePts.add(new LatLng(positions.get(i).getValue().getLatitude(), positions.get(i).getValue().getLongitude()));
                }
                mMap.addPolyline(new PolylineOptions().color(Color.RED)).setPoints(linePts);
            }
            }
        });
    }


}
