package com.example.tetrixa

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages game settings persistence
 * Stores user preferences for grid size, game speed, and other options
 */
class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "tetris_settings",
        Context.MODE_PRIVATE
    )
    
    // Grid size keys
    private val KEY_GRID_WIDTH = "grid_width"
    private val KEY_GRID_HEIGHT = "grid_height"
    
    // Game speed keys
    private val KEY_GAME_SPEED = "game_speed" // 1-5 scale, 1=slowest, 5=fastest
    private val KEY_INITIAL_FALL_DELAY = "initial_fall_delay"
    
    // Default values
    private val DEFAULT_GRID_WIDTH = 10
    private val DEFAULT_GRID_HEIGHT = 20
    private val DEFAULT_GAME_SPEED = 2 // Slower default speed for better control
    private val DEFAULT_INITIAL_FALL_DELAY = 750L // Slower default fall speed
    
    // Grid size limits
    val MIN_GRID_WIDTH = 8
    val MAX_GRID_WIDTH = 12
    val MIN_GRID_HEIGHT = 16
    val MAX_GRID_HEIGHT = 24
    
    // Game speed limits
    val MIN_GAME_SPEED = 1
    val MAX_GAME_SPEED = 5
    
    var gridWidth: Int
        get() = prefs.getInt(KEY_GRID_WIDTH, DEFAULT_GRID_WIDTH)
            .coerceIn(MIN_GRID_WIDTH, MAX_GRID_WIDTH)
        set(value) {
            prefs.edit().putInt(KEY_GRID_WIDTH, value.coerceIn(MIN_GRID_WIDTH, MAX_GRID_WIDTH)).apply()
        }
    
    var gridHeight: Int
        get() = prefs.getInt(KEY_GRID_HEIGHT, DEFAULT_GRID_HEIGHT)
            .coerceIn(MIN_GRID_HEIGHT, MAX_GRID_HEIGHT)
        set(value) {
            prefs.edit().putInt(KEY_GRID_HEIGHT, value.coerceIn(MIN_GRID_HEIGHT, MAX_GRID_HEIGHT)).apply()
        }
    
    var gameSpeed: Int
        get() = prefs.getInt(KEY_GAME_SPEED, DEFAULT_GAME_SPEED)
            .coerceIn(MIN_GAME_SPEED, MAX_GAME_SPEED)
        set(value) {
            prefs.edit().putInt(KEY_GAME_SPEED, value.coerceIn(MIN_GAME_SPEED, MAX_GAME_SPEED)).apply()
        }
    
    /**
     * Get initial fall delay based on game speed setting
     * 
     * Speed tuning: These values control how long (in milliseconds) a tetromino
     * waits before falling one row. Higher values = slower fall speed.
     * 
     * Speed progression:
     * - Speed 1: Very slow, relaxed pace (1500ms) - comfortable for beginners
     * - Speed 2: Slow, controlled (1000ms) - good for learning
     * - Speed 3: Medium, balanced (700ms) - standard gameplay
     * - Speed 4: Fast, challenging (450ms) - requires quick thinking
     * - Speed 5: Very fast, expert (300ms) - maximum difficulty
     * 
     * These values are used directly by the game loop, ensuring the selected
     * speed setting directly controls gameplay pace.
     */
    fun getInitialFallDelay(): Long {
        return when (gameSpeed) {
            1 -> 1500L  // Very slow - relaxed pace, comfortable for beginners
            2 -> 1000L  // Slow - controlled speed, good for learning
            3 -> 700L   // Medium - balanced difficulty, standard gameplay
            4 -> 450L   // Fast - challenging, requires quick thinking
            5 -> 300L   // Very fast - expert level, maximum difficulty
            else -> 1000L // Default to slow speed
        }
    }
}

