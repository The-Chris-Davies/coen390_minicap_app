package com.minicap.collarapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LocalDBHelper extends SQLiteOpenHelper {

    private Context context;

    //prefix used for logging
    private static final String TAG = "DBHelper";

    public LocalDBHelper(Context extContext) {
        super(extContext, LocalDBConfig.DATABASE_NAME, null, LocalDBConfig.DATABASE_VERSION);
        context = extContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //the SQL command to create the dog table
        String CREATE_TABLE_DOG = "CREATE TABLE " + LocalDBConfig.DOG_TABLE_NAME + " (" +
                LocalDBConfig.DOG_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                LocalDBConfig.DOG_COLUMN_NAME + " TEXT NOT NULL, " +
                LocalDBConfig.DOG_COLUMN_BATTERY + " INTEGER " +
                ")";

        //write the command to the log
        Log.d(TAG, CREATE_TABLE_DOG);

        //create the table
        db.execSQL(CREATE_TABLE_DOG);

        Log.d(TAG, "dog table created.");

        //the SQL command to create the position table
        String CREATE_TABLE_POSITION = "CREATE TABLE " + LocalDBConfig.POSITION_TABLE_NAME + " (" +
                LocalDBConfig.POSITION_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                LocalDBConfig.POSITION_COLUMN_DOG + " INTEGER NOT NULL, " +
                LocalDBConfig.POSITION_COLUMN_TIMESTAMP + " INTEGER, " +
                LocalDBConfig.POSITION_COLUMN_LATITUDE + " REAL, " +
                LocalDBConfig.POSITION_COLUMN_LONGITUDE + " REAL " +
                ")";


        //write the command to the log
        Log.d(TAG, CREATE_TABLE_POSITION);

        //create the table
        db.execSQL(CREATE_TABLE_POSITION);

        Log.d(TAG, "position table created.");

        //the SQL command to create the heartrate table
        String CREATE_TABLE_HEARTRATE = "CREATE TABLE " + LocalDBConfig.HEARTRATE_TABLE_NAME + " (" +
                LocalDBConfig.HEARTRATE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                LocalDBConfig.HEARTRATE_COLUMN_DOG + " INTEGER NOT NULL, " +
                LocalDBConfig.HEARTRATE_COLUMN_TIMESTAMP + " INTEGER, " +
                LocalDBConfig.HEARTRATE_COLUMN_VALUE + " INTEGER " +
                ")";

        //write the command to the log
        Log.d(TAG, CREATE_TABLE_HEARTRATE);

        //create the table
        db.execSQL(CREATE_TABLE_HEARTRATE);

        Log.d(TAG, "heartrate table created.");

        //the SQL command to create the temperature table
        String CREATE_TABLE_TEMPERATURE = "CREATE TABLE " + LocalDBConfig.TEMPERATURE_TABLE_NAME + " (" +
                LocalDBConfig.TEMPERATURE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                LocalDBConfig.TEMPERATURE_COLUMN_DOG + " INTEGER NOT NULL, " +
                LocalDBConfig.TEMPERATURE_COLUMN_TIMESTAMP + " INTEGER, " +
                LocalDBConfig.TEMPERATURE_COLUMN_VALUE + " REAL, " +
                LocalDBConfig.TEMPERATURE_COLUMN_EXTVAL + " REAL " +
                ")";

        //write the command to the log
        Log.d(TAG, CREATE_TABLE_TEMPERATURE);

        //create the table
        db.execSQL(CREATE_TABLE_TEMPERATURE);

        Log.d(TAG, "temperature table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int ii) {
        //only called on database layout change
    }

    public ArrayList<Dog> getDogs() {
        ArrayList<Dog> dogList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(LocalDBConfig.DOG_TABLE_NAME, null, null, null, null, null, LocalDBConfig.DOG_COLUMN_ID);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do{
                        //create dog with just ID
                        dogList.add(new Dog(cursor.getInt(cursor.getColumnIndex(LocalDBConfig.DOG_COLUMN_ID))));
                    }while(cursor.moveToNext());
                }
            }
        } catch(SQLiteException e) {
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }

        return dogList;
    }

    public void updateDog(Dog dog) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        dog.setPositions(getPositions(dog));
        dog.setHeartrates(getHeartrates(dog));
        dog.setTemperatures(getTemperatures(dog));
        dog.setExternalTemperatures(getExternalTemperatures(dog));

        try {
            cursor = db.query(LocalDBConfig.DOG_TABLE_NAME, null, LocalDBConfig.DOG_COLUMN_ID + "=" + dog.getId(), null , null, null, LocalDBConfig.DOG_COLUMN_ID);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    dog.setName(cursor.getString(cursor.getColumnIndex(LocalDBConfig.DOG_COLUMN_NAME)));
                    dog.setBatteryLife(cursor.getDouble(cursor.getColumnIndex(LocalDBConfig.DOG_COLUMN_BATTERY)));
                }
            }
        } catch(SQLiteException e) {
            Log.d(TAG, "EXCEPTION: " + e + "\nwhen trying to load dog positions");
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }
    }

    public ArrayList<Position> getPositions(Dog dog) {

        ArrayList<Position> positionList = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(LocalDBConfig.POSITION_TABLE_NAME, null, LocalDBConfig.POSITION_COLUMN_DOG + "=" + dog.getId(), null , null, null, LocalDBConfig.POSITION_COLUMN_TIMESTAMP);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do{
                        positionList.add(new Position(
                                cursor.getDouble(cursor.getColumnIndex(LocalDBConfig.POSITION_COLUMN_LATITUDE)),
                                cursor.getDouble(cursor.getColumnIndex(LocalDBConfig.POSITION_COLUMN_LONGITUDE)),
                                new Timestamp(cursor.getLong(cursor.getColumnIndex(LocalDBConfig.POSITION_COLUMN_TIMESTAMP)),0)));
                    }while(cursor.moveToNext());
                }
            }
        } catch(SQLiteException e) {
            Log.d(TAG, "EXCEPTION: " + e + "\nwhen trying to load dog positions");
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }
        return positionList;
    }

    public ArrayList<Heartrate> getHeartrates(Dog dog) {

        ArrayList<Heartrate> heartrateList = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(LocalDBConfig.HEARTRATE_TABLE_NAME, null, LocalDBConfig.HEARTRATE_COLUMN_DOG + "=" + dog.getId(), null , null, null, LocalDBConfig.HEARTRATE_COLUMN_TIMESTAMP);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do{
                        heartrateList.add(new Heartrate(
                                cursor.getDouble(cursor.getColumnIndex(LocalDBConfig.HEARTRATE_COLUMN_VALUE)),
                                new Timestamp(cursor.getLong(cursor.getColumnIndex(LocalDBConfig.HEARTRATE_COLUMN_TIMESTAMP)),0)));
                    }while(cursor.moveToNext());
                }
            }
        } catch(SQLiteException e) {
            Log.d(TAG, "EXCEPTION: " + e + "\nwhen trying to load dog heart rates");
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }
        return heartrateList;
    }

    public ArrayList<Temperature> getTemperatures(Dog dog) {

        ArrayList<Temperature> temperatureList = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(LocalDBConfig.TEMPERATURE_TABLE_NAME, null, LocalDBConfig.TEMPERATURE_COLUMN_DOG + "=" + dog.getId(), null , null, null, LocalDBConfig.TEMPERATURE_COLUMN_TIMESTAMP);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do{
                        temperatureList.add(new Temperature(
                                cursor.getDouble(cursor.getColumnIndex(LocalDBConfig.TEMPERATURE_COLUMN_VALUE)),
                                new Timestamp(cursor.getLong(cursor.getColumnIndex(LocalDBConfig.TEMPERATURE_COLUMN_TIMESTAMP)),0)));
                    }while(cursor.moveToNext());
                }
            }
        } catch(SQLiteException e) {
            Log.d(TAG, "EXCEPTION: " + e + "\nwhen trying to load dog temperatures");
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }
        return temperatureList;
    }
    public ArrayList<Temperature> getExternalTemperatures(Dog dog) {

        ArrayList<Temperature> temperatureList = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(LocalDBConfig.TEMPERATURE_TABLE_NAME, null, LocalDBConfig.TEMPERATURE_COLUMN_DOG + "=" + dog.getId(), null , null, null, LocalDBConfig.TEMPERATURE_COLUMN_TIMESTAMP);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do{
                        temperatureList.add(new Temperature(
                                cursor.getDouble(cursor.getColumnIndex(LocalDBConfig.TEMPERATURE_COLUMN_EXTVAL)),
                                new Timestamp(cursor.getLong(cursor.getColumnIndex(LocalDBConfig.TEMPERATURE_COLUMN_TIMESTAMP)),0)));
                    }while(cursor.moveToNext());
                }
            }
        } catch(SQLiteException e) {
            Log.d(TAG, "EXCEPTION: " + e + "\nwhen trying to load dog temperatures");
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }
        return temperatureList;
    }
}

