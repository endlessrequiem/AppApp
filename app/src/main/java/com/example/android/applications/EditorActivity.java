
package com.example.android.applications;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.android.applications.data.AppContract.AppEntry;

import java.text.DateFormat;
import java.util.Date;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_APP_LOADER = 0;
    private Uri mCurrentAppUri;
    private EditText mOrganizationEditText;
    private EditText mPositionEditText;
    private EditText mStatusEditText;




    private boolean mAppHasChanged = false;

    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mAppHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentAppUri = intent.getData();

        if (mCurrentAppUri == null) {
            setTitle(getString(R.string.editor_activity_title_add_app));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_app));

            getLoaderManager().initLoader(EXISTING_APP_LOADER, null, this);
        }


        mOrganizationEditText = (EditText) findViewById(R.id.edit_app_name);
        mPositionEditText = (EditText) findViewById(R.id.edit_app_pos);
        mStatusEditText = (EditText) findViewById(R.id.edit_status);


    }


    private void saveApp() {
        Date time = new Date();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        String nameString = mOrganizationEditText.getText().toString().trim();
        String posString = mPositionEditText.getText().toString().trim();
        String dateString = dateFormat.format(time);
        String statusString = mStatusEditText.getText().toString().trim();


        ContentValues values = new ContentValues();
        values.put(AppEntry.COLUMN_ORGANIZATION, nameString);
        values.put(AppEntry.COLUMN_POSITION, posString);
        values.put(AppEntry.COLUMN_DATE, dateString);
        values.put(AppEntry.COLUMN_STATUS, statusString);




        if (mCurrentAppUri == null) {
            Uri newUri = getContentResolver().insert(AppEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentAppUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentAppUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveApp();
                Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);
                finish();
                startActivity(intent);
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mAppHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mAppHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AppEntry._ID,
                AppEntry.COLUMN_ORGANIZATION,
                AppEntry.COLUMN_POSITION,
                AppEntry.COLUMN_DATE,
                AppEntry.COLUMN_STATUS
        };

        return new CursorLoader(this,
                mCurrentAppUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(AppEntry.COLUMN_ORGANIZATION);
            int posColumnIndex = cursor.getColumnIndex(AppEntry.COLUMN_POSITION);
            int dateColumnIndex = cursor.getColumnIndex((AppEntry.COLUMN_DATE));

            String name = cursor.getString(nameColumnIndex);
            String pos = cursor.getString(posColumnIndex);
            String time = cursor.getString(dateColumnIndex);

            mOrganizationEditText.setText(name);
            mPositionEditText.setText(pos);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mOrganizationEditText.setText("");
        mPositionEditText.setText("");

    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteApp();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteApp() {
        if (mCurrentAppUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentAppUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}