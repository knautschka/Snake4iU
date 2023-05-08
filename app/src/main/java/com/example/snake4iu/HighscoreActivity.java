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

        dataSource = new HighscoreMemoDataSource(this);

        activateAddButton();

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


    private void activateAddButton() {
        Button buttonAddHighscoreEntry = (Button) findViewById(R.id.addEntry);
        final EditText editTextScoredPoints = (EditText) findViewById(R.id.editTextPoints);
        final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);

        buttonAddHighscoreEntry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String scoredPointsString = editTextScoredPoints.getText().toString();
                String usernameString = editTextUsername.getText().toString();

                if(TextUtils.isEmpty(scoredPointsString)) {
                    editTextScoredPoints.setError("Punktefeld darf nicht leer sein");
                    return;
                }

                if(TextUtils.isEmpty(usernameString)) {
                    editTextUsername.setError("Username-Feld darf nicht leer sein");
                    return;
                }

                int scoredPoints = Integer.parseInt(scoredPointsString);
                editTextScoredPoints.setText("");
                editTextUsername.setText("");

                dataSource.createHighscoreMemo(usernameString, scoredPoints);

                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                showAllListEntries();
            }
        });
    }

}