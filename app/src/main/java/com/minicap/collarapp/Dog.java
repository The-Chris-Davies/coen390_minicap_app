package com.minicap.collarapp;

import java.util.ArrayList;

public class Dog {

    //arraylists storing past values from the sensors
    private ArrayList<Position> positions;
    private ArrayList<Heartrate> heartrates;
    private ArrayList<Temperature> temperatures;

    //battery percent
    private double batteryLife;

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }

    public ArrayList<Heartrate> getHeartrates() {
        return heartrates;
    }

    public void setHeartrates(ArrayList<Heartrate> heartrates) {
        this.heartrates = heartrates;
    }

    public ArrayList<Temperature> getTemperatures() {
        return temperatures;
    }

    public void setTemperatures(ArrayList<Temperature> temperatures) {
        this.temperatures = temperatures;
    }

    public double getBatteryLife() {
        return batteryLife;
    }

    public void setBatteryLife(double batteryLife) {
        this.batteryLife = batteryLife;
    }
}
