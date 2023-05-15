package com.example.snake4iu;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    public static float speed = 1;
    public static int snakeColor = Color.rgb(255,165,0);

    public static int boardSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        RadioButton buttonEasy = findViewById(R.id.radioButtonEasy);
        RadioButton buttonMedium = findViewById(R.id.radioButtonMedium);
        RadioButton buttonHard = findViewById(R.id.radioButtonHard);

        RadioButton buttonS = findViewById(R.id.radioButtonS);
        RadioButton buttonM = findViewById(R.id.radioButtonM);
        RadioButton buttonL = findViewById(R.id.radioButtonL);

        buttonEasy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    speed = 1f;
                    snakeColor = Color.rgb(255,165,0);
                }
            }
        });

        buttonMedium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    speed = 2f;
                    snakeColor = Color.BLACK;
                }
            }
        });

        buttonHard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                speed = 3f;
                snakeColor = Color.RED;
            }
        });

        buttonS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                boardSize = 40;
            }
        });

        buttonM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                boardSize = 30;
            }
        });

        buttonL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                boardSize = 20;
            }
        });
    }



}