package com.minicap.collarapp;

import com.google.firebase.Timestamp;

public class Temperature implements Comparable<Temperature> {

    Double value;
    Timestamp timestamp;

    public Temperature(Double value, Timestamp timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public Temperature() {
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

    public int compareTo(Temperature that) {
        //sort based on timestamp
        return this.timestamp.compareTo(that.timestamp);
    }
}
