package com.example.airmonitor.airmonitor.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class AirMonitorContentProvider extends ContentProvider {

    private AirMonitorDbHelper mDbHelper;
    public static final int SAMPLES = 100;
    public static final int SAMPLES_ID = 101;
    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AirMonitorContract.AUTHORITY, AirMonitorContract.PATH_SAMPLES, SAMPLES);
        uriMatcher.addURI(AirMonitorContract.AUTHORITY, AirMonitorContract.PATH_SAMPLES + "/#", SAMPLES_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new AirMonitorDbHelper(context);

        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor finalCursor;

        switch (match){
            case SAMPLES:
                finalCursor = db.query(AirMonitorContract.ChannelEntries.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SAMPLES_ID:
                String id = uri.getPathSegments().get(1);

                String[] mSelectionArgs = new String[]{id};
                finalCursor = db.query(AirMonitorContract.ChannelEntries.TABLE_NAME,
                        projection,
                        AirMonitorContract.ChannelEntries.COLUMN_TIME_STAMP + "= ?",
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder
                        );
                break;

            default:
                throw new UnsupportedOperationException("Uri desconhecida " + uri);
        }

        finalCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return finalCursor;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented method");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri finalUri;

        int match = sUriMatcher.match(uri);
        switch (match){
            case SAMPLES:
                long id = db.insert(AirMonitorContract.ChannelEntries.TABLE_NAME, null, values);
                if (id > 0){
                    finalUri = ContentUris.withAppendedId(AirMonitorContract.ChannelEntries.SAMPLES_URI, id);
                }
                else
                    throw new android.database.SQLException("Fail to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Uri desconhecida " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return finalUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deletedRows;
        int match = sUriMatcher.match(uri);
        switch (match){
            case SAMPLES:
                deletedRows = db.delete(AirMonitorContract.ChannelEntries.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Uri desconhecida " + uri);
        }

        if (deletedRows > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented method");
    }
}
