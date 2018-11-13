package com.example.airmonitor.airmonitor.Data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

public class AirMonitorContract {

    public static final String AUTHORITY = "com.example.airmonitor.airmonitor";
    public static final String SCHEME = "content://";
    public static final Uri BASE_URI = Uri.parse(SCHEME + AUTHORITY);
    public static final String PATH_SAMPLES = "samples";

    public static final class ChannelEntries implements BaseColumns{
        public static final Uri SAMPLES_URI =
                BASE_URI.buildUpon().appendPath(PATH_SAMPLES).build();

        public static final String TABLE_NAME = "channel";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PM = "pm";
        public static final String COLUMN_CO = "co";
        public static final String COLUMN_TIME_STAMP = "time_stamp";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_LATITUDE = "lat";
        public static final String COLUMN_LONGITUDE = "lon";
    }
}
