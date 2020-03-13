package com.minicap.collarapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

public class PositionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("dogs/HpwWiJSGHNbOgJtYi2jM/position/YXT3F0MuJpiUzJStuqNN");
    private static final String POSITION_VALUE = "value";
    private static final String TAG = "MAPACTIVITY";
    private MarkerOptions dogPositionMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        //initialize the marker that points to the dog's position
        dogPositionMarker = new MarkerOptions().title("Dog Location");
        //set minimum / maximum zoom
        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(20.0f);


        mDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    GeoPoint value = documentSnapshot.getGeoPoint(POSITION_VALUE);

                    //Get longitude and latitude from geopoint
                    Double latitudeGet = value.getLatitude();
                    Double longitudeGet = value.getLongitude();
                    Log.i(TAG, "latitude: " + latitudeGet.toString());
                    Log.i(TAG, "longitude: " + longitudeGet.toString());

                    //create marker on map
                    LatLng dogPos = new LatLng(latitudeGet, longitudeGet);
                    dogPositionMarker.position(dogPos);
                    mMap.addMarker(dogPositionMarker);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(dogPos));
                }
            }
        });
    }
}
