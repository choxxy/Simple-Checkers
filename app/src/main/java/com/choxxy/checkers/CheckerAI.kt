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

import java.util.*

/**
 * Checkers AI engine
 */
class CheckerAI {
    /**
     * Very dump AI, randomly picks a checker and randomly moves it
     *
     * @param e current game which contains current game state
     * @param isRed flag for red or black side
     */
    fun randomMove(e: GameEngine, isRed: Boolean) {
        val rn = Random()

        // click random checker that can move
        val playPieces = playablePieces(e, isRed)
        if (playPieces.size < 1) {
            return
        }
        val piece = playPieces[rn.nextInt(playPieces.size)]
        e.click(piece[0], piece[1])

        // click random next move
        val nextSteps = nextStep(e)
        if (nextSteps.size < 1) {
            return
        }
        val next = nextSteps[rn.nextInt(nextSteps.size)]
        e.click(next[0], next[1])
    }

    /**
     * Returns all currently highlighted next possible steps on the board
     *
     * @param e current game which contains current game state
     * @return
     */
    fun nextStep(e: GameEngine): LinkedList<IntArray> {
        val result = LinkedList<IntArray>()
        for (row in 0 until e.squaresPerSide) {
            for (col in 0 until e.squaresPerSide) {
                if (e.at(row, col) === Sprite.EMPTY_NEXT ||
                    e.at(row, col) === Sprite.SCORE
                ) {
                    result.add(intArrayOf(row, col))
                }
            }
        }
        return result
    }

    /**
     * Find all pieces that can move
     *
     * @param e current game which contains current game state
     * @param isRed flag for red or black side
     * @return
     */
    fun playablePieces(e: GameEngine, isRed: Boolean): LinkedList<IntArray> {
        val result = LinkedList<IntArray>()
        for (row in 0 until e.squaresPerSide) {
            for (col in 0 until e.squaresPerSide) {
                if (isRed && e.isRed(col, row) &&
                    validRedMoves(e, col, row)
                ) {
                    result.add(intArrayOf(col, row))
                }
                if (!isRed && e.isBlack(col, row) &&
                    validBlackMoves(e, col, row)
                ) {
                    result.add(intArrayOf(col, row))
                }
            }
        }
        return result
    }

    /**
     * Return true if a black checker at row, col has possible moves
     *
     * @param e current game which contains current game state
     * @param row
     * @param col
     * @return
     */
    private fun validBlackMoves(e: GameEngine, row: Int, col: Int): Boolean {
        // check possible moves to empty square
        if (e.isEmpty(col - 1, row + 1)) {
            return true
        }
        if (e.isEmpty(col + 1, row + 1)) {
            return true
        }
        if (e.at(col, row) === Sprite.BLACK_CHECKER_S) {
            if (e.isEmpty(col - 1, row - 1)) {
                return true
            }
            if (e.isEmpty(col + 1, row - 1)) {
                return true
            }
        }

        // check jump moves
        if (e.isRed(col - 1, row + 1) && e.isEmpty(col - 2, row + 2)) {
            return true
        }
        if (e.isRed(col + 1, row + 1) && e.isEmpty(col + 2, row + 2)) {
            return true
        }
        if (e.at(col, row) === Sprite.BLACK_CHECKER_S) {
            if (e.isRed(col - 1, row - 1) && e.isEmpty(col - 2, row - 2)) {
                return true
            }
            if (e.isRed(col + 1, row - 1) && e.isEmpty(col + 2, row - 2)) {
                return true
            }
        }
        return false
    }

    /**
     * Return true if a red checker at row, col has possible moves
     *
     * @param e current game which contains current game state
     * @param row
     * @param col
     * @return
     */
    private fun validRedMoves(e: GameEngine, row: Int, col: Int): Boolean {
        // check possible moves to empty square
        if (e.isEmpty(row - 1, col - 1)) {
            return true
        }
        if (e.isEmpty(row + 1, col - 1)) {
            return true
        }
        if (e.at(row, col) === Sprite.RED_CHECKER_S) {
            if (e.isEmpty(row - 1, col + 1)) {
                return true
            }
            if (e.isEmpty(row + 1, col + 1)) {
                return true
            }
        }

        // check jump moves
        if (e.isBlack(row - 1, col - 1) && e.isEmpty(row - 2, col - 2)) {
            return true
        }
        if (e.isBlack(row + 1, col - 1) && e.isEmpty(row + 2, col - 2)) {
            return true
        }
        if (e.at(row, col) === Sprite.RED_CHECKER_S) {
            if (e.isBlack(row - 1, col - 1) && e.isEmpty(row - 2, col - 2)) {
                return true
            }
            if (e.isBlack(row + 1, col - 1) && e.isEmpty(row + 2, col - 2)) {
                return true
            }
        }
        return false
    }
}
