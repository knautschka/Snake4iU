package com.example.snake4iu

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.TypedValue
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*

class GameManager(context: Context, attributeSet: AttributeSet): SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    companion object {
        var invertedControls = false
    }

    private val boardSize = SettingsActivity.boardSize
    private var pointSize = 0f
    private var w = Resources.getSystem().displayMetrics.widthPixels
    private var h = Resources.getSystem().displayMetrics.heightPixels
    private lateinit var island:Point
    private lateinit var item:Point
    private val islandList = arrayListOf<Point>()

    private val snake = arrayListOf<Point>()
    private val gameEngine = GameEngine(holder, this)
    private var movingDirection = Direction.LEFT
    private var updatedDirection = Direction.LEFT

    private var gameOver = false
    private var islandVisited = 0
    private var level = 1
    private var scorePoints = 0

    private val mpStart = MediaPlayer.create(context, R.raw.startsm)
    private val mpIsland = MediaPlayer.create(context, R.raw.pointsm)
    private val mpDie = MediaPlayer.create(context, R.raw.diesm)
    private val levelUp = MediaPlayer.create(context, R.raw.levelupsm)
    private val itemShorterSnakeSound = MediaPlayer.create(context, R.raw.itemsizesm)
    private val itemColorChangeSound = MediaPlayer.create(context, R.raw.itemcolorsm)
    private val itemBonusPointsSound = MediaPlayer.create(context, R.raw.itembonussm)
    private val itemInvertedControlsSound = MediaPlayer.create(context, R.raw.itemquestionsm)
    private val itemIncreaseSpeedSound = MediaPlayer.create(context, R.raw.itemspeedupsm)
    private val itemGodModeSound = MediaPlayer.create(context, R.raw.itemgodesm)
    private var gameOverSoundPlayed = false
    private var itemSpawn = false
    private var itemChosen = 0
    private var bonusPoints = 1
    private var godMode = false
    private var speedButtonPressed = false
    private var newLevelWait = false
    private var justStarted = true


    init {
        holder.addCallback(this)
        pointSize = w * 0.9f / boardSize

        initGame()
    }

    fun initGame() {
        gameEngine.reset()
        gameOver = false
        gameOverSoundPlayed = false
        itemSpawn = false
        invertedControls = false
        godMode = false
        speedButtonPressed = false
        bonusPoints = 1
        if(SettingsActivity.speed == 1f) {
            SettingsActivity.snakeColor = Color.rgb(255, 165, 0)
        } else if(SettingsActivity.speed == 2f) {
            SettingsActivity.snakeColor = Color.BLACK
        } else if(SettingsActivity.speed == 3f) {
            SettingsActivity.snakeColor = Color.RED
        } else {
            SettingsActivity.snakeColor = Color.rgb(255, 165, 0)
        }
        chooseItem()
        snake.clear()
        islandList.clear()
        level = 1
        scorePoints = 0
        islandVisited = 0
        val initialPoint = Point(Random().nextInt(boardSize - 1), Random().nextInt(boardSize - 1))
        snake.add(initialPoint)
        if(initialPoint.x < boardSize / 2) {
            movingDirection = Direction.RIGHT
            updatedDirection = Direction.RIGHT
        } else {
            movingDirection = Direction.LEFT
            updatedDirection = Direction.LEFT
        }
        generateNewIsland()
        generateNewItem()
        mpStart.start()
        newLevelWait = true
        justStarted = true
    }


    fun generateNewIsland() {
        var valid = false
        islandList.clear()
        while(!valid) {
            valid = true
            for(i in 0..level-1) {
                island = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))

                while(islandList.contains(island)) {
                    island = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))
                }

                islandList.add(island)
            }

            for(snakePoint: Point in snake) {
                for(i in 0..islandList.size-1) {
                    if(islandList.get(i).x.equals(snakePoint.x) && islandList.get(i).y.equals(snakePoint.y))  {
                        islandList.clear()
                        valid = false
                        break
                    }
                }

            }
        }
    }

    fun generateNewItem() {
        item = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))
        while(snake.contains(item) || islandList.contains(item)) {
           item = Point(Random().nextInt(boardSize), Random().nextInt(boardSize))
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

        (context as SnakeActivity).updateLevel(level)
        (context as SnakeActivity).updatePoints(scorePoints)

        if(justStarted) {
            (context as SnakeActivity).newLevel()
        }

        for(i in 0..islandList.size-1) {
            if (snake[0].x == islandList.get(i).x && snake[0].y == islandList.get(i).y && i == islandVisited) {
                islandList.get(i).x = 1000
                islandList.get(i).y = 1000
                islandVisited ++
                updateScorePoints()
                if(islandVisited != islandList.size) {
                    mpIsland.start()
                }

            }

            if(snake[0].x == islandList.get(i).x && snake[0].y == islandList.get(i).y && i != islandVisited && !godMode) {
                gameOver = true
                break
            } else if(snake[0].x == islandList.get(i).x && snake[0].y == islandList.get(i).y && i != islandVisited && godMode) {
                godModeRebirth()
            }
        }

        checkItemSpawn()

        if(itemSpawn) {
            if(snake[0].x == item.x && snake[0].y == item.y) {
                activateItem()
            }
        }


        if(gameOver) {

            (context as SnakeActivity).gameOver()
            if(!gameOverSoundPlayed) {
                mpDie.start()
                gameOverSoundPlayed = true
            }

        }

        if(islandVisited == islandList.size) {
            levelUp.start()
            updateLevel()
            snake.clear()
            generateNewIsland()
            gameEngine.reset()
            itemSpawn = false
            islandVisited = 0
            newLevelWait = true
            godMode = false
            invertedControls = false
            bonusPoints = 1
            speedButtonPressed = false
            invertedControls = false
            (context as SnakeActivity).newLevel()
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

        if(!gameOver && !checkCollision() && !newLevelWait) {
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

    fun goOnWithLevel() {
        newLevelWait = false
        justStarted = false
    }

    fun godModeRebirth() {
        snake.clear()
        val initialPointReborn = Point(Random().nextInt(boardSize - 1), Random().nextInt(boardSize - 1))
        snake.add(initialPointReborn)
        if(initialPointReborn.x < boardSize / 2) {
            movingDirection = Direction.RIGHT
            updatedDirection = Direction.RIGHT
        } else {
            movingDirection = Direction.LEFT
            updatedDirection = Direction.LEFT
        }
    }
    fun checkItemSpawn() {
        if(!itemSpawn) {
            var willSpawn = Random().nextInt(100)
            if(willSpawn <= 25) {
                chooseItem()
                itemSpawn = true
            }
        }
    }

    fun chooseItem() {
        itemChosen = Random().nextInt(6)
    }
    fun activateItem() {
        itemSpawn = false
        generateNewItem()
        if(itemChosen == 0) {
            if(snake.size > 1) {
                itemShorterSnakeSound.start()
                for (i in 0..((snake.size -1) / 2))
                    snake.remove(snake.get(snake.size-1))
            }
        }

        if(itemChosen == 1) {
            itemColorChangeSound.start()

            val red = Random().nextInt(256)
            val green = Random().nextInt(256)
            val blue = 0

            SettingsActivity.snakeColor = Color.rgb(red, green, blue)
        }

        if(itemChosen == 2) {
            itemBonusPointsSound.start()
            bonusPoints = 2
        }

        if(itemChosen == 3) {
            itemInvertedControlsSound.start()
            invertedControls = true
        }

        if(itemChosen == 4) {
            itemIncreaseSpeedSound.start()
            gameEngine.increaseSpeed()
        }

        if(itemChosen == 5) {
            itemGodModeSound.start()
            godMode = true
        }

    }

    fun speedButton() {
        if(!speedButtonPressed) {
            gameEngine.increaseSpeed()
            speedButtonPressed = true
        } else {
            gameEngine.decreaseSpeed()
            speedButtonPressed = false
        }

    }

    fun updateLevel() {
        level ++
        (context as SnakeActivity).updateLevel(level)
    }

    fun updateScorePoints() {
        scorePoints += (10 * level * bonusPoints)
        (context as SnakeActivity).updatePoints(scorePoints)
    }

    fun checkCollision(): Boolean {
        when(updatedDirection) {

            Direction.UP -> {
                if((snake[0].y == 0 || (snake[0] == snake[snake.size-1] && snake.size > 1)) && !godMode) {
                    gameOver = true
                } else if((snake[0].y == 0 || (snake[0] == snake[snake.size-1] && snake.size > 1)) && godMode) {
                    godModeRebirth()
                } else {
                    for(i in 1 until snake.size -1) {
                        if((snake[0].x == snake[i].x && snake[0].y -1 == snake[i].y) && !godMode) {
                            gameOver = true
                            break
                        } else if((snake[0].x == snake[i].x && snake[0].y -1 == snake[i].y) && godMode) {
                            godModeRebirth()
                        }
                    }
                }
            }

            Direction.DOWN -> {
                if((snake[0].y == boardSize -1 || (snake[0] == snake[snake.size-1] && snake.size > 1)) && !godMode) {
                    gameOver = true
                } else if((snake[0].y == boardSize -1 || (snake[0] == snake[snake.size-1] && snake.size > 1)) && godMode) {
                    godModeRebirth()
                } else {
                    for(i in 1 until snake.size -1) {
                        if((snake[0].x == snake[i].x && snake[0].y + 1 == snake[i].y) && !godMode) {
                            gameOver = true
                            break
                        } else if((snake[0].x == snake[i].x && snake[0].y + 1 == snake[i].y) && godMode) {
                            godModeRebirth()
                        }
                    }
                }
            }

            Direction.LEFT -> {
                if((snake[0].x == 0 || (snake[0] == snake[snake.size-1] && snake.size > 1)) && !godMode) {
                    gameOver = true
                } else if((snake[0].x == 0 || (snake[0] == snake[snake.size-1] && snake.size > 1)) && godMode) {
                    godModeRebirth()
                } else {
                    for(i in 1 until snake.size -1) {
                        if((snake[0].y == snake[i].y && snake[0].x - 1 == snake[i].x) && !godMode) {
                            gameOver = true
                            break
                        } else if(((snake[0].y == snake[i].y && snake[0].x - 1 == snake[i].x) && godMode)) {
                            godModeRebirth()
                        }
                    }
                }
            }

            Direction.RIGHT -> {
                if((snake[0].x == boardSize -1 || (snake[0] == snake[snake.size-1] && snake.size > 1)) && !godMode) {
                    gameOver = true
                } else if((snake[0].x == boardSize -1 || (snake[0] == snake[snake.size-1] && snake.size > 1)) && godMode) {
                    godModeRebirth()
                } else {
                    for(i in 1 until snake.size -1) {
                        if((snake[0].y == snake[i].y && snake[0].x + 1 == snake[i].x) && !godMode) {
                            gameOver = true
                            break
                        } else if((snake[0].y == snake[i].y && snake[0].x + 1 == snake[i].x) && godMode) {
                            godModeRebirth()
                        }
                    }
                }
            }
        }

        if(gameOver) {

            (context as SnakeActivity).gameOver()
            if(!gameOverSoundPlayed) {
                mpDie.start()
                gameOverSoundPlayed = true
            }
        }

        return gameOver
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        drawBoard(canvas)
        if(itemSpawn) {
            drawItem(canvas)
        }
        drawIsland(canvas)
        drawNumbers(canvas)


        drawSnake(canvas)

    }

    fun drawBoard(canvas: Canvas?) {
        var backgroundColor = resources.getColor(R.color.gameBoy)
        canvas?.drawColor(backgroundColor)
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

    fun drawIsland(canvas: Canvas?) {

        for(i in 0..islandList.size-1) {
            if (canvas != null) {
                var islandPNG = resources.getDrawable(R.drawable.palm, null)
                var left = (w * 0.05f + islandList.get(i).x * pointSize).toInt()
                var right = (left + pointSize).toInt()
                var top = (h * 0.02f + islandList.get(i).y * pointSize).toInt()
                var bottom = (top + pointSize).toInt()
                islandPNG?.setBounds(left, top, right, bottom)

                islandPNG?.draw(canvas)
            }
        }


    }

    fun drawItem(canvas: Canvas?) {

        lateinit var itemPNG:Drawable

        if(itemChosen == 0) {
            itemPNG = resources.getDrawable(R.drawable.largersnake, null)
        } else if(itemChosen == 1) {
            itemPNG = resources.getDrawable(R.drawable.colorchange, null)
        } else if(itemChosen == 2) {
            itemPNG = resources.getDrawable(R.drawable.bonuspoints, null)
        } else if(itemChosen == 3) {
            itemPNG = resources.getDrawable(R.drawable.question, null)
        } else if(itemChosen == 4) {
            itemPNG = resources.getDrawable(R.drawable.speedup, null)
        } else if(itemChosen == 5) {
            itemPNG = resources.getDrawable(R.drawable.godmode, null)
        } else {
            itemPNG = resources.getDrawable(R.drawable.largersnake, null)
        }


        val left = (w * 0.05f + item.x * pointSize).toInt()
        val right = (left + pointSize).toInt()
        val top = (h * 0.02f + item.y * pointSize).toInt()
        val bottom = (top + pointSize).toInt()
        itemPNG?.setBounds(left, top, right, bottom)
        if (canvas != null) {
            itemPNG?.draw(canvas)
        }
    }

    fun drawNumbers(canvas: Canvas?) {

        val textPaint = Paint()
        textPaint.color = Color.BLACK
        var textSize = 20F
        var numberPositionX = -5F
        var numberPositionY = 5F


        var textSizeRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, resources.displayMetrics)
        var numberPositionXRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, numberPositionX, resources.displayMetrics)
        var numberPositionYRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, numberPositionY, resources.displayMetrics)

        if(boardSize == 20) {
            textSize = 20F
            textSizeRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, resources.displayMetrics)
            numberPositionX = -5F
            numberPositionY = 5F
            numberPositionXRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, numberPositionX, resources.displayMetrics)
            numberPositionYRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, numberPositionY, resources.displayMetrics)
        } else if(boardSize == 30) {
            textSize = 15F
            textSizeRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, resources.displayMetrics)
            numberPositionX = -5F
            numberPositionY = 5F
            numberPositionXRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, numberPositionX, resources.displayMetrics)
            numberPositionYRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, numberPositionY, resources.displayMetrics)
        } else if(boardSize == 40) {
            textSize = 10F
            textSizeRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, resources.displayMetrics)
            numberPositionX = -5F
            numberPositionY = 5F
            numberPositionXRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, numberPositionX, resources.displayMetrics)
            numberPositionYRelative = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, numberPositionY, resources.displayMetrics)
        }

        textPaint.textSize = textSizeRelative
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL

        for(i in 0..islandList.size-1) {
            canvas?.drawText((i+1).toString(),
                getPointRectangle(islandList.get(i)).centerX().toFloat()  + numberPositionXRelative, getPointRectangle(islandList.get(i)).centerY().toFloat() + numberPositionYRelative, textPaint)
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