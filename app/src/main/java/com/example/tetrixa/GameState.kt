package com.example.tetrixa

/**
 * Represents the current game state
 */
enum class GameState {
    RUNNING,    // Game is actively playing
    PAUSED,     // Game is paused, can be resumed
    GAME_OVER   // Game has ended
}

/**
 * Represents different game modes
 */
enum class GameMode(val displayName: String, val fallDelay: Long, val hasGameOver: Boolean) {
    CLASSIC("Classic", 500L, true),
    ZEN("Zen", 800L, false),
    CHALLENGE("Challenge", 300L, true)
}

