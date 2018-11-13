package com.example.airmonitor.airmonitor.Sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.airmonitor.airmonitor.Data.AirMonitorContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.sql.Driver;

public class AirMonitorSyncUtil {
    private static final int SYNC_INTERVAL = 30; //Tempo em segundos responsável por buscar dados
    private static final int SYNC_TIME_WINDOW = 5;

    private static boolean sInitialized;
    private static final String AIRMONITOR_SYNC_TAG = "airmonito-sync";

    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context){
        GooglePlayDriver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncAirMonitorJob = dispatcher.newJobBuilder()
                .setService(AirMonitorFirebaseJobService.class)
                .setTag(AIRMONITOR_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(SYNC_INTERVAL, SYNC_INTERVAL + SYNC_TIME_WINDOW))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncAirMonitorJob);

    }

    synchronized public static void initialize(@NonNull final Context context){
        if (sInitialized)
            return;

        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        Thread checkDb = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri queryUri = AirMonitorContract.ChannelEntries.SAMPLES_URI;

                String[] selectArgs = {AirMonitorContract.ChannelEntries._ID};

                Cursor cursor = context.getContentResolver().query(
                        queryUri,
                        selectArgs,
                        null,
                        null,
                        null
                );

                if (cursor == null || cursor.getCount() == 0){
                    startSync(context);
                }

                cursor.close();
            }
        });

        checkDb.start();

    }

    public static void startSync(@NonNull final Context context){
        Intent intent = new Intent(context, AirMonitorSyncIntentService.class);
        context.startService(intent);
    }
}
