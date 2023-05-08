package com.example.snake4iu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        dataSource = new HighscoreMemoDataSource(this);

        activateAddButton();

        initializeContextualActionBar();


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

        ArrayAdapter<HighscoreMemo> highscoreMemoArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
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
        highscoreMemosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        highscoreMemosListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch(menuItem.getItemId()) {

                    case R.id.cab_delete:
                        SparseBooleanArray touchedHighscoreMemosPositions = highscoreMemosListView.getCheckedItemPositions();
                        for(int i = 0; i < touchedHighscoreMemosPositions.size(); i++) {
                            boolean isChecked = touchedHighscoreMemosPositions.valueAt(i);
                            if(isChecked) {
                                int positionInListView = touchedHighscoreMemosPositions.keyAt(i);
                                HighscoreMemo highscoreMemo = (HighscoreMemo) highscoreMemosListView.getItemAtPosition(positionInListView);
                                Log.d(LOG_TAG, "Position in der ListView: " + positionInListView + " Inhalt: " + highscoreMemo.toString());
                                dataSource.deleteHighscoreMemo(highscoreMemo);
                            }
                        }
                        showAllListEntries();
                        actionMode.finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

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