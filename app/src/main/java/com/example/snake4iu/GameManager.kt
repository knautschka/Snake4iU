package com.example.snake4iu

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.Random

class GameManager(context: Context, attributeSet: AttributeSet): SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    private val boardSize = 20
    private var pointSize = 0f
    private var w = Resources.getSystem().displayMetrics.widthPixels
    private var h = Resources.getSystem().displayMetrics.heightPixels
    private lateinit var apple:Point
    private val snake = arrayListOf<Point>()
    private val gameEngine = GameEngine(holder, this)
    private var movingDirection = Direction.LEFT
    private var updatedDirection = Direction.LEFT

    init {
        holder.addCallback(this)
        pointSize = w * 0.9f / boardSize

        initGame()
    }

    fun initGame() {
        snake.clear()
        val initialPoint = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))
        snake.add(initialPoint)
        if(initialPoint.x < boardSize / 2) {
            movingDirection = Direction.RIGHT
            updatedDirection = Direction.RIGHT
        }

        generateNewApple()
    }

    fun generateNewApple() {
        var valid = false
        while(!valid) {
            valid = true
            apple = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))
            for(snakePoint: Point in snake) {
                if(apple.x == snakePoint.x && apple.y == snakePoint.y) {
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
        val direction = updatedDirection

        val lastPoint = Point(snake[snake.size - 1].x, snake[snake.size -1].y)

        if(snake.size > 1) {
            for(i in snake.size - 1 downTo 1) {
                if(snake[i].x != snake[i-1].x) {
                    snake[i].x = snake[i-1].x
                } else {
                    snake[i].y = snake[i-1].y
                }
            }
        }

        when(direction) {
            Direction.LEFT -> snake[0].x --
            Direction.RIGHT -> snake[0].x ++
            Direction.UP -> snake[0].y --
            Direction.DOWN -> snake[0].y ++
        }

        movingDirection = updatedDirection
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        drawBoard(canvas)
        drawApple(canvas)
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