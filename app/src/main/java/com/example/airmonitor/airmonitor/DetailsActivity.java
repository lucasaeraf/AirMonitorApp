package com.example.airmonitor.airmonitor;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.airmonitor.airmonitor.Data.AirMonitorContract;
import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.Arrays;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    final private int TEMP = 1;
    final private int HMDT = 2;
    final private int PM = 3;
    private int constant;
    private TextView mDisplayValue;
    private TextView mDisplayAvg;
    private TextView mDisplayStatus;
    private ToggleButton mButtonStatus;
    private MapView mMap;
    private Cursor mCursor;
    private float lat;
    private float lon;
    private final int FETCH_DETAILS_LOADER = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mDisplayAvg = (TextView) findViewById(R.id.avg);
        mDisplayStatus = (TextView) findViewById(R.id.status);
        mDisplayValue = (TextView) findViewById(R.id.value);
        mMap = (MapView) findViewById(R.id.mapView);

        //mMap.set

        Intent intentInitializer = getIntent();
        constant = intentInitializer.getIntExtra(Intent.EXTRA_TEXT, 0);
        getSupportLoaderManager().initLoader(FETCH_DETAILS_LOADER, null, this);
//        if (intentInitializer.hasExtra(Intent.EXTRA_TEXT)){
//           String constant = intentInitializer.getStringExtra(Intent.EXTRA_TEXT);
//
//           switch (constant){
//               case "TEMP":
//                   mDisplayValue.setText("40 ºC");
//                   mDisplayStatus.setText("OK");
//                   mDisplayAvg.setText("35.7 ºC");
//           }
//        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, @Nullable Bundle bundle) {
        Uri fetchDataUri = AirMonitorContract.ChannelEntries.SAMPLES_URI;
        String sort = AirMonitorContract.ChannelEntries._ID + " DESC LIMIT 60";
        switch (constant){
            case PM:
                String[] projectionPM = {AirMonitorContract.ChannelEntries.COLUMN_PM,
                        AirMonitorContract.ChannelEntries.COLUMN_LONGITUDE,
                        AirMonitorContract.ChannelEntries.COLUMN_LATITUDE
                };

                return new CursorLoader(this,
                        fetchDataUri,
                        projectionPM,
                        null,
                        null,
                        sort
                        );

            case HMDT:
                String[] projectionHMDT = {AirMonitorContract.ChannelEntries.COLUMN_HUMIDITY,
                        AirMonitorContract.ChannelEntries.COLUMN_LONGITUDE,
                        AirMonitorContract.ChannelEntries.COLUMN_LATITUDE
                };

                return new CursorLoader(this,
                        fetchDataUri,
                        projectionHMDT,
                        null,
                        null,
                        sort
                );

            case TEMP:
                String[] projectionTEMP = {AirMonitorContract.ChannelEntries.COLUMN_TEMPERATURE,
                        AirMonitorContract.ChannelEntries.COLUMN_LONGITUDE,
                        AirMonitorContract.ChannelEntries.COLUMN_LATITUDE
                };

                return new CursorLoader(this,
                        fetchDataUri,
                        projectionTEMP,
                        null,
                        null,
                        sort
                );

        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        swapCursor(cursor);
        mCursor.moveToFirst();
        lat = mCursor.getFloat(mCursor.getColumnIndex(AirMonitorContract.ChannelEntries.COLUMN_LATITUDE));
        lon = mCursor.getFloat(mCursor.getColumnIndex(AirMonitorContract.ChannelEntries.COLUMN_LONGITUDE));

        if (constant == PM) {
            mDisplayValue.setText("PM ATUAL: " + String.valueOf(mCursor.getFloat(0)));
            mDisplayStatus.setText(R.string.detail_pm_hint);
        }
        else if (constant == HMDT) {
            mDisplayValue.setText("UMIDADE RELATIVA ATUAL: " + String.valueOf(mCursor.getFloat(0)));
            mDisplayStatus.setText(R.string.detail_humidity_hint);
        }
        else if (constant == TEMP) {
            mDisplayValue.setText("TEMPERATURA ATUAL: " + String.valueOf(mCursor.getFloat(0)));
            mDisplayStatus.setText(R.string.detail_temperature_hint);
        }


        int size = 1;
        float sum = (mCursor.getFloat(0));
        while (mCursor.moveToNext()){
            sum += (mCursor.getFloat(0));
            size += 1;
        }
        mDisplayAvg.setText("MÉDIA DA ÚLTIMA HORA: " + String.format("%.2f", (sum/size)));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
    }
}
