//package com.minicap.collarapp;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class SharedPreferenceHelper extends AppCompatActivity {
//
//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;
//
//    public SharedPreferenceHelper(Context context)
//    {
//        sharedPreferences = context.getSharedPreferences(context.getString(R.string.dogName), Context.MODE_PRIVATE );
//        editor = sharedPreferences.edit();
//    }
//    public void setProfileName(String name)
//    {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("dogName",name);
//        editor.commit();
//    }
//
//    public String getProfileName()
//    {
//        return sharedPreferences.getString("profileName", "Name");
//    }
//
//    public SharedPreferences getSharedPreferences() {
//        return sharedPreferences;
//    }
//
//    public void save() {
//        editor.apply();
//    }
//}
