package com.tetrixa

import android.os.Handler
import android.os.Looper
import kotlin.math.min

/**
 * Core Tetris game logic
 */
class TetrisGame(
    private val gridWidth: Int = 10,
    private val gridHeight: Int = 20,
    private val initialFallDelay: Long = 500L,
    private val onGameStateChanged: (GameState) -> Unit = {},
    private val onScoreChanged: (Int, Int) -> Unit = { _, _ -> } // score, level
) {
    private val handler = Handler(Looper.getMainLooper())
    private var fallRunnable: Runnable? = null
    private var gameGrid = Array(gridHeight) { IntArray(gridWidth) }
    private var currentTetromino: Tetromino? = null
    private var currentX = 0
    private var currentY = 0
    private var score = 0
    private var level = 1
    private var gameState: GameState = GameState.GAME_OVER
    private var gameMode: GameMode = GameMode.CLASSIC
    
    private val SCORE_PER_LEVEL = 1000
    // Store initial fall delay - can be updated when settings change
    // This value comes directly from SettingsManager.getInitialFallDelay()
    // and directly controls the base fall speed
    private var INITIAL_FALL_DELAY_MS = initialFallDelay
    // Minimum fall delay: prevents game from becoming unplayably fast at high levels
    // Set to 100ms to allow for very fast gameplay at expert levels
    private val MIN_FALL_DELAY_MS = 100L
    // Speed increase per level: how much faster the game gets each level
    // Reduced to 30ms for gentler progression, keeping early gameplay calm
    private val SPEED_INCREASE_PER_LEVEL = 30L
    
    /**
     * Update the initial fall delay when speed settings change
     * This allows the game to respond to speed changes without restarting
     */
    fun updateInitialFallDelay(newDelay: Long) {
        INITIAL_FALL_DELAY_MS = newDelay
        // If game is running, restart the game loop with new delay
        if (gameState == GameState.RUNNING) {
            stopGameLoop()
            startGameLoop()
        }
    }
    
    fun getGameState() = gameState
    fun getScore() = score
    fun getLevel() = level
    fun getGrid() = gameGrid
    fun getCurrentTetromino() = currentTetromino
    fun getCurrentX() = currentX
    fun getCurrentY() = currentY
    fun getGridWidth() = gridWidth
    fun getGridHeight() = gridHeight
    
    fun setGameMode(mode: GameMode) {
        gameMode = mode
    }
    
    /**
     * Start a new game with current settings
     * Uses the current INITIAL_FALL_DELAY_MS which reflects speed settings
     */
    fun startNewGame() {
        gameGrid = Array(gridHeight) { IntArray(gridWidth) }
        score = 0
        level = 1
        gameState = GameState.RUNNING
        spawnNewTetromino()
        startGameLoop() // Game loop uses calculateFallDelay() which reads INITIAL_FALL_DELAY_MS
        onGameStateChanged(gameState)
        onScoreChanged(score, level)
    }
    
    fun pause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSED
            stopGameLoop()
            onGameStateChanged(gameState)
        }
    }
    
    fun resume() {
        if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING
            startGameLoop()
            onGameStateChanged(gameState)
        }
    }
    
    fun moveLeft() {
        if (gameState != GameState.RUNNING) return
        if (checkCollision(currentX - 1, currentY, currentTetromino?.shape)) return
        currentX--
    }
    
    fun moveRight() {
        if (gameState != GameState.RUNNING) return
        if (checkCollision(currentX + 1, currentY, currentTetromino?.shape)) return
        currentX++
    }
    
    fun rotate() {
        if (gameState != GameState.RUNNING) return
        val rotated = currentTetromino?.rotate() ?: return
        if (!checkCollision(currentX, currentY, rotated.shape)) {
            currentTetromino = rotated
        }
    }
    
    fun softDrop() {
        if (gameState != GameState.RUNNING) return
        moveDown()
    }
    
    fun hardDrop() {
        if (gameState != GameState.RUNNING) return
        while (!checkCollision(currentX, currentY + 1, currentTetromino?.shape)) {
            currentY++
        }
        lockTetromino()
    }
    
    fun calculateGhostY(): Int {
        var ghostY = currentY
        while (!checkCollision(currentX, ghostY + 1, currentTetromino?.shape)) {
            ghostY++
        }
        return ghostY
    }
    
    private fun moveDown() {
        if (checkCollision(currentX, currentY + 1, currentTetromino?.shape)) {
            lockTetromino()
        } else {
            currentY++
        }
    }
    
    private fun checkCollision(x: Int, y: Int, shape: Array<IntArray>?): Boolean {
        if (shape == null) return true
        
        for (i in shape.indices) {
            for (j in shape[i].indices) {
                if (shape[i][j] != 0) {
                    val gridX = x + j
                    val gridY = y + i
                    
                    if (gridX < 0 || gridX >= gridWidth || gridY >= gridHeight) {
                        return true
                    }
                    
                    if (gridY >= 0 && gameGrid[gridY][gridX] != 0) {
                        return true
                    }
                }
            }
        }
        return false
    }
    
    private fun lockTetromino() {
        currentTetromino?.let { tetromino ->
            val shape = tetromino.shape
            for (i in shape.indices) {
                for (j in shape[i].indices) {
                    if (shape[i][j] != 0) {
                        val gridX = currentX + j
                        val gridY = currentY + i
                        if (gridY >= 0 && gridX >= 0 && gridX < gridWidth && gridY < gridHeight) {
                            gameGrid[gridY][gridX] = tetromino.color
                        }
                    }
                }
            }
        }
        
        clearLines()
        spawnNewTetromino()
    }
    
    private fun clearLines() {
        val linesToClear = mutableListOf<Int>()
        
        for (row in gridHeight - 1 downTo 0) {
            var isFull = true
            for (col in 0 until gridWidth) {
                if (gameGrid[row][col] == 0) {
                    isFull = false
                    break
                }
            }
            if (isFull) {
                linesToClear.add(row)
            }
        }
        
        if (linesToClear.isNotEmpty()) {
            linesToClear.sortedDescending().forEach { row ->
                for (r in row downTo 1) {
                    for (col in 0 until gridWidth) {
                        gameGrid[r][col] = gameGrid[r - 1][col]
                    }
                }
                for (col in 0 until gridWidth) {
                    gameGrid[0][col] = 0
                }
            }
            
            val linesCleared = linesToClear.size
            val scoreGain = linesCleared * 100 * level
            score += scoreGain
            level = (score / SCORE_PER_LEVEL) + 1
            onScoreChanged(score, level)
        }
    }
    
    private fun spawnNewTetromino() {
        val tetromino = Tetromino.getRandom()
        currentTetromino = tetromino
        currentX = (gridWidth - tetromino.getWidth()) / 2
        currentY = 0
        
        if (checkCollision(currentX, currentY, tetromino.shape)) {
            gameState = GameState.GAME_OVER
            stopGameLoop()
            onGameStateChanged(gameState)
        }
    }
    
    private fun startGameLoop() {
        stopGameLoop()
        fallRunnable = object : Runnable {
            override fun run() {
                if (gameState == GameState.RUNNING) {
                    moveDown()
                    handler.postDelayed(this, calculateFallDelay())
                }
            }
        }
        handler.post(fallRunnable!!)
    }
    
    private fun stopGameLoop() {
        fallRunnable?.let {
            handler.removeCallbacks(it)
            fallRunnable = null
        }
    }
    
    /**
     * Calculate fall delay based on level and current speed settings
     * 
     * Speed setting flow:
     * 1. User changes speed in Settings -> SettingsManager.gameSpeed updated
     * 2. SettingsManager.getInitialFallDelay() returns delay based on gameSpeed
     * 3. GameScreen calls game.updateInitialFallDelay() with new delay
     * 4. This method uses INITIAL_FALL_DELAY_MS (updated from settings)
     * 5. Game loop calls this method each frame to get current fall delay
     * 
     * Speed tuning: The base delay comes directly from settings, ensuring the
     * selected speed directly controls gameplay. Level progression gradually
     * increases speed, but early levels remain calm and controlled.
     */
    private fun calculateFallDelay(): Long {
        // Use initial fall delay from settings (reflects current speed setting)
        // This value comes directly from SettingsManager and is the base speed
        val baseDelay = INITIAL_FALL_DELAY_MS
        // Gradually reduce delay per level for progressive difficulty
        // Early levels stay close to base speed for calm gameplay
        val delay = baseDelay - (level - 1) * SPEED_INCREASE_PER_LEVEL
        // Ensure minimum delay is maintained for playability at high levels
        return delay.coerceAtLeast(MIN_FALL_DELAY_MS)
    }
    
    fun isGameOver(): Boolean = gameState == GameState.GAME_OVER
}

