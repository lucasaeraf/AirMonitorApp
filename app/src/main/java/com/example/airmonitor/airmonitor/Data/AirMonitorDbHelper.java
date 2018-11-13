package com.example.airmonitor.airmonitor.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AirMonitorDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "airmonitor.db";
    private static final int DB_VERSION = 1;

    public AirMonitorDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                AirMonitorContract.ChannelEntries.TABLE_NAME + " (" +
                AirMonitorContract.ChannelEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AirMonitorContract.ChannelEntries.COLUMN_CO + " INTEGER, " +
                AirMonitorContract.ChannelEntries.COLUMN_HUMIDITY + " REAL, " +
                AirMonitorContract.ChannelEntries.COLUMN_TEMPERATURE + " REAL, " +
                AirMonitorContract.ChannelEntries.COLUMN_PM + " REAL, " +
                AirMonitorContract.ChannelEntries.COLUMN_STATUS + " TEXT, " +
                AirMonitorContract.ChannelEntries.COLUMN_LATITUDE + " REAL, " +
                AirMonitorContract.ChannelEntries.COLUMN_LONGITUDE + " REAL, " +
                AirMonitorContract.ChannelEntries.COLUMN_TIME_STAMP + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + AirMonitorContract.ChannelEntries.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
