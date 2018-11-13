package com.example.airmonitor.airmonitor.Sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.airmonitor.airmonitor.Data.AirMonitorContentProvider;
import com.example.airmonitor.airmonitor.Data.AirMonitorContract;
import com.example.airmonitor.airmonitor.Util.NetworkUtil;
import com.example.airmonitor.airmonitor.Util.NotificationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AirMonitorSyncTask {
    synchronized public static void syncAirQualityData(Context context){
        String response;
        String apiKey = NetworkUtil.API_KEY;
        if (apiKey == null || apiKey.isEmpty()){
            return;
        }
        try {
            response = NetworkUtil.getLastChannelFeed(apiKey);
            if (response == null)
                return;
            JSONObject res = new JSONObject(response);
            String pm = res.getString("field3");
            String gas = res.getString("field4");
            String humidity = res.getString("field2");
            String temp = res.getString("field1");
            String lat = res.getString("latitude");
            String lon = res.getString("longitude");
            String status = res.getString("status");
            String date = res.getString("created_at");

            ContentValues values = new ContentValues();
            values.put(AirMonitorContract.ChannelEntries.COLUMN_TEMPERATURE, Float.parseFloat(temp));
            values.put(AirMonitorContract.ChannelEntries.COLUMN_HUMIDITY, Float.parseFloat(humidity));
            values.put(AirMonitorContract.ChannelEntries.COLUMN_PM, Float.parseFloat(pm));
            values.put(AirMonitorContract.ChannelEntries.COLUMN_CO, Integer.parseInt(gas));
            values.put(AirMonitorContract.ChannelEntries.COLUMN_LATITUDE, Float.parseFloat(lat));
            values.put(AirMonitorContract.ChannelEntries.COLUMN_LONGITUDE, Float.parseFloat(lon));
            if (status == null || Integer.valueOf(status) == 0)
                values.put(AirMonitorContract.ChannelEntries.COLUMN_STATUS, "ERROR");
            else
                values.put(AirMonitorContract.ChannelEntries.COLUMN_STATUS, "OK");
            values.put(AirMonitorContract.ChannelEntries.COLUMN_TIME_STAMP, date);

            ContentResolver resolver = context.getContentResolver();
            if(Float.parseFloat(pm) < 0 || Float.parseFloat(humidity) < 0) return;
            resolver.insert(AirMonitorContract.ChannelEntries.SAMPLES_URI,
                    values);
            if (Integer.parseInt(gas) == 0 && Float.parseFloat(pm) > 0.6){
                NotificationUtil.clearAllNotifications(context);
                NotificationUtil.alertUserFire(context);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return response;  CHANGE TO SOMETHING TO WRITE INTO THE DATABASE
    }
}
