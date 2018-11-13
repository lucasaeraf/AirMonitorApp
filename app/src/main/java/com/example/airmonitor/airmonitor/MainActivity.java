package com.example.airmonitor.airmonitor;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.airmonitor.airmonitor.Data.AirMonitorContract;
import com.example.airmonitor.airmonitor.Data.AirMonitorDbHelper;
import com.example.airmonitor.airmonitor.Sync.AirMonitorSyncUtil;
import com.example.airmonitor.airmonitor.Util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO (1): Mover as constantes para um arquivo de Resources, para que todas as classes tenham acesso
    final private int TEMP = 1;
    final private int HMDT = 2;
    final private String CO = "CO";
    final private int PM = 3;
    final private String API_KEY = "APIKEY";
    final private static int FETCH_DATA_LOADER = 11;
    final private String EMERGENCY_PHONE = "193";

    public static final String[] AIRMONITOR_PROJECTION = {
            AirMonitorContract.ChannelEntries.COLUMN_TEMPERATURE,
            AirMonitorContract.ChannelEntries.COLUMN_HUMIDITY,
            AirMonitorContract.ChannelEntries.COLUMN_CO,
            AirMonitorContract.ChannelEntries.COLUMN_PM,
            AirMonitorContract.ChannelEntries.COLUMN_LATITUDE,
            AirMonitorContract.ChannelEntries.COLUMN_LONGITUDE,
            AirMonitorContract.ChannelEntries.COLUMN_STATUS,
            AirMonitorContract.ChannelEntries.COLUMN_TIME_STAMP
    };

    private Button mButtonTemp;
    private Button mButtonHumidity;
    private Button mButtonPM;
    private Button mButtonCO;
    private TextView mTextDate;
    private TextView mLastUpdate;
    private TextView mStatus;
    private String apiKey;
    private Cursor mCursor;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AirMonitorDbHelper dbHelper = new AirMonitorDbHelper(this);

        mButtonTemp = (Button) findViewById(R.id.Temperature);
        mButtonHumidity = (Button) findViewById(R.id.Humidity);
        mButtonCO = (Button) findViewById(R.id.CO);
        mButtonPM = (Button) findViewById(R.id.PM);
        mTextDate = (TextView) findViewById(R.id.date);
        mLastUpdate = (TextView) findViewById(R.id.lastUpdate);
        mStatus = (TextView) findViewById(R.id.status);

        mDb = dbHelper.getWritableDatabase();
        apiKey = "6Q4GC2JNZIQD7Q1R";
//
//        Bundle fetchBundle = new Bundle();
//        fetchBundle.putString(API_KEY, apiKey);
//
//        LoaderManager loaderManager = getSupportLoaderManager();
//        Loader<String> fetchLoader = loaderManager.getLoader(FETCH_DATA_LOADER);
//
//        if (fetchLoader == null){
//            loaderManager.initLoader(FETCH_DATA_LOADER, fetchBundle, this);
//        }
//        else {
//            loaderManager.restartLoader(FETCH_DATA_LOADER, fetchBundle, this);
//        }

        mButtonTemp.setOnClickListener((v) -> {
            Context context = MainActivity.this;
            Class newActivity = DetailsActivity.class;
            Intent intent = new Intent(context, newActivity);
            intent.putExtra(Intent.EXTRA_TEXT, TEMP);

            startActivity(intent);
        });

        mButtonHumidity.setOnClickListener((v) -> {
            Context context = MainActivity.this;
            Class newActivity = DetailsActivity.class;
            Intent intent = new Intent(context, newActivity);
            intent.putExtra(Intent.EXTRA_TEXT, HMDT);

            startActivity(intent);
        });

        mButtonPM.setOnClickListener((v) -> {
            Context context = MainActivity.this;
            Class newActivity = DetailsActivity.class;
            Intent intent = new Intent(context, newActivity);
            intent.putExtra(Intent.EXTRA_TEXT, PM);

            startActivity(intent);
        });

        AirMonitorSyncUtil.initialize(this);
        getSupportLoaderManager().initLoader(FETCH_DATA_LOADER, null, this);


    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, final Bundle bundle) {
        Uri fetchDataUri = AirMonitorContract.ChannelEntries.SAMPLES_URI;
        String sort = AirMonitorContract.ChannelEntries._ID + " DESC";

        return new CursorLoader(this,
                fetchDataUri,
                AIRMONITOR_PROJECTION,
                null,
                null,
                sort);
/*

        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading(){
                super.onStartLoading();
                if (bundle == null){
                    return;
                }
                else forceLoad();
                //preencher com alguma indicação que tá carregando coisas
            }
            @Override
            public Cursor loadInBackground() {
                String response = null;
                String apiKey = bundle.getString(API_KEY);
                if (apiKey == null || apiKey.isEmpty()){
                    return null;
                }
                try {
                    response = NetworkUtil.getLastChannelFeed(apiKey);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        };*/
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0){
            swapCursor(cursor);
            mCursor.moveToFirst();


            float pm = mCursor.getFloat(mCursor.getColumnIndex(AirMonitorContract.ChannelEntries.COLUMN_PM));
            float temp = mCursor.getFloat(mCursor.getColumnIndex(AirMonitorContract.ChannelEntries.COLUMN_TEMPERATURE));
            float humidity = mCursor.getFloat(mCursor.getColumnIndex(AirMonitorContract.ChannelEntries.COLUMN_HUMIDITY));
            int gas = mCursor.getInt(mCursor.getColumnIndex(AirMonitorContract.ChannelEntries.COLUMN_CO));
            String lastUpdate = mCursor.getString(mCursor.getColumnIndex(AirMonitorContract.ChannelEntries.COLUMN_TIME_STAMP));
            int status;


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            SimpleDateFormat output = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
            Date d = null;
            try {
                d = sdf.parse(lastUpdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            lastUpdate = output.format(d);

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
            String date = df.format(calendar.getTime());

            mButtonPM.setText("PM: " + String.valueOf(pm) + " mg/m³");
            if (gas > 0)
                mButtonCO.setText("Sem presença de gases tóxicos");
            else
                mButtonCO.setText("Presença de gases tóxicos detectada");
            mButtonHumidity.setText("Umidade relativa: " + String.valueOf(humidity) + " %");
            mButtonTemp.setText("Temperatura: " + String.valueOf(temp) + " ºC");
            mTextDate.setText("Date:\n" + date);
            mLastUpdate.setText("last update:\n" + lastUpdate);
            if (gas == 0 && pm > 0.6) {
                mStatus.setText("Status:\nALERTA DE FOGO!!!");
                mStatus.setTextColor(Color.WHITE);
                mStatus.setBackgroundColor(Color.RED);
            }
            else if (gas == 0) {
                mStatus.setText("Status:\nSeu bebê está exposto a gases tóxicos");
            }
            else
                mStatus.setText("Status:\nOK!");

            //mButtonTemp.setOnClickListener(v -> );
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.call_fire) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", EMERGENCY_PHONE, null)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void swapCursor(Cursor newCursor){
        mCursor = newCursor;
    }
}
