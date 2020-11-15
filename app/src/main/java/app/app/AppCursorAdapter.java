
package app.app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import app.app.R;

import app.app.data.AppContract.AppEntry;
import app.app.data.AppProvider;


public class AppCursorAdapter extends CursorAdapter {

    AppProvider mAppProvider;



    public AppCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.name);
        TextView posTextView = view.findViewById(R.id.postiton);
        TextView dateTextView = view.findViewById(R.id.date);
        TextView statusTextView = view.findViewById(R.id.appStatus);



        int nameColumnIndex = cursor.getColumnIndex(AppEntry.COLUMN_ORGANIZATION);
        int posColumnIndex = cursor.getColumnIndex(AppEntry.COLUMN_POSITION);
        int dateColumnIndex = cursor.getColumnIndex(AppEntry.COLUMN_DATE);
        int statusColumnIndex = cursor.getColumnIndex(AppEntry.COLUMN_STATUS);
        int idColumnIndex = cursor.getColumnIndex(AppEntry._ID);


        String appName = cursor.getString(nameColumnIndex);
        String appPosition = cursor.getString(posColumnIndex);
        String appDate = cursor.getString(dateColumnIndex);
        String appStatus = cursor.getString(statusColumnIndex);
        String appId = cursor.getString(idColumnIndex);



        if (TextUtils.isEmpty(appPosition)) {
            appPosition = context.getString(R.string.unknown_pos);
            posTextView.setTextColor(Color.parseColor("#F0B030"));
        }
        if (appPosition.contains(context.getString(R.string.unknown_pos))) {
            posTextView.setTextColor(Color.parseColor("#F0B030"));
        }

        if (TextUtils.isEmpty(appStatus)) {
            appStatus = context.getString(R.string.status_not_specified);
            statusTextView.setTextColor(Color.parseColor("#F0B030"));
        }

        String statusColorCheck = appStatus.toLowerCase();

        if (statusColorCheck.contains("reject") ||
                statusColorCheck.contains("denied") ||
                statusColorCheck.contains("refused") ||
                statusColorCheck.contains("decline")) {
            statusTextView.setTextColor(Color.RED);
        }
        if (statusColorCheck.contains("interview")) {
            statusTextView.setTextColor(Color.BLUE);
        }
        if (statusColorCheck.contains("accept")) {
            statusTextView.setTextColor(Color.parseColor("#558B2F"));
        }

        String nameIdFormat = String.format("%s %s%s%s%s", appName, "(" , "#" ,appId, ")");

        nameTextView.setText(nameIdFormat);
        posTextView.setText(appPosition);
        dateTextView.setText(String.format("%s %s", context.getString(R.string.LastUpdatedOn), appDate));
        statusTextView.setText(String.format("%s %s", context.getString(R.string.status), appStatus));


    }

}
