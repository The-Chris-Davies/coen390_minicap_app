package com.minicap.collarapp;

import java.util.ArrayList;

public class Dog {

    //the dog's name
    private String name;
    //battery percent
    private double batteryLife;
    //the dog's ID
    private int id;

    //arraylists storing past values from the sensors
    private ArrayList<Position> positions;
    private ArrayList<Heartrate> heartrates;
    private ArrayList<Temperature> temperatures;
    private ArrayList<Temperature> externalTemperatures;

    public Dog(int id) {
        this.id = id;
    }

    public Dog(String name, double batteryLife, int id, ArrayList<Position> positions, ArrayList<Heartrate> heartrates, ArrayList<Temperature> temperatures, ArrayList<Temperature> externalTemperatures) {
        this.name = name;
        this.batteryLife = batteryLife;
        this.id = id;
        this.positions = positions;
        this.heartrates = heartrates;
        this.temperatures = temperatures;
        this.externalTemperatures = externalTemperatures;
    }

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

    public ArrayList<Temperature> getExternalTemperatures() {
        return externalTemperatures;
    }

    public void setExternalTemperatures(ArrayList<Temperature> externalTemperatures) {
        this.externalTemperatures = externalTemperatures;
    }

    public double getBatteryLife() {
        return batteryLife;
    }

    public void setBatteryLife(double batteryLife) {
        this.batteryLife = batteryLife;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
         this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
