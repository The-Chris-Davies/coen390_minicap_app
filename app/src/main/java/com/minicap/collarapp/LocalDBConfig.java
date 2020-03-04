package com.minicap.collarapp;

public class LocalDBConfig {
    public static final String DATABASE_NAME = "collar-db";
    public static final int DATABASE_VERSION = 1;

    public static final String DOG_TABLE_NAME = "dog";
    public static final String DOG_COLUMN_ID = "dogid";
    public static final String DOG_COLUMN_NAME = "dogname";
    public static final String DOG_COLUMN_BATTERY = "batterylevel";

    public static final String POSITION_TABLE_NAME = "position";
    public static final String POSITION_COLUMN_ID = "positionid";
    public static final String POSITION_COLUMN_DOG = "positiondog";
    public static final String POSITION_COLUMN_TIMESTAMP = "positiontime";
    public static final String POSITION_COLUMN_LATITUDE = "positionlat";
    public static final String POSITION_COLUMN_LONGITUDE = "positionlon";

    public static final String HEARTRATE_TABLE_NAME = "heartrate";
    public static final String HEARTRATE_COLUMN_ID = "heartrateid";
    public static final String HEARTRATE_COLUMN_DOG = "heartratedog";
    public static final String HEARTRATE_COLUMN_TIMESTAMP = "heartratetime";
    public static final String HEARTRATE_COLUMN_VALUE = "heartrateval";

    public static final String TEMPERATURE_TABLE_NAME = "temperature";
    public static final String TEMPERATURE_COLUMN_ID = "temperatureid";
    public static final String TEMPERATURE_COLUMN_DOG = "temperaturedog";
    public static final String TEMPERATURE_COLUMN_TIMESTAMP = "temperaturetime";
    public static final String TEMPERATURE_COLUMN_VALUE = "temperatureval";
    public static final String TEMPERATURE_COLUMN_EXTVAL = "temperatureext";
}