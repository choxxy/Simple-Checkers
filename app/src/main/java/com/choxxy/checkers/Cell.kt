package com.choxxy.checkers

import android.graphics.Rect

data class Cell(val col: Int, val row: Int, var sprite: Sprite) {

    fun hitTest(x: Int, y: Int, width: Int): Boolean {
        val rect = Rect(
            col * width, row * width,
            col * width + width, row * width + width
        )
        return rect.contains(x, y)
    }
}
