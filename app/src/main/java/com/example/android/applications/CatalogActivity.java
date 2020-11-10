
package com.example.android.applications;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.applications.data.AppContract;
import com.example.android.applications.data.AppContract.AppEntry;
import com.example.android.applications.data.AppDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.text.DateFormat;

import static android.app.PendingIntent.getActivity;


public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int APP_LOADER = 0;

    AppCursorAdapter mCursorAdapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        context = this;

        setTitle(getString(R.string.yourApps));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);

            }
        });

        ListView appListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        appListView.setEmptyView(emptyView);

        mCursorAdapter = new AppCursorAdapter(this, null);
        appListView.setAdapter(mCursorAdapter);

        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(AppEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);

                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(APP_LOADER, null, this);


    }

    private void insertApp() {
        Date date = new Date();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        ContentValues values = new ContentValues();
        values.put(AppEntry.COLUMN_ORGANIZATION, "Google");
        values.put(AppEntry.COLUMN_POSITION, "Android Engineer");
        values.put(AppEntry.COLUMN_DATE, dateFormat.format(date));
        values.put(AppEntry.COLUMN_STATUS, "Applied");

        Uri newUri = getContentResolver().insert(AppEntry.CONTENT_URI, values);



    }


    private void deleteAllApplications() {
        AppDbHelper mDbHelper = new AppDbHelper(context);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        AppContract tableName = new AppContract();

        Cursor mCursor = database.rawQuery("SELECT * FROM " + tableName.getTableName(), null);

        if(mCursor.getCount() == 0) {
            Toast.makeText(this, getString(R.string.emptyDB),
                    Toast.LENGTH_SHORT).show();
        } else {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(R.string.alertTitle);
            builder.setMessage(R.string.alertMessage);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(DialogInterface dialog, int id) {
                    int rowsDeleted = getContentResolver().delete(AppEntry.CONTENT_URI, null, null);
                    Log.v("CatalogActivity", rowsDeleted + " rows deleted from application database");
                    dialog.dismiss();
                    looperThread();

                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


    }

    //Multi-threading to run a while loop that makes the FAB disappear while the Snackbar is shown
    private void looperThread() {
        final Handler handler= new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Snackbar confirmDeleteSnackbar = Snackbar.make(findViewById(R.id.fab),
                        getString(R.string.all_apps_deleted),
                        Snackbar.LENGTH_SHORT);

                confirmDeleteSnackbar.show();

                while (confirmDeleteSnackbar.isShown()) {
                    makeFabInvisible();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!confirmDeleteSnackbar.isShown()) {
                            makeFabVisible();
                        }
                    }
                });
            }
        }).start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertApp();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllApplications();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AppEntry._ID,
                AppEntry.COLUMN_ORGANIZATION,
                AppEntry.COLUMN_POSITION,
                AppEntry.COLUMN_DATE,
                AppEntry.COLUMN_STATUS};

        return new CursorLoader(this,
                AppEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    public void makeFabInvisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            }
        });
    }

    public void makeFabVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
            }
        });
    }

}
