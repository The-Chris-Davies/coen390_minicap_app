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
                LocalDBConfig.DOG_COLUMN_TITLE + " TEXT NOT NULL, " +
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
}

