package com.minicap.collarapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {

    private SharedPreferences sharedPreferences;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("ProfilePreference", Context.MODE_PRIVATE );
    }

    public void saveProfileName(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name",name );
        editor.commit();
    }

    public void saveProfileAge(int age){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("age", age);
        editor.commit();
    }

    public void saveProfileID(String id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", id);
        editor.commit();
    }

    public String getProfileName() {
        return sharedPreferences.getString("name", "");
    }

    public int getProfileAge() {
        return sharedPreferences.getInt("age", 0);
    }

    public String getProfileID() {
        return sharedPreferences.getString("id", "");
    }
}