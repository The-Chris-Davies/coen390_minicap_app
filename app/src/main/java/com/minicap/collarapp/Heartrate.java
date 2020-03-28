package com.minicap.collarapp;

import com.google.firebase.Timestamp;

public class Heartrate implements Comparable<Heartrate> {

    Double value;
    Timestamp timestamp;
    String documentID;

    public Heartrate(Double value, Timestamp timestamp) {
        this.value = value;
        this.timestamp = timestamp;
        this.documentID = "";
    }

    public Heartrate() {
        this.value = 0.0;
        this.timestamp = Timestamp.now();
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
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

    public int compareTo(Heartrate that) {
        //sort based on timestamp
        return this.timestamp.compareTo(that.timestamp);
    }
}
