package com.example.snake4iu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HighscoreMemoDbHelper extends SQLiteOpenHelper {


    private static final String LOG_TAG = HighscoreMemoDbHelper.class.getSimpleName();

    public static final String DB_NAME = "highscore_list.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_HIGHSCORE_LIST = "highscore_list";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_SCOREDPOINTS = "scoredPoints";

    public static final String SQL_CREATE = "CREATE TABLE " + TABLE_HIGHSCORE_LIST +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USERNAME + " TEXT NOT NULL, " +
            COLUMN_SCOREDPOINTS + " INTEGER NOT NULL);";

    public HighscoreMemoDbHelper(Context context) {
        // super(context, "PLATZHALTER_DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + "erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
