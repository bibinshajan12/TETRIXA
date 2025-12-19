package com.tetrixa

import android.graphics.Color
import com.tetrixa.ui.theme.*

/**
 * Represents a Tetromino piece with its shape and color
 */
data class Tetromino(
    val shape: Array<IntArray>,
    val color: Int
) {
    fun getWidth(): Int = shape[0].size
    fun getHeight(): Int = shape.size

    /**
     * Rotate the tetromino 90 degrees clockwise
     */
    fun rotate(): Tetromino {
        val rows = shape.size
        val cols = shape[0].size
        val rotated = Array(cols) { IntArray(rows) }
        
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                rotated[j][rows - 1 - i] = shape[i][j]
            }
        }
        
        return Tetromino(rotated, color)
    }

    companion object {
        /**
         * Create all tetromino shapes with neon colors
         */
        fun createAll(): List<Tetromino> {
            return listOf(
                // I shape (cyan)
                Tetromino(
                    arrayOf(
                        intArrayOf(1, 1, 1, 1)
                    ),
                    Color.rgb(107, 206, 255) // CandyCyan
                ),
                // O shape (yellow)
                Tetromino(
                    arrayOf(
                        intArrayOf(1, 1),
                        intArrayOf(1, 1)
                    ),
                    Color.rgb(255, 230, 109) // CandyYellow
                ),
                // T shape (pink)
                Tetromino(
                    arrayOf(
                        intArrayOf(0, 1, 0),
                        intArrayOf(1, 1, 1)
                    ),
                    Color.rgb(255, 159, 214) // CandyPink
                ),
                // L shape (orange)
                Tetromino(
                    arrayOf(
                        intArrayOf(0, 0, 1),
                        intArrayOf(1, 1, 1)
                    ),
                    Color.rgb(255, 179, 71) // CandyOrange
                )
            )
        }

        /**
         * Get a random tetromino
         */
        fun getRandom(): Tetromino {
            val all = createAll()
            return all[(all.indices).random()]
        }
    }
}

