package com.minicap.collarapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {

    private SharedPreferences sharedPreferences;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("AlertPreferences", Context.MODE_PRIVATE );
    }

    public void saveAlertSettings(Double intTempHighVal, Double intTempHighTime, Double intTempLowVal, Double intTempLowTime, Double extTempHighVal, Double extTempHighTime, Double extTempLowVal, Double extTempLowTime, Double battAlertVal, Double watchdogAlertVal) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("intTempHighVal",intTempHighVal.toString());
        editor.putString("intTempHighTime",intTempHighTime.toString());
        editor.putString("intTempLowVal",intTempLowVal.toString());
        editor.putString("intTempLowTime",intTempLowTime.toString());
        editor.putString("extTempHighVal",extTempHighVal.toString());
        editor.putString("extTempHighTime",extTempHighTime.toString());
        editor.putString("extTempLowVal",extTempLowVal.toString());
        editor.putString("extTempLowTime",extTempLowTime.toString());
        editor.putString("battAlertVal",battAlertVal.toString());
        editor.putString("watchdogAlertVal",watchdogAlertVal.toString());

        editor.apply();
    }

    public Double getIntTempHighVal() {
        return Double.parseDouble(sharedPreferences.getString("intTempHighVal", "42"));
    }

    public Double getIntTempHighTime() {
        return Double.parseDouble(sharedPreferences.getString("intTempHighTime", "5"));
    }

    public Double getIntTempLowVal() {
        return Double.parseDouble(sharedPreferences.getString("intTempLowVal", "32"));
    }

    public Double getIntTempLowTime() {
        return Double.parseDouble(sharedPreferences.getString("intTempLowTime", "5"));
    }

    public Double getExtTempHighVal() {
        return Double.parseDouble(sharedPreferences.getString("extTempHighVal", "32"));
    }

    public Double getExtTempHighTime() {
        return Double.parseDouble(sharedPreferences.getString("extTempHighTime", "45"));
    }

    public Double getExtTempLowVal() {
        return Double.parseDouble(sharedPreferences.getString("extTempLowVal", "-20"));
    }

    public Double getExtTempLowTime() {
        return Double.parseDouble(sharedPreferences.getString("extTempLowTime", "45"));
    }

    public Double getBattAlertVal() {
        return Double.parseDouble(sharedPreferences.getString("battAlertVal", "15"));
    }

    public Double getWatchdogAlertVal() {
        return Double.parseDouble(sharedPreferences.getString("watchdogAlertVal", "15"));
    }
}