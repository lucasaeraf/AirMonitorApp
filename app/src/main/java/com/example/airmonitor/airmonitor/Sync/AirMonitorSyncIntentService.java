package com.example.airmonitor.airmonitor.Sync;

import android.app.IntentService;
import android.content.Intent;

public class AirMonitorSyncIntentService extends IntentService {
    public AirMonitorSyncIntentService(){
        super("AirMonitorSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AirMonitorSyncTask.syncAirQualityData(this);
    }
}
