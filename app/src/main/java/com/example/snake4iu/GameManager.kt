package com.example.snake4iu

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.MediaPlayer
import android.text.TextPaint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.Objects
import java.util.Random

class GameManager(context: Context, attributeSet: AttributeSet): SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    private val boardSize = 20
    private var pointSize = 0f
    private var w = Resources.getSystem().displayMetrics.widthPixels
    private var h = Resources.getSystem().displayMetrics.heightPixels
    private lateinit var apple:Point
    private lateinit var apple2:Point
    private lateinit var apple3:Point

    private val snake = arrayListOf<Point>()
    private val gameEngine = GameEngine(holder, this)
    private var movingDirection = Direction.LEFT
    private var updatedDirection = Direction.LEFT

    private var gameOver = false;
    private var score = 0
    private var appleSnacked = 0

    private val mpStart = MediaPlayer.create(context, R.raw.snake_start)
    private val mpApple = MediaPlayer.create(context, R.raw.snake_point)
    private val mpDie = MediaPlayer.create(context, R.raw.snake_die)

    init {
        holder.addCallback(this)
        pointSize = w * 0.9f / boardSize

        initGame()
    }

    fun initGame() {
        gameEngine.reset()
        gameOver = false
        snake.clear()
        appleSnacked == 0
        val initialPoint = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))
        snake.add(initialPoint)
        if(initialPoint.x < boardSize / 2) {
            movingDirection = Direction.RIGHT
            updatedDirection = Direction.RIGHT
        } else {
            movingDirection = Direction.LEFT
            updatedDirection = Direction.LEFT
        }

        generateNewApple()


        score = 0

        mpStart.start()
    }


    fun generateNewApple() {
        var valid = false
        while(!valid) {
            valid = true
            apple = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))
            apple2 = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))
            apple3 = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))

            for(snakePoint: Point in snake) {
                if((apple.x == snakePoint.x && apple.y == snakePoint.y) ||
                        (apple2.x == snakePoint.x && apple2.y == snakePoint.y) ||
                        (apple3.x == snakePoint.x && apple3.y == snakePoint.y))  {
                    valid = false
                    break
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
        if(!gameOver && !checkCollision()) {
            val direction = updatedDirection

            val lastPoint = Point(snake[snake.size - 1].x, snake[snake.size - 1].y)

            if (snake.size > 1) {
                for (i in snake.size - 1 downTo 1) {
                    if (snake[i].x != snake[i - 1].x) {
                        snake[i].x = snake[i - 1].x
                    } else {
                        snake[i].y = snake[i - 1].y
                    }
                }
            }

            if (snake[0].x == apple.x && snake[0].y == apple.y) {
                snake.add(lastPoint)
                apple.x = 1000
                    apple.y = 1000
                gameEngine.increaseSpeed()
                appleSnacked ++
                updateScore()
                mpApple.start()
            }

            if (snake[0].x == apple2.x && snake[0].y == apple2.y && appleSnacked == 1) {
                snake.add(lastPoint)
                apple2.x = 1000
                apple2.y = 1000
                gameEngine.increaseSpeed()
                appleSnacked ++
                updateScore()
                mpApple.start()
            }
            if (snake[0].x == apple3.x && snake[0].y == apple3.y && appleSnacked == 2) {
                snake.add(lastPoint)
                apple3.x = 1000
                apple3.y = 1000
                gameEngine.increaseSpeed()
                appleSnacked = 0
                updateScore()
                mpApple.start()
            }

            if(score % 3 == 0 && score > 0) {
                generateNewApple()
                score = 0
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

    fun updateScore() {
        score ++
        (context as SnakeActivity).updateScore(score)
    }

    fun checkCollision(): Boolean {
        when(updatedDirection) {
            Direction.UP -> {
                if(snake[0].y == 0) {
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
                if(snake[0].y == boardSize -1) {
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
                if(snake[0].x == 0) {
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
                if(snake[0].x == boardSize -1) {
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
        val boardTop = h * 0.1f
        val boardBottom = h * 0.1f + boardSize * pointSize

        val boardPaint = Paint()
        boardPaint.color = Color.BLACK

        canvas?.drawLine(boardLeft, boardTop, boardLeft, boardBottom, boardPaint)
        canvas?.drawLine(boardLeft, boardTop, boardRight, boardTop, boardPaint)
        canvas?.drawLine(boardLeft, boardBottom, boardRight, boardBottom, boardPaint)
        canvas?.drawLine(boardRight, boardTop, boardRight, boardBottom, boardPaint)
    }

    fun drawApple(canvas: Canvas?) {
            val applePaint = Paint()
            applePaint.color = Color.GREEN

            canvas?.drawRect(getPointRectangle(apple), applePaint)
            canvas?.drawRect(getPointRectangle(apple2), applePaint)
            canvas?.drawRect(getPointRectangle(apple3), applePaint)
    }

    fun drawNumbers(canvas: Canvas?) {

        val textPaint = Paint()
        textPaint.color = Color.BLACK
        textPaint.textSize = 40F
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL

        canvas?.drawText("1",
            getPointRectangle(apple).centerX().toFloat() -10F, getPointRectangle(apple).centerY().toFloat()+10F, textPaint)
        canvas?.drawText("2",
            getPointRectangle(apple2).centerX().toFloat() -10F, getPointRectangle(apple2).centerY().toFloat()+10F, textPaint)
        canvas?.drawText("3",
            getPointRectangle(apple3).centerX().toFloat() -10F, getPointRectangle(apple3).centerY().toFloat()+10F, textPaint)
    }


    fun drawSnake(canvas: Canvas?) {
        val snakePaint = Paint()
        snakePaint.color = Color.BLUE

        for(point: Point in snake) {
            canvas?.drawRect(getPointRectangle(point), snakePaint)
        }
    }

    fun getPointRectangle(point: Point): Rect {
        val left = (w * 0.05f + point.x * pointSize).toInt()
        val right = (left + pointSize).toInt()
        val top = (h * 0.1f + point.y * pointSize).toInt()
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