package com.example.snake4iu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import android.view.inputmethod.InputMethodManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class HighscoreActivity extends AppCompatActivity {

    public static final String LOG_TAG = HighscoreActivity.class.getSimpleName();

    private HighscoreMemoDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        try {
            Objects.requireNonNull(getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            // Ignore the exception if getSupportActionBar() returns null
        }

        dataSource = new HighscoreMemoDataSource(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        showAllListEntries();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
    }

    private void showAllListEntries() {
        List<HighscoreMemo> highscoreMemoList = dataSource.getAllHighscoreMemos();

        Collections.sort(highscoreMemoList, new Comparator<HighscoreMemo>() {
            @Override
            public int compare(HighscoreMemo highscoreMemo, HighscoreMemo t1) {
                return Integer.valueOf(t1.getScoredPoints()).compareTo(highscoreMemo.getScoredPoints());
            }
        });

        ArrayAdapter<HighscoreMemo> highscoreMemoArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                highscoreMemoList);

        ListView highscoreMemosListView = (ListView) findViewById(R.id.highscoreListView);
        highscoreMemosListView.setAdapter(highscoreMemoArrayAdapter);


    }



}