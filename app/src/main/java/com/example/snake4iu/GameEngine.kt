package com.example.snake4iu

import android.graphics.Canvas
import android.view.Surface
import android.view.SurfaceHolder
import java.lang.Exception

class GameEngine(var surfaceHolder: SurfaceHolder?, val gameManager: GameManager): Thread() {
    private var running = false
    private var canvas: Canvas? = null
    private var targetFPS = SettingsActivity.speed

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    fun increaseSpeed() {
        targetFPS += 0.1f
    }

    fun reset() {
        targetFPS = SettingsActivity.speed
    }

    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        var targetTime: Long

        while(running) {
            targetTime = (1000 / targetFPS).toLong()
            startTime = System.nanoTime()
            canvas = null

            try {
                canvas = surfaceHolder?.lockCanvas()
                surfaceHolder?.let {
                    synchronized(it) {
                        gameManager.update()
                        gameManager.draw(canvas)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if(canvas != null) {
                    try {
                        surfaceHolder?.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = targetTime - timeMillis

            try {
                if(waitTime > 0) {
                    Thread.sleep(waitTime)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}