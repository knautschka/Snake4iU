package com.example.snake4iu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class HighscoreMemoDataSource {

    private static final String LOG_TAG = HighscoreMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private HighscoreMemoDbHelper dbHelper;

    private String[] columns = {
            HighscoreMemoDbHelper.COLUMN_ID,
            HighscoreMemoDbHelper.COLUMN_USERNAME,
            HighscoreMemoDbHelper.COLUMN_SCOREDPOINTS
    };

    public HighscoreMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new HighscoreMemoDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    public HighscoreMemo createHighscoreMemo(String username, int scoredPoints) {
        ContentValues values = new ContentValues();
        values.put(HighscoreMemoDbHelper.COLUMN_USERNAME, username);
        values.put(HighscoreMemoDbHelper.COLUMN_SCOREDPOINTS, scoredPoints);

        long insertId = database.insert(HighscoreMemoDbHelper.TABLE_HIGHSCORE_LIST, null, values);

        Cursor cursor = database.query(HighscoreMemoDbHelper.TABLE_HIGHSCORE_LIST,
                columns, HighscoreMemoDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        HighscoreMemo highscoreMemo = cursorToHighscoreMemo(cursor);
        cursor.close();

        return highscoreMemo;
    }

    private HighscoreMemo cursorToHighscoreMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(HighscoreMemoDbHelper.COLUMN_ID);
        int idUsername = cursor.getColumnIndex(HighscoreMemoDbHelper.COLUMN_USERNAME);
        int idScoredPoints = cursor.getColumnIndex(HighscoreMemoDbHelper.COLUMN_SCOREDPOINTS);

        String username = cursor.getString(idUsername);
        int scoredPoints = cursor.getInt(idScoredPoints);
        long id = cursor.getLong(idIndex);

        HighscoreMemo highscoreMemo = new HighscoreMemo(username, scoredPoints, id);

        return highscoreMemo;
    }

    public List<HighscoreMemo> getAllHighscoreMemos() {
        List<HighscoreMemo> highscoreMemoList = new ArrayList<>();

        Cursor cursor = database.query(HighscoreMemoDbHelper.TABLE_HIGHSCORE_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        HighscoreMemo highscoreMemo;

        while(!cursor.isAfterLast()) {
            highscoreMemo = cursorToHighscoreMemo(cursor);
            highscoreMemoList.add(highscoreMemo);
            Log.d(LOG_TAG, "ID: " + highscoreMemo.getId() + ", Inhalt: " + highscoreMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return highscoreMemoList;
    }

    public void deleteHighscoreMemo(HighscoreMemo highscoreMemo) {
        long id = highscoreMemo.getId();

        database.delete(HighscoreMemoDbHelper.TABLE_HIGHSCORE_LIST,
                HighscoreMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + highscoreMemo.toString());
    }
}
