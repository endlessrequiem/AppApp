
package app.applicationtracker.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;


public final class AppContract {


    public AppContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.applications";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_APPS = "applications";


    public static final class AppEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_APPS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_APPS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_APPS;

        public final static String TABLE_NAME = "applications";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_ORGANIZATION ="company";

        public final static String COLUMN_POSITION = "position";

        public final static String COLUMN_DATE = "date";

        public final static String COLUMN_STATUS = "status";

    }

    public String getTableName() {
        return AppEntry.TABLE_NAME;
    }

}

