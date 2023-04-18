package com.example.snake4iu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_snake.*

class SnakeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake)
    }

    fun onUp(v: View) {
        gameManager.move(Direction.UP)
    }

    fun onDown(v: View) {
        gameManager.move(Direction.DOWN)
    }

    fun onLeft(v: View) {
        gameManager.move(Direction.LEFT)
    }

    fun onRight(v: View) {
        gameManager.move(Direction.RIGHT)
    }

    fun onGameStart(v: View) {
        score.text = "0"
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
}