package com.tretton37.twitter37.data.db;

import android.database.Cursor;

public interface HistorySource {

    void saveQueryToDb(String query, long ms);

    Cursor getHistoryCursor();

    void clearHistory();

    void clearAll();

    Cursor runQuery(CharSequence constraint);

}
