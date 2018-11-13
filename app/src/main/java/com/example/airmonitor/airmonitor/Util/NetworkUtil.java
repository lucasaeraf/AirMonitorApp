package com.example.airmonitor.airmonitor.Util;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();
    final static String THING_SPEAK_API_URL = "http://api.thingspeak.com/channels";

    final static String CHANNEL = "601803";

    final static String FEED = "feeds";
    final static String LAST = "last";
    final static String FORMAT = "json";

    public final static String NUM_ENTRIES_PARAM = "results";
    public final static String API_KEY_PARAM = "api_key";
    public final static String TZ_PARAM = "timezone"; //America/Sao_Paulo
    public final static String STATUS_PARAM = "status"; //<true-false>
    public final static String LOCATION_PARAM = "location"; //<true-false>
    public final static String API_KEY = "6Q4GC2JNZIQD7Q1R";

    private static URL buildURL(String channel, String pathBasic, String pathCompletion, String apiKey){
        Uri.Builder builder = Uri.parse(THING_SPEAK_API_URL).buildUpon().appendPath(channel)
                .appendPath(pathBasic);
        if(pathCompletion != null && !pathCompletion.isEmpty())
            builder.appendPath(pathCompletion);

        builder.appendQueryParameter(TZ_PARAM, "America/Sao_Paulo")
                .appendQueryParameter(STATUS_PARAM, "true")
                .appendQueryParameter(LOCATION_PARAM, "true")
                .appendQueryParameter(API_KEY_PARAM, apiKey);

        URL url = null;
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "URL: " + url);
        return url;
    }

    public static String getLastChannelFeed(String apiKey) throws IOException{
        URL getURL = buildURL(CHANNEL, FEED,LAST + "." + FORMAT, apiKey);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(getURL).build();
        Response response = client.newCall(request).execute();
        return  response.body().string();
    }
}
