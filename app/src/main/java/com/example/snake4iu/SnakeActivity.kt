package com.example.snake4iu

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.example.snake4iu.HighscoreActivity.LOG_TAG
import kotlinx.android.synthetic.main.activity_snake.*

class SnakeActivity : AppCompatActivity() {

    var scoredPoints = 0
    var highscoreSaved = false
    var level = 1
    var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        setContentView(R.layout.activity_snake)

    }


    fun onUp(v: View) {
        if(!GameManager.invertedControls) {
            gameManager.move(Direction.UP)
        } else {
            gameManager.move(Direction.DOWN)
        }

        vibrate()
    }

    fun onDown(v: View) {
        if(!GameManager.invertedControls) {
            gameManager.move(Direction.DOWN)
        } else {
            gameManager.move(Direction.UP)
        }

        vibrate()
    }

    fun onLeft(v: View) {
        if(!GameManager.invertedControls) {
            gameManager.move(Direction.LEFT)
        } else {
            gameManager.move(Direction.RIGHT)
        }

        vibrate()
    }

    fun onRight(v: View) {
        if(!GameManager.invertedControls) {
            gameManager.move(Direction.RIGHT)
        } else {
            gameManager.move(Direction.LEFT)
        }

        vibrate()
    }

    fun onButtonB(v: View) {
        gameManager.speedButton()
    }

    fun onButtonA(v: View) {
        if(!gameOver) {
            gameManager.goOnWithLevel()
        } else {
            onGameStart(v)
        }


        runOnUiThread() {
            newLevel.visibility = GONE
        }
    }

    fun onGameStart(v: View) {
        gameOver = false
        score.text = "Level 1"
        scorePoints.text = "Punkte: 0"
        gameOverTextView.visibility = GONE
        gameManager.initGame()
        highscoreSaved = false
    }

    fun newLevel() {
        runOnUiThread() {
            newLevel.visibility = View.VISIBLE
            newLevel.text = "Drücke A zum losfahren!\n Aktuelles Level: $level"
        }
    }
    fun gameOver() {

        if(!highscoreSaved && scoredPoints > 0) {
            var dataSource = HighscoreMemoDataSource(this)

            Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.")
            dataSource.open()

            val highscoreMemo = dataSource.createHighscoreMemo("Spieler 1", scoredPoints)
            Log.d(LOG_TAG, "Es wurde der folgende Eintrag in die Datenbank geschrieben:")
            Log.d(LOG_TAG, "ID: ${highscoreMemo.id}, Inhalt: $highscoreMemo")


            Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.")

            dataSource.close()

            highscoreSaved = true
        }

        gameOver = true


        runOnUiThread() {
            gameOverTextView.visibility = View.VISIBLE
            gameOverTextView.text =
                "Game Over\n Erreichte Punkte: $scoredPoints\n Erreichtes Level: $level\nA drücken für Neustart"
        }
    }

    fun updateLevel(newLevel: Int) {
        level = newLevel
        runOnUiThread() {
            score.text = "Level $newLevel"
        }
    }

    fun updatePoints(newPoints: Int) {
        scoredPoints = newPoints
        runOnUiThread() {
            scorePoints.text = "Punkte: $newPoints"
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(vibrator.hasVibrator()) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(23, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(23)
            }
        }
    }

}