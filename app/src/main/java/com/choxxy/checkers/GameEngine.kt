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

import android.graphics.Point
import kotlin.math.roundToInt

/**
 * Handles player input and checker game rules
 */
class GameEngine(size: Int) {
    /**
     * records current player info
     */
    inner class CheckerPieces {
        var checkerPiece: Sprite = Sprite.EMPTY
        var row = 0
        var col = 0
    }

    /**
     * Returns current game state
     *
     * @return
     */
    lateinit var cells: Array<Array<Cell?>>
    var squaresPerSide = 8
    var currentPlayer: GameEnum
    var gameState: GameEnum? = null

    init {
        startNewGame(size)
        currentPlayer = GameEnum.RED
    }

    private fun startNewGame(size: Int) {
        gameState = GameEnum.PLAY
        squaresPerSide = size
        cells = genEmptyBoardState()
        initCheckers()
    }

    private fun getClickedCell(point: Point, squareWidth: Int): Cell {
        var cell = Cell(0, 0, Sprite.EMPTY)

        for (row in 0 until squaresPerSide) {
            for (col in 0 until squaresPerSide) {
                if (cells[col][row]?.hitTest(point.x, point.y, squareWidth) == true) {
                    cell = cells[col][row]!!
                    break
                }
            }
        }
        return cell
    }

    fun click(point: Point, squareWidth: Int): Boolean {
        val cell = getClickedCell(point, squareWidth)
        return click(cell.col, cell.row)
    }

    /**
     * Process click event
     *
     * @param row
     * @param col
     * @return
     */
    fun click(col: Int, row: Int): Boolean {

        if (!isValid(col, row) || gameState != GameEnum.PLAY) {
            return false
        }

        // clicked score square?
        if (at(col, row) === Sprite.SCORE) {
            val p = findPlayer()
            set(p!!.row, p.col, Sprite.EMPTY)
            set(
                p.row + (Math.round((row - p.row).toFloat()) / 2.0).toInt(),
                p.col + ((col - p.col) / 2.0).roundToInt(), Sprite.EMPTY
            )
            set(col, row, p.checkerPiece)
            upgrade(col, row)
            removeHints()
            currentPlayer = if (currentPlayer == GameEnum.BLACK) GameEnum.RED else GameEnum.BLACK
            return true
        }

        // clicked highlighted square?
        if (at(col, row) === Sprite.EMPTY_NEXT) {
            val p = findPlayer()
            set(p!!.col, p.row, Sprite.EMPTY)
            set(col, row, p.checkerPiece)
            upgrade(col, row)
            removeHints()
            currentPlayer = if (currentPlayer == GameEnum.BLACK) GameEnum.RED else GameEnum.BLACK
            return true
        }

        // clicked a checker?
        if (at(col, row) === Sprite.BLACK_CHECKER &&
            currentPlayer == GameEnum.BLACK
        ) {
            removeHints()
            set(col, row, Sprite.BLACK_CHECKER_H)
            renderHints(col, row)
        } else if (at(col, row) === Sprite.BLACK_CHECKER_H) {
            set(col, row, Sprite.BLACK_CHECKER)
            removeHints()
        } else if (at(col, row) === Sprite.RED_CHECKER &&
            currentPlayer == GameEnum.RED
        ) {
            removeHints()
            set(col, row, Sprite.RED_CHECKER_H)
            renderHints(col, row)
        } else if (at(col, row) === Sprite.RED_CHECKER_H) {
            set(col, row, Sprite.RED_CHECKER)
            removeHints()
        } else if (at(col, row) === Sprite.RED_CHECKER_S &&
            currentPlayer == GameEnum.RED
        ) {
            removeHints()
            set(col, row, Sprite.RED_CHECKER_S_H)
            renderHints(col, row)
        } else if (at(col, row) === Sprite.RED_CHECKER_S_H) {
            set(col, row, Sprite.RED_CHECKER_S)
            removeHints()
        } else if (at(col, row) === Sprite.BLACK_CHECKER_S &&
            currentPlayer == GameEnum.BLACK
        ) {
            removeHints()
            set(col, row, Sprite.BLACK_CHECKER_S_H)
            renderHints(col, row)
        } else if (at(col, row) === Sprite.BLACK_CHECKER_S_H) {
            set(col, row, Sprite.BLACK_CHECKER_S)
            removeHints()
        }
        return false
    }

    private fun isValid(col: Int, row: Int): Boolean {
        return row > -1 && row < squaresPerSide && col > -1 && col < squaresPerSide
    }

    /**
     * Returns square at a given
     *
     * @param row in the checker board
     * @param col in the checker board
     * @return Sprite
     */
    fun at(col: Int, row: Int): Sprite? {
        return if (isValid(col, row)) {
            cells[col][row]?.sprite
        } else {
            Sprite.INVALID
        }
    }

    private fun set(col: Int, row: Int, s: Sprite): Boolean {
        if (isValid(col, row)) {
            cells[col][row]?.sprite = s
            return true
        }
        return false
    }

    fun isEmpty(col: Int, row: Int): Boolean {
        return at(col, row) === Sprite.EMPTY
    }

    private fun upgrade(col: Int, row: Int) {
        if ((
            at(col, row) === Sprite.BLACK_CHECKER ||
                at(col, row) === Sprite.BLACK_CHECKER_H
            ) &&
            col == squaresPerSide - 1
        ) {
            set(col, row, Sprite.BLACK_CHECKER_S)
        }
        if ((
            at(col, row) === Sprite.RED_CHECKER ||
                at(col, row) === Sprite.RED_CHECKER_H
            ) &&
            col == 0
        ) {
            set(col, row, Sprite.RED_CHECKER_S)
        }
    }

    private fun renderHints(col: Int, row: Int) {
        // show potential moves for REGULAR CHECKER, here direction matters
        if (at(col, row) === Sprite.BLACK_CHECKER_H) {
            if (isEmpty(col + 1, row + 1)) {
                set(col + 1, row + 1, Sprite.EMPTY_NEXT)
            }
            if (isEmpty( col - 1, row + 1)) {
                set(col - 1, row + 1, Sprite.EMPTY_NEXT)
            }
        }
        if (at(col, row) == Sprite.RED_CHECKER_H) {
            if (isEmpty(col + 1, row - 1)) {
                set(col + 1, row - 1, Sprite.EMPTY_NEXT)
            }
            if (isEmpty(col - 1, row - 1)) {
                set(col - 1, row - 1, Sprite.EMPTY_NEXT)
            }
        }

        // show winning moves for REGULAR CHECKERS
        if (at(col, row) === Sprite.BLACK_CHECKER_H) {
            if (isEmpty(col + 2, row + 2) && at(col + 1, row + 1) === Sprite.RED_CHECKER ||
                at(col + 1, row + 1) === Sprite.RED_CHECKER_S
            ) {
                set(col + 2, row + 2, Sprite.SCORE)
            }
            if (isEmpty(col - 2, row + 2) && (
                at(col - 1, row + 1) === Sprite.RED_CHECKER ||
                    at(col - 1, row + 1) === Sprite.RED_CHECKER_S
                )
            ) {
                set(col - 2, row + 2, Sprite.SCORE)
            }
        }
        if (at(col, row) === Sprite.RED_CHECKER_H) {
            if (isEmpty(col + 2, row - 2) && (
                at(col + 1, row - 1) === Sprite.BLACK_CHECKER ||
                    at(col + 1, row - 1) === Sprite.BLACK_CHECKER_S
                )
            ) {
                set(col + 2, row - 2, Sprite.SCORE)
            }
            if (isEmpty(col - 2, row - 2) && (
                at(col - 1, row - 1) === Sprite.BLACK_CHECKER ||
                    at(col - 1, row - 1) === Sprite.BLACK_CHECKER_S
                )
            ) {
                set(col - 2, row - 2, Sprite.SCORE)
            }
        }

        // show potential moves for SUPER CHECKER, direction doesn't matter
        if (at(col, row) === Sprite.RED_CHECKER_S_H ||
            at(col, row) === Sprite.BLACK_CHECKER_S_H
        ) {
            if (isEmpty(col + 1, row - 1)) {
                set(col + 1, row - 1, Sprite.EMPTY_NEXT)
            }
            if (isEmpty(col - 1, row - 1)) {
                set(col - 1, row - 1, Sprite.EMPTY_NEXT)
            }
            if (isEmpty(col + 1, row + 1)) {
                set(col + 1, row + 1, Sprite.EMPTY_NEXT)
            }
            if (isEmpty(col - 1, row + 1)) {
                set(col - 1, row + 1, Sprite.EMPTY_NEXT)
            }
        }

        // show winning moves for SUPER CHECKERS
        if (at(col, row) === Sprite.BLACK_CHECKER_S_H) {
            if (isEmpty(col + 2, row + 2) && (
                at(col + 1, row + 1) === Sprite.RED_CHECKER ||
                    at(col + 1, row + 1) === Sprite.RED_CHECKER_S
                )
            ) {
                set(col + 2, row + 2, Sprite.SCORE)
            }
            if (isEmpty(col - 2, row + 2) && (
                at(col - 1, row + 1) === Sprite.RED_CHECKER ||
                    at(col - 1, row + 1) === Sprite.RED_CHECKER_S
                )
            ) {
                set(col - 2, row + 2, Sprite.SCORE)
            }
            if (isEmpty(col + 2, row - 2) && (
                at(col + 1, row - 1) === Sprite.RED_CHECKER ||
                    at(col + 1, row - 1) === Sprite.RED_CHECKER_S
                )
            ) {
                set(col + 2, row - 2, Sprite.SCORE)
            }
            if (isEmpty(col - 2, row - 2) && (
                at(col - 1, row - 1) === Sprite.RED_CHECKER ||
                    at(col - 1, row - 1) === Sprite.RED_CHECKER_S
                )
            ) {
                set(col - 2, row - 2, Sprite.SCORE)
            }
        }
        if (at(col, row) === Sprite.RED_CHECKER_S_H) {
            if (isEmpty(col + 2, row + 2) && (
                at(col + 1, row + 1) === Sprite.BLACK_CHECKER ||
                    at(col + 1, row + 1) === Sprite.BLACK_CHECKER_S
                )
            ) {
                set(col + 2, row + 2, Sprite.SCORE)
            }
            if (isEmpty(col - 2, row + 2) && (
                at(col - 1, row + 1) === Sprite.BLACK_CHECKER ||
                    at(row - 1, row + 1) === Sprite.BLACK_CHECKER_S
                )
            ) {
                set(col - 2, row + 2, Sprite.SCORE)
            }
            if (isEmpty(col + 2, row - 2) && (
                at(col+ 1, row - 1) === Sprite.BLACK_CHECKER ||
                    at(col + 1, row - 1) === Sprite.BLACK_CHECKER_S
                )
            ) {
                set(col + 2, row - 2, Sprite.SCORE)
            }
            if (isEmpty(col - 2, row - 2) && (
                at(col - 1, row - 1) === Sprite.BLACK_CHECKER ||
                    at(col - 1, row - 1) === Sprite.BLACK_CHECKER_S
                )
            ) {
                set(col - 2, row - 2, Sprite.SCORE)
            }
        }
    }

    private fun removeHints() {
        for (row in 0 until squaresPerSide) {
            for (col in 0 until squaresPerSide) {
                if (row % 2 == 1 && col % 2 == 0 || row % 2 != 1 && col % 2 != 0) {
                    if (at(col, row) === Sprite.EMPTY_NEXT ||
                        at(col, row) === Sprite.SCORE
                    ) {
                        set(col, row, Sprite.EMPTY)
                    }
                    if (at(col, row) === Sprite.BLACK_CHECKER_H) {
                        set(col, row, Sprite.BLACK_CHECKER)
                    }
                    if (at(col, row) === Sprite.RED_CHECKER_H) {
                        set(col, row, Sprite.RED_CHECKER)
                    }
                    if (at(col, row) === Sprite.BLACK_CHECKER_S_H) {
                        set(col, row, Sprite.BLACK_CHECKER_S)
                    }
                    if (at(col, row) === Sprite.RED_CHECKER_S_H) {
                        set(col, row, Sprite.RED_CHECKER_S)
                    }
                }
            }
        }
    }

    private fun findFirst(g: Sprite): CheckerPieces {
        val result = CheckerPieces()
        result.checkerPiece = g
        result.col = -1
        result.row = -1
        for (row in 0 until squaresPerSide) {
            for (col in 0 until squaresPerSide) {
                if (at(col, row) === g) {
                    result.col = col
                    result.row = row
                }
            }
        }
        return result
    }

    private fun findPlayer(): CheckerPieces? {
        for (
            g in arrayOf(
                Sprite.BLACK_CHECKER_H,
                Sprite.BLACK_CHECKER_S_H,
                Sprite.RED_CHECKER_H,
                Sprite.RED_CHECKER_S_H
            )
        ) {
            val p = findFirst(g)
            if (p.col != -1) {
                return p
            }
        }
        return null
    }

    /**
     * Build checker board pattern
     *
     * @return
     */
    private fun genEmptyBoardState(): Array<Array<Cell?>> {
        val board = Array(squaresPerSide) { arrayOfNulls<Cell>(squaresPerSide) }
        for (row in 0 until squaresPerSide) {
            for (col in 0 until squaresPerSide) {
                if (row % 2 == 1 && col % 2 == 0 || row % 2 != 1 && col % 2 != 0) {
                    board[col][row] = Cell(col, row, Sprite.EMPTY)
                } else {
                    board[col][row] = Cell(col, row, Sprite.INVALID)
                }
            }
        }
        return board
    }

    /**
     * Place checker pieces
     */
    private fun initCheckers() {
        for (row in 0 until squaresPerSide) {
            for (col in 0 until squaresPerSide) {
                if (row % 2 == 1 && col % 2 == 0 || row % 2 != 1 && col % 2 != 0) {
                    if (row < 3) {
                        cells[col][row]?.sprite = Sprite.BLACK_CHECKER
                    }
                    if (row > 4) {
                        cells[col][row]?.sprite = Sprite.RED_CHECKER
                    }
                }
            }
        }
    }

    /**
     * Is given cell a Red checker
     *
     * @param row
     * @param col
     * @return
     */
    fun isRed(col: Int, row: Int): Boolean {
        return at(col, row) === Sprite.RED_CHECKER || at(col, row) === Sprite.RED_CHECKER_H || at(
            col,
            row
        ) === Sprite.RED_CHECKER_S || at(col, row) === Sprite.RED_CHECKER_S_H
    }

    /**
     * Is given cell a Black checker
     *
     * @param row
     * @param col
     * @return
     */
    fun isBlack(col: Int, row: Int): Boolean {
        return at(col, row) === Sprite.BLACK_CHECKER || at(
            col,
            row
        ) === Sprite.BLACK_CHECKER_H || at(col, row) === Sprite.BLACK_CHECKER_S || at(
            col,
            row
        ) === Sprite.BLACK_CHECKER_S_H
    }

    /**
     * Returns side score
     *
     * @param side
     * @return
     */
    fun getScore(side: GameEnum): Int {
        var score = 12
        for (row in 0 until squaresPerSide) {
            for (col in 0 until squaresPerSide) {
                if (side == GameEnum.RED && isBlack(col, row)) {
                    score--
                } else if (side == GameEnum.BLACK && isRed(col, row)) {
                    score--
                }
            }
        }
        if (score > 11) {
            gameState = GameEnum.GAME_OVER
        }
        return score
    }
}
