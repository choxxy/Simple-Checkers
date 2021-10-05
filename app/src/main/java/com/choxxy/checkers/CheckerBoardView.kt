/*
 * The MIT License
 *
 * Copyright 2014 Vitaliy Pavlenko.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.choxxy.checkers

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Checker board View
 */
class CheckerBoardView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var scoreListener: ScoreListener? = null
    private lateinit var squareFactory: BoardAssetFactory
    private val squaresPerSide = 8
    var squareWidth: Int = 0
    private val paint = Paint()
    var currentAlertDialog: AlertDialog? = null
    var gameEngine: GameEngine
    var randomAI: CheckerAI
    var tx = 0
    var ty = 0

    /**
     * Add click listener
     */
    private fun addClickListener() {
        // capture square touching
        setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (gameEngine.gameState != GameEnum.PLAY) {
                    return false
                }
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val x = event.x - tx
                    val y = event.y - ty
                    if (gameEngine.click(Point(x.toInt(), y.toInt()), squareWidth)) {
                        if (gameEngine.getScore(GameEnum.RED) > 11) {
                            showAlert(v, "Red win!")
                        }
                        randomAI.randomMove(gameEngine, false)
                        if (gameEngine.getScore(GameEnum.BLACK) > 11) {
                            showAlert(v, "Black win!")
                        }
                    }
                }
                v.invalidate()
                return true
            }
        })
    }

    /**
     * Display Alert box
     *
     * @param v
     * @param s
     */
    fun showAlert(v: View, s: String?) {
        currentAlertDialog = AlertDialog.Builder(v.context)
            .setTitle(s)
            .setPositiveButton(R.string.ok) { dialog, which ->
                gameEngine = GameEngine(squaresPerSide)
                v.invalidate()
            }
            .setIcon(android.R.drawable.star_on)
            .show()
    }

    /**
     * Resize the board on device flip
     *
     * @param newConfig
     */
    public override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val width = newConfig.screenWidthDp
            val height = newConfig.screenHeightDp
            val minSide = min(width, height)
            squareWidth = floor((minSide / squaresPerSide.toFloat()).toDouble()).toInt()

            // find translation points for centering the board
            tx = (width / 2.0 - squareWidth * squaresPerSide / 2.0).roundToInt()
            ty = (height / 2.0 - squareWidth * squaresPerSide / 2.0).roundToInt()
            squareFactory = BoardAssetFactory(squareWidth)
            // SimpleCheckersActivity.blackScore.rotation = 180f
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val width = newConfig.screenWidthDp
            val height = newConfig.screenHeightDp
            val minSide = min(width, height)
            squareWidth = floor(minSide / squaresPerSide.toFloat() * 0.90).toInt()

            // find translation points for centering the board
            tx = (width / 2.0 - squareWidth * squaresPerSide / 2.0).roundToInt()
            ty = (height / 2.0 - squareWidth * squaresPerSide / 2.0).roundToInt()
            squareFactory = BoardAssetFactory(squareWidth)
            // SimpleCheckersActivity.blackScore.rotation = 360f
        }
    }

    /**
     * Render game board
     *
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (row in 0 until squaresPerSide) {
            for (col in 0 until squaresPerSide) {
                gameEngine.cells[col][row]?.let { cell ->
                    squareFactory.getSquare(cell)?.let { bitmap ->
                        canvas.drawBitmap(
                            bitmap,
                            (tx + col * squareWidth).toFloat(),
                            (ty + row * squareWidth).toFloat(),
                            paint
                        )
                    }
                }
            }
        }

        // update the score
        scoreListener?.upDateScores(
            gameEngine.getScore(GameEnum.RED),
            gameEngine.getScore(GameEnum.BLACK)
        )
    }

    /**
     * Instantiate board View, given the screen size and activity context
     *
     * @param context
     * @param attrs
     */
    init {
        gameEngine = GameEngine(squaresPerSide)
        randomAI = CheckerAI()
        addClickListener()
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Ensure you call it only once :
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                val minSide = min(width, height)
                squareWidth = floor((minSide / squaresPerSide.toFloat()).toDouble()).toInt()

                // find translation points for centering the board
                tx = (width / 2.0 - squareWidth * squaresPerSide / 2.0).roundToInt()
                ty = (height / 2.0 - squareWidth * squaresPerSide / 2.0).roundToInt()
                squareFactory = BoardAssetFactory(squareWidth)
            }
        })
    }

    fun setScoreListener(scoreListener: ScoreListener) {
        this.scoreListener = scoreListener
    }
}

interface ScoreListener {
    fun upDateScores(redScore: Int, blackScore: Int)
}
