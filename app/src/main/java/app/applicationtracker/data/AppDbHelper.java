
package app.applicationtracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.applicationtracker.data.AppContract.AppEntry;


public class AppDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = AppDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "applications.db";

    private static final int DATABASE_VERSION = 1;

    public AppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the applications table
        String SQL_CREATE_APPS_TABLE =  "CREATE TABLE " + AppEntry.TABLE_NAME + " ("
                + AppEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AppEntry.COLUMN_ORGANIZATION + " TEXT NOT NULL, "
                + AppEntry.COLUMN_POSITION + " TEXT, "
                + AppEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + AppEntry.COLUMN_STATUS + " TEXT NOT NULL );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_APPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}