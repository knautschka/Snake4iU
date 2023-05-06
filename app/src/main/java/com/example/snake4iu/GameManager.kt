package com.example.snake4iu

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.Random
import java.util.logging.Handler

class GameManager(context: Context, attributeSet: AttributeSet): SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    private val boardSize = 20
    private var pointSize = 0f
    private var w = Resources.getSystem().displayMetrics.widthPixels
    private var h = Resources.getSystem().displayMetrics.heightPixels
    private lateinit var apple:Point
    private val appleList = arrayListOf<Point>()

    private val snake = arrayListOf<Point>()
    private val gameEngine = GameEngine(holder, this)
    private var movingDirection = Direction.LEFT
    private var updatedDirection = Direction.LEFT

    private var gameOver = false;
    private var appleSnacked = 0
    private var level = 1

    private val mpStart = MediaPlayer.create(context, R.raw.snake_start)
    private val handler = android.os.Handler()
    private val mpApple = MediaPlayer.create(context, R.raw.snake_point1)
    private val mpDie = MediaPlayer.create(context, R.raw.snake_die2)

    init {
        holder.addCallback(this)
        pointSize = w * 0.9f / boardSize

        initGame()
    }

    fun initGame() {
        gameEngine.reset()
        gameOver = false
        snake.clear()
        appleList.clear()
        level = 1
        appleSnacked = 0
        val initialPoint = Point(Random().nextInt(boardSize - 1), Random().nextInt(boardSize - 1))
        snake.add(initialPoint)
        if(initialPoint.x < boardSize / 2) {
            movingDirection = Direction.RIGHT
            updatedDirection = Direction.RIGHT
        } else {
            movingDirection = Direction.LEFT
            updatedDirection = Direction.LEFT
        }
        generateNewApple()
        mpStart.start()
    }


    fun generateNewApple() {
        var valid = false
        appleList.clear()
        while(!valid) {
            valid = true
            for(i in 0..level-1) {
                apple = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))
                appleList.add(apple)
            }

            for(i in 0..level-1) {
                for(j in i+1..level-1) {
                    if(appleList.get(i).x == appleList.get(j).x && appleList.get(i).y == appleList.get(j).y) {
                        appleList.clear()
                        valid = false
                        break
                    }
                }
            }

            for(snakePoint: Point in snake) {
                for(i in 0..appleList.size-1) {
                    if(appleList.get(i).x.equals(snakePoint.x) && appleList.get(i).y.equals(snakePoint.y))  {
                        appleList.clear()
                        valid = false
                        break
                    }
                }

            }
        }
    }

    fun move(direction: Direction) {
        if(!(movingDirection == Direction.UP && direction == Direction.DOWN) &&
                !(movingDirection == Direction.DOWN && direction == Direction.UP) &&
                !(movingDirection == Direction.LEFT && direction == Direction.RIGHT) &&
                !(movingDirection == Direction.RIGHT && direction == Direction.LEFT)) {
                updatedDirection = direction
        }
    }

    fun update() {

        for(i in 0..appleList.size-1) {
            if (snake[0].x == appleList.get(i).x && snake[0].y == appleList.get(i).y && i == appleSnacked) {
                appleList.get(i).x = 1000
                appleList.get(i).y = 1000
                gameEngine.increaseSpeed()
                appleSnacked ++
                mpApple.start()
            }

            if(snake[0].x == appleList.get(i).x && snake[0].y == appleList.get(i).y && i != appleSnacked) {
                gameOver = true
                break
            }
        }

        if(gameOver) {
            (context as SnakeActivity).gameOver()
            mpDie.start()
        }

        if(appleSnacked == appleList.size) {
            updateLevel()
            snake.clear()
            generateNewApple()
            appleSnacked = 0
            val initialPoint = Point(Random().nextInt(boardSize - 1), Random().nextInt(boardSize - 1))
            snake.add(initialPoint)
            if(initialPoint.x < boardSize / 2) {
                movingDirection = Direction.RIGHT
                updatedDirection = Direction.RIGHT
            } else {
                movingDirection = Direction.LEFT
                updatedDirection = Direction.LEFT
            }
        }

        if(!gameOver && !checkCollision()) {
            val direction = updatedDirection

            val lastPoint = Point(snake[snake.size - 1].x, snake[snake.size - 1].y)
            snake.add(lastPoint)

            if (snake.size > 1) {
                for (i in snake.size - 1 downTo 1) {
                    if (snake[i].x != snake[i - 1].x) {
                        snake[i].x = snake[i - 1].x
                    } else {
                        snake[i].y = snake[i - 1].y
                    }
                }
            }



            when (direction) {
                Direction.LEFT -> snake[0].x--
                Direction.RIGHT -> snake[0].x++
                Direction.UP -> snake[0].y--
                Direction.DOWN -> snake[0].y++
            }

            movingDirection = updatedDirection
        }
    }

    fun updateLevel() {
        level ++
        (context as SnakeActivity).updateLevel(level)
        handler.postDelayed({
            mpStart.start()
        }, 300)
    }

    fun checkCollision(): Boolean {
        when(updatedDirection) {

            Direction.UP -> {
                if(snake[0].y == 0 || (snake[0] == snake[snake.size-1] && snake.size > 1)) {
                    gameOver = true
                } else {
                    for(i in 1 until snake.size -1) {
                        if(snake[0].x == snake[i].x && snake[0].y -1 == snake[i].y) {
                            gameOver = true
                            break
                        }
                    }
                }
            }

            Direction.DOWN -> {
                if(snake[0].y == boardSize -1 || (snake[0] == snake[snake.size-1] && snake.size > 1)) {
                    gameOver = true
                } else {
                    for(i in 1 until snake.size -1) {
                        if(snake[0].x == snake[i].x && snake[0].y + 1 == snake[i].y) {
                            gameOver = true
                            break
                        }
                    }
                }
            }

            Direction.LEFT -> {
                if(snake[0].x == 0 || (snake[0] == snake[snake.size-1] && snake.size > 1)) {
                    gameOver = true
                } else {
                    for(i in 1 until snake.size -1) {
                        if(snake[0].y == snake[i].y && snake[0].x - 1 == snake[i].x) {
                            gameOver = true
                            break
                        }
                    }
                }
            }

            Direction.RIGHT -> {
                if(snake[0].x == boardSize -1 || (snake[0] == snake[snake.size-1] && snake.size > 1)) {
                    gameOver = true
                } else {
                    for(i in 1 until snake.size -1) {
                        if(snake[0].y == snake[i].y && snake[0].x + 1 == snake[i].x) {
                            gameOver = true
                            break
                        }
                    }
                }
            }
        }

        if(gameOver) {
            (context as SnakeActivity).gameOver()
            mpDie.start()
        }

        return gameOver
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        drawBoard(canvas)
        drawApple(canvas)
        drawNumbers(canvas)
        drawSnake(canvas)

    }

    fun drawBoard(canvas: Canvas?) {
        canvas?.drawRGB(255,255,255)
        val boardLeft = w * 0.05f
        val boardRight = w * 0.95f
        val boardTop = h * 0.02f
        val boardBottom = h * 0.02f + boardSize * pointSize

        val boardBackground = Paint()
        boardBackground.color = Color.rgb(0,188,212)

        canvas?.drawRect(boardLeft, boardTop, boardRight, boardBottom, boardBackground)

        val boardPaint = Paint()
        boardPaint.color = Color.GRAY

        canvas?.drawLine(boardLeft, boardTop, boardLeft, boardBottom, boardPaint)
        canvas?.drawLine(boardLeft, boardTop, boardRight, boardTop, boardPaint)
        canvas?.drawLine(boardLeft, boardBottom, boardRight, boardBottom, boardPaint)
        canvas?.drawLine(boardRight, boardTop, boardRight, boardBottom, boardPaint)


    }

    fun drawApple(canvas: Canvas?) {
            val applePaint = Paint()
            applePaint.color = Color.rgb(255,234,0)

            for(i in 0..appleList.size-1) {
                canvas?.drawRect(getPointRectangle(appleList.get(i)), applePaint)
            }

    }

    fun drawNumbers(canvas: Canvas?) {

        val textPaint = Paint()
        textPaint.color = Color.BLACK
        val textSize = 12
        val textSizeRelative = textSize * resources.displayMetrics.scaledDensity
        textPaint.textSize = textSizeRelative
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL

        for(i in 0..appleList.size-1) {
            canvas?.drawText((i+1).toString(),
                getPointRectangle(appleList.get(i)).centerX().toFloat() -5F, getPointRectangle(appleList.get(i)).centerY().toFloat()+5F, textPaint)
        }

    }


    fun drawSnake(canvas: Canvas?) {
        val snakePaint = Paint()
        snakePaint.color = SettingsActivity.snakeColor

        for(point: Point in snake) {
            canvas?.drawRect(getPointRectangle(point), snakePaint)
        }
    }

    fun getPointRectangle(point: Point): Rect {
        val left = (w * 0.05f + point.x * pointSize).toInt()
        val right = (left + pointSize).toInt()
        val top = (h * 0.02f + point.y * pointSize).toInt()
        val bottom = (top + pointSize).toInt()
        return Rect(left, top, right, bottom)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        gameEngine.setRunning(true)
        gameEngine.start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        gameEngine.surfaceHolder = holder
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        try {
            gameEngine.setRunning(false)
            gameEngine.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}