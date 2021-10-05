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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.*

/**
 * Generates and caches bitmap images for various board pieces
 */
class BoardAssetFactory(private val sideLength: Int) {
    private val cache: MutableMap<Sprite, Bitmap> = EnumMap(
        Sprite::class.java
    )
    private val fHalfSide: Float = sideLength / 2.0f
    private val fCheckerR: Float = fHalfSide * 0.8f

    private fun blackSquare(): Bitmap? {
        if (cache.containsKey(Sprite.EMPTY)) {
            return cache[Sprite.EMPTY]
        }
        val square = Bitmap.createBitmap(
            sideLength, sideLength, Bitmap.Config.ARGB_8888
        )
        square.eraseColor(Color.DKGRAY)
        cache[Sprite.EMPTY] = square
        return square
    }

    private fun whiteSquare(): Bitmap? {
        if (cache.containsKey(Sprite.INVALID)) {
            return cache[Sprite.INVALID]
        }
        val square = Bitmap.createBitmap(
            sideLength, sideLength, Bitmap.Config.ARGB_8888
        )
        square.eraseColor(Color.WHITE)
        cache[Sprite.INVALID] = square
        return square
    }

    private fun nextMoveSquare(): Bitmap? {
        if (cache.containsKey(Sprite.EMPTY_NEXT)) {
            return cache[Sprite.EMPTY_NEXT]
        }
        val square = Bitmap.createBitmap(
            sideLength, sideLength, Bitmap.Config.ARGB_8888
        )
        square.eraseColor(Color.GRAY)
        cache[Sprite.EMPTY_NEXT] = square
        return square
    }

    private fun scoreSquare(): Bitmap? {
        if (cache.containsKey(Sprite.SCORE)) {
            return cache[Sprite.SCORE]
        }
        val square = Bitmap.createBitmap(
            sideLength, sideLength, Bitmap.Config.ARGB_8888
        )
        square.eraseColor(Color.LTGRAY)
        cache[Sprite.SCORE] = square
        return square
    }

    fun redChecker(): Bitmap? {
        if (cache.containsKey(Sprite.RED_CHECKER)) {
            return cache[Sprite.RED_CHECKER]
        }
        val square = Bitmap.createBitmap(
            sideLength, sideLength, Bitmap.Config.ARGB_8888
        )
        square.eraseColor(Color.DKGRAY)
        val red = Paint(Paint.ANTI_ALIAS_FLAG)
        red.style = Paint.Style.FILL
        red.color = Color.RED
        red.setShadowLayer(1.5f, 0.0f, 2.0f, Color.BLACK)
        val c = Canvas(square)
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR, red)
        val white = Paint(Paint.ANTI_ALIAS_FLAG)
        white.style = Paint.Style.STROKE
        white.color = Color.LTGRAY
        white.strokeWidth = 1.5f
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR, white)
        cache[Sprite.RED_CHECKER] = square
        return square
    }

    fun redCheckerHighlighted(): Bitmap? {
        if (cache.containsKey(Sprite.RED_CHECKER_H)) {
            return cache[Sprite.RED_CHECKER_H]
        }

        // start with red checker
        val square = Bitmap.createBitmap(redChecker()!!)
        val c = Canvas(square)

        // highlight
        val highlight = Paint(Paint.ANTI_ALIAS_FLAG)
        highlight.style = Paint.Style.STROKE
        highlight.strokeWidth = 1.5f
        highlight.color = Color.WHITE
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR, highlight)
        cache[Sprite.RED_CHECKER_H] = square
        return square
    }

    fun redSuperChecker(): Bitmap? {
        if (cache.containsKey(Sprite.RED_CHECKER_S)) {
            return cache[Sprite.RED_CHECKER_S]
        }
        // start with red checker
        val square = Bitmap.createBitmap(redChecker()!!)
        val c = Canvas(square)

        // mark
        val superMark = Paint(Paint.ANTI_ALIAS_FLAG)
        superMark.style = Paint.Style.STROKE
        superMark.strokeWidth = 2.0f
        superMark.color = Color.YELLOW
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR * 0.1f, superMark)
        cache[Sprite.RED_CHECKER_S] = square
        return square
    }

    fun redSuperCheckerHighlighted(): Bitmap? {
        if (cache.containsKey(Sprite.RED_CHECKER_S_H)) {
            return cache[Sprite.RED_CHECKER_S_H]
        }
        // start with red super checker
        val square = Bitmap.createBitmap(redSuperChecker()!!)
        val c = Canvas(square)

        // highlight
        val highlight = Paint(Paint.ANTI_ALIAS_FLAG)
        highlight.style = Paint.Style.STROKE
        highlight.strokeWidth = 1.5f
        highlight.color = Color.WHITE
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR, highlight)
        cache[Sprite.RED_CHECKER_S_H] = square
        return square
    }

    fun blackChecker(): Bitmap? {
        if (cache.containsKey(Sprite.BLACK_CHECKER)) {
            return cache[Sprite.BLACK_CHECKER]
        }
        val square = Bitmap.createBitmap(
            sideLength, sideLength, Bitmap.Config.ARGB_8888
        )
        square.eraseColor(Color.DKGRAY)
        val black = Paint(Paint.ANTI_ALIAS_FLAG)
        black.style = Paint.Style.FILL
        black.color = Color.BLACK
        black.setShadowLayer(1.5f, 0.0f, 2.0f, Color.BLACK)
        val c = Canvas(square)
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR, black)
        val border = Paint(Paint.ANTI_ALIAS_FLAG)
        border.style = Paint.Style.STROKE
        border.color = Color.LTGRAY
        border.strokeWidth = 1.5f
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR, border)
        cache[Sprite.BLACK_CHECKER] = square
        return square
    }

    fun blackCheckerHighlighted(): Bitmap? {
        if (cache.containsKey(Sprite.BLACK_CHECKER_H)) {
            return cache[Sprite.BLACK_CHECKER_H]
        }

        // start with black checker
        val square = Bitmap.createBitmap(blackChecker()!!)
        val c = Canvas(square)

        // highlight
        val highlight = Paint(Paint.ANTI_ALIAS_FLAG)
        highlight.style = Paint.Style.STROKE
        highlight.strokeWidth = 1.5f
        highlight.color = Color.WHITE
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR, highlight)
        cache[Sprite.BLACK_CHECKER_H] = square
        return square
    }

    fun blackSuperChecker(): Bitmap? {
        if (cache.containsKey(Sprite.BLACK_CHECKER_S)) {
            return cache[Sprite.BLACK_CHECKER_S]
        }

        // start with black checker
        val square = Bitmap.createBitmap(blackChecker()!!)
        val c = Canvas(square)

        // mark
        val superMark = Paint(Paint.ANTI_ALIAS_FLAG)
        superMark.style = Paint.Style.STROKE
        superMark.strokeWidth = 2.0f
        superMark.color = Color.YELLOW
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR * 0.1f, superMark)
        cache[Sprite.BLACK_CHECKER_S] = square
        return square
    }

    fun blackSuperCheckerHighlighted(): Bitmap? {
        if (cache.containsKey(Sprite.BLACK_CHECKER_S_H)) {
            return cache[Sprite.BLACK_CHECKER_S_H]
        }

        // start with black checker
        val square = Bitmap.createBitmap(blackSuperChecker()!!)
        val c = Canvas(square)

        // highlight
        val highlight = Paint(Paint.ANTI_ALIAS_FLAG)
        highlight.style = Paint.Style.STROKE
        highlight.strokeWidth = 1.5f
        highlight.color = Color.WHITE
        c.drawCircle(fHalfSide, fHalfSide, fCheckerR, highlight)
        cache[Sprite.BLACK_CHECKER_S_H] = square
        return square
    }

    fun getSquare(cell: Cell): Bitmap? {
        return when (cell.sprite) {
            Sprite.INVALID -> whiteSquare()
            Sprite.SCORE -> scoreSquare()

            Sprite.RED_CHECKER -> {
                redChecker()
            }
            Sprite.RED_CHECKER_S -> {
                redSuperChecker()
            }
            Sprite.RED_CHECKER_H -> {
                redCheckerHighlighted()
            }
            Sprite.RED_CHECKER_S_H -> {
                redSuperCheckerHighlighted()
            }
            Sprite.BLACK_CHECKER -> {
                blackChecker()
            }
            Sprite.BLACK_CHECKER_H -> {
                blackCheckerHighlighted()
            }
            Sprite.BLACK_CHECKER_S -> {
                blackSuperChecker()
            }
            Sprite.BLACK_CHECKER_S_H -> {
                blackSuperCheckerHighlighted()
            }
            Sprite.EMPTY_NEXT -> {
                nextMoveSquare()
            } else -> blackSquare()
        }
    }

    fun genBlackScore(ge: GameEngine?): Bitmap {
        val score = Bitmap.createBitmap(
            sideLength * 8, sideLength, Bitmap.Config.ARGB_8888
        )
        score.eraseColor(Color.BLACK)
        val c = Canvas(score)
        val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        scorePaint.style = Paint.Style.FILL
        scorePaint.strokeWidth = 2.0f
        scorePaint.color = Color.YELLOW
        for (i in 0..7) {
            c.drawRect(
                fHalfSide * 0.5f + i * fHalfSide,
                fHalfSide,
                1f + i * fHalfSide,
                10f,
                scorePaint
            )
        }
        return score
    }

    fun genRedScore(ge: GameEngine?): Bitmap {
        val score = Bitmap.createBitmap(
            sideLength * 8, sideLength, Bitmap.Config.ARGB_8888
        )
        score.eraseColor(Color.LTGRAY)
        return score
    }
}
