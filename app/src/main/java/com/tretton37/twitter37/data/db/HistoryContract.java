package com.tretton37.twitter37.data.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.lang.reflect.Field;

import static com.tretton37.twitter37.utils.AppConstants.MAX_HISTORY;
import static com.tretton37.twitter37.utils.AppConstants.ZERO;

/**
 * Created by suyashg
 *
 * Database contract. Contains the definition of my tables.
 */
public final class HistoryContract implements HistorySource {

    public static final String CONTENT_AUTHORITY = initAuthority();

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_HISTORY = "history";

    private ContentResolver mContentResolver;

    public HistoryContract(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }

    //region Database Methods
    /**
     * Save a query to the local database.
     *
     * @param query - The query to be saved. Can't be empty or null.
     * @param ms    - The insert date, in millis. As a suggestion, use System.currentTimeMillis();
     **/
    @Override
    public void saveQueryToDb(String query, long ms) {
        if (!TextUtils.isEmpty(query) && ms > ZERO) {
            ContentValues values = new ContentValues();

            values.put(HistoryEntry.COLUMN_QUERY, query);
            values.put(HistoryEntry.COLUMN_INSERT_DATE, ms);
            values.put(HistoryEntry.COLUMN_IS_HISTORY, 1); // Saving as history.

            mContentResolver.insert(HistoryContract.HistoryEntry.CONTENT_URI, values);
        }
    }

    @Override
    public Cursor getHistoryCursor() {
        return mContentResolver.query(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                new String[]{"1"},
                HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " DESC LIMIT " + MAX_HISTORY
        );
    }

    @Override
    public void clearHistory() {
        mContentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                new String[]{"1"}
        );
    }

    @Override
    public void clearAll() {
        mContentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null
        );
    }

    @Override
    public Cursor runQuery(CharSequence constraint) {
        String filter = constraint.toString();
        if (filter.isEmpty()) {
            return getHistoryCursor();
        } else {
            return mContentResolver.query(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    null,
                    HistoryContract.HistoryEntry.COLUMN_QUERY + " LIKE ?",
                    new String[]{"%" + filter + "%"},
                    HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " DESC, " +
                            HistoryContract.HistoryEntry.COLUMN_QUERY
            );
        }
    }
    // ----- Table definitions ----- //

    public static final class HistoryEntry implements BaseColumns {
        // Content provider stuff.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_HISTORY;

        public static final String CONTENT_ITEM =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_HISTORY;

        // Table definition stuff.
        public static final String TABLE_NAME = "SEARCH_HISTORY";

        public static final String COLUMN_QUERY = "query";
        public static final String COLUMN_INSERT_DATE = "insert_date";
        public static final String COLUMN_IS_HISTORY = "is_history";

        public static Uri buildHistoryUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // ----- !Table definitions ----- //

    // ----- Authority setup ----- //

    private static String initAuthority() {
        String authority = "com.tretton37.twitter37.defaultsearchhistorydatabase";
        try {
            ClassLoader clazzLoader = HistoryContract.class.getClassLoader();
            Class<?> clazz = clazzLoader.loadClass("com.tretton37.twitter37.data.db.T37Authority");
            Field field = clazz.getDeclaredField("CONTENT_AUTHORITY");

            authority = field.get(null).toString();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return authority;
    }

}
