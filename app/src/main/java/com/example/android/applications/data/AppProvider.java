
package com.example.android.applications.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.applications.data.AppContract.AppEntry;

import static com.example.android.applications.data.AppContract.AppEntry.TABLE_NAME;

public class AppProvider extends ContentProvider {

    public static final String LOG_TAG = AppProvider.class.getSimpleName();

    private static final int APPS = 100;

    private static final int APP_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AppContract.CONTENT_AUTHORITY, AppContract.PATH_APPS, APPS);

        sUriMatcher.addURI(AppContract.CONTENT_AUTHORITY, AppContract.PATH_APPS + "/#", APP_ID);
    }

    /** Database helper object */
    private AppDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new AppDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case APPS:
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case APP_ID:
                selection = AppEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case APPS:
                return insertApp(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertApp(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(AppEntry.COLUMN_ORGANIZATION);
        if (name == null) {
            throw new IllegalArgumentException("Enter a company name");
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case APPS:
                return updateApp(uri, contentValues, selection, selectionArgs);
            case APP_ID:

                selection = AppEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateApp(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateApp(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(AppEntry.COLUMN_ORGANIZATION)) {
            String name = values.getAsString(AppEntry.COLUMN_ORGANIZATION);
            if (name == null) {
                throw new IllegalArgumentException("Enter a company name");
            }
        }


        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case APPS:
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case APP_ID:
                selection = AppEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case APPS:
                return AppEntry.CONTENT_LIST_TYPE;
            case APP_ID:
                return AppEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    public int getAppsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
