package com.minicap.collarapp;

import com.google.firebase.Timestamp;

public class Heartrate implements Comparable<Heartrate> {

    Double value;
    Timestamp timestamp;

    public Heartrate(Double value, Timestamp timestamp) {
        this.value = value;
        this.timestamp = timestamp;
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

    public void setCurrent() {
        //update the timestamp to the current time
        this.timestamp = Timestamp.now();
    }

    public int compareTo(Heartrate that) {
        //sort based on timestamp
        return this.timestamp.compareTo(that.timestamp);
    }
}