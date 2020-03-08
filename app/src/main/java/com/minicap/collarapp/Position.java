package com.minicap.collarapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Position implements Comparable<Position> {

    GeoPoint value;
    String longitude;
    String latitude;
    Timestamp timestamp;

    public Position(GeoPoint value, Timestamp timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public Position() {
        this.value = new GeoPoint(0.0,0.0);
        this.timestamp = Timestamp.now();
    }

    public Position(double lat, double lon) {
        this.value = new GeoPoint(lat, lon);
        this.timestamp = Timestamp.now();
    }

    public Position(double lat, double lon, Timestamp timestamp) {
        this.value = new GeoPoint(lat, lon);
        this.timestamp = timestamp;
    }

    public GeoPoint getValue() {
        return value;
    }

    public void setValue(GeoPoint value) {
        this.value = value;
    }

    public double getLatitude() {
        return value.getLatitude();
    }

    public double getLongitude() {
        return value.getLongitude();
    }

    public void setLatitude(double lat) {
        value = new GeoPoint(lat,this.value.getLongitude());
    }

    public void setLongitude(double lon) {
        value = new GeoPoint(this.value.getLatitude(), lon);
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setCurrent() {
        //update the timestamp to the current time
        this.timestamp = Timestamp.now();
    }

    public int compareTo(Position that) {
        //sort based on timestamp
        return this.timestamp.compareTo(that.timestamp);
    }
}
