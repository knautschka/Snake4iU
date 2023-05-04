package com.example.snake4iu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class TitleScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);
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