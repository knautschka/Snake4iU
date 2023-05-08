package com.example.snake4iu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;

public class TitleScreenActivity extends AppCompatActivity {

    private MediaPlayer startSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);

        startSound = MediaPlayer.create(this, R.raw.startup);
        startSound.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            
            case MotionEvent.ACTION_MOVE:
                Intent intent = new Intent(this, MainActivity.class);

                startActivity(intent);
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}