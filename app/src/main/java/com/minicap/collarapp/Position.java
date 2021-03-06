package com.minicap.collarapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Position implements Comparable<Position> {

    GeoPoint value;
    Timestamp timestamp;
    String documentID;

    public Position(GeoPoint value, Timestamp timestamp) {
        this.value = value;
        this.timestamp = timestamp;
        this.documentID = "";
    }

    public Position() {
        this.value = new GeoPoint(0.0,0.0);
        this.timestamp = Timestamp.now();
    }

    public GeoPoint getValue() {
        return value;
    }

    public void setValue(GeoPoint value) {
        this.value = value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public int compareTo(Position that) {
        //sort based on timestamp
        return this.timestamp.compareTo(that.timestamp);
    }
}
