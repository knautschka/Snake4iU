package com.example.snake4iu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.widget.AdapterView;
import android.widget.TextView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.view.inputmethod.InputMethodManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class HighscoreActivity extends AppCompatActivity {

    public static final String LOG_TAG = HighscoreActivity.class.getSimpleName();

    private HighscoreMemoDataSource dataSource;
    TextView highscoreBoard;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    int scoredPoints;
    final String KEY = "savedPreferences";

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);

        return true;
    } */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        dataSource = new HighscoreMemoDataSource(this);

        activateAddButton();

        //initializeContextualActionBar();

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
                HighscoreMemo p1 = (HighscoreMemo) highscoreMemo;
                HighscoreMemo p2 = (HighscoreMemo) t1;

                int i1 = highscoreMemo.getScoredPoints();
                int i2 = t1.getScoredPoints();

                String i1String = String.valueOf(i1);
                String i2String = String.valueOf(i2);

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

    private void initializeContextualActionBar() {
        final ListView highscoreMemosListView = (ListView) findViewById(R.id.highscoreListView);
        highscoreMemosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        highscoreMemosListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.cab_delete:
                        SparseBooleanArray touchedShoppingMemosPositions = highscoreMemosListView.getCheckedItemPositions();
                        for (int i=0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if(isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                HighscoreMemo highscoreMemo = (HighscoreMemo) highscoreMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + highscoreMemo.toString());
                                dataSource.deleteHighscoreMemo(highscoreMemo);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

    }

    private void showPoints() {
        if(preferences.getInt(KEY, 0) < scoredPoints) {
            highscoreBoard.setText("Aktueller Highscore: " + scoredPoints);

            editor.putInt(KEY, scoredPoints);
        }
    }
}