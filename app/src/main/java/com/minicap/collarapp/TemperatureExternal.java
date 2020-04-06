package com.minicap.collarapp;

import com.google.firebase.Timestamp;

public class TemperatureExternal implements Comparable<TemperatureExternal> {

    Double value;
    Timestamp timestamp;
    String documentID;

    public TemperatureExternal(Double value, Timestamp timestamp) {
        this.value = value;
        this.timestamp = timestamp;
        documentID = "";
    }

    public TemperatureExternal() {
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

    public int compareTo(TemperatureExternal that) {
        //sort based on timestamp
        return this.timestamp.compareTo(that.timestamp);
    }

}
