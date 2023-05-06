package com.example.snake4iu

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_snake.*

class SnakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        setContentView(R.layout.activity_snake)

    }

    fun onUp(v: View) {
        gameManager.move(Direction.UP)
        vibrate()
    }

    fun onDown(v: View) {
        gameManager.move(Direction.DOWN)
        vibrate()
    }

    fun onLeft(v: View) {
        gameManager.move(Direction.LEFT)
        vibrate()
    }

    fun onRight(v: View) {
        gameManager.move(Direction.RIGHT)
        vibrate()
    }

    fun onGameStart(v: View) {
        score.text = "Level 1"
        gameOver.visibility = View.GONE
        gameManager.initGame()
    }

    fun gameOver() {
        runOnUiThread() {
            gameOver.visibility = View.VISIBLE
        }
    }

    fun updateLevel(newLevel: Int) {
        runOnUiThread() {
            score.text = "Level " + newLevel.toString()
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(vibrator.hasVibrator()) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(23, VibrationEffect.EFFECT_CLICK))
            } else {
                vibrator.vibrate(23)
            }
        }
    }

}