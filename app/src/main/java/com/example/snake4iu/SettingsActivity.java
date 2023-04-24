package com.example.snake4iu;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    public static float speed = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        RadioButton buttonEasy = findViewById(R.id.radioButtonEasy);
        RadioButton buttonMedium = findViewById(R.id.radioButtonMedium);
        RadioButton buttonHard = findViewById(R.id.radioButtonHard);

        buttonEasy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    speed = 1f;
                }
            }
        });

        buttonMedium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    speed = 2f;
                }
            }
        });

        buttonHard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                speed = 3f;
            }
        });
    }



}