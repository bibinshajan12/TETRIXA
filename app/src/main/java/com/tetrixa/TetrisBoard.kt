package com.tetrixa

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import com.tetrixa.ui.theme.*
import kotlin.math.min
import kotlin.math.abs
import kotlinx.coroutines.launch

@Composable
fun TetrisBoard(
    game: TetrisGame,
    modifier: Modifier = Modifier,
    onScoreChanged: (Int, Int) -> Unit = { _, _ -> },
    onGameStateChanged: (GameState) -> Unit = {}
) {
    // Get grid dimensions from game
    val gridWidth = game.getGridWidth()
    val gridHeight = game.getGridHeight()
    // Use state to force recomposition
    var updateTrigger by remember { mutableStateOf(0) }
    
    // Force update periodically
    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            updateTrigger++
        }
    }
    
    // Read game state (will recompose when updateTrigger changes)
    val grid = remember(updateTrigger) { game.getGrid() }
    val currentTetromino = remember(updateTrigger) { game.getCurrentTetromino() }
    val currentX = remember(updateTrigger) { game.getCurrentX() }
    val currentY = remember(updateTrigger) { game.getCurrentY() }
    val gameState = remember(updateTrigger) { game.getGameState() }
    
    // Dark theme colors for gameplay rendering
    // Visual darkening: Reduced brightness/saturation for grid lines and ghost piece
    // while maintaining visibility and contrast for active blocks
    val GridLineColor = Color.White.copy(alpha = 0.1f)  // Subtle grid lines on dark background
    val GhostPieceColor = CandyBlue.copy(alpha = 0.3f)   // Visible but understated ghost piece
    
    // Drag-based control system state
    // Tracks touch position and movement for fluid horizontal dragging
    var isDragging by remember { mutableStateOf(false) }
    var dragStartX by remember { mutableStateOf(0f) }
    var lastColumnX by remember { mutableStateOf(0f) }  // Last column position in pixels
    var isDraggingDown by remember { mutableStateOf(false) }  // Track downward drag for soft drop
    var dragStartY by remember { mutableStateOf(0f) }
    
    // Track canvas size for cell size calculation in pointer input
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }
    
    // Visual feedback: highlight active piece while dragging
    var dragHighlightAlpha by remember { mutableStateOf(0f) }
    
    // Animate drag highlight fade out when dragging stops
    LaunchedEffect(isDragging) {
        if (!isDragging) {
            while (dragHighlightAlpha > 0f) {
                dragHighlightAlpha = (dragHighlightAlpha - 0.15f).coerceAtLeast(0f)
                delay(16) // ~60fps animation
            }
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(gameState, gridWidth, gridHeight, canvasWidth, canvasHeight) {
                // Drag-based control system using pointer events
                // Handles ACTION_DOWN, ACTION_MOVE, ACTION_UP equivalent events
                // Input handling is separate from game logic for clean architecture
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        
                        // Only process input when game is running
                        // Prevents drag input when game is paused
                        if (gameState != GameState.RUNNING) {
                            continue
                        }
                        
                        // Handle single pointer events (ignore multi-touch)
                        // Only the active tetromino responds to drag input
                        if (event.changes.size == 1) {
                            val change = event.changes[0]
                            val position = change.position
                            
                            when {
                                // ACTION_DOWN equivalent: Touch started
                                change.pressed && !isDragging -> {
                                    isDragging = true
                                    dragStartX = position.x
                                    dragStartY = position.y
                                    lastColumnX = position.x
                                    isDraggingDown = false
                                    // Visual feedback: start highlight animation
                                    dragHighlightAlpha = 0.3f
                                }
                                
                                // ACTION_MOVE equivalent: Finger moving
                                change.pressed && isDragging -> {
                                    val deltaX = position.x - dragStartX
                                    val deltaY = position.y - dragStartY
                                    val absDeltaX = abs(deltaX)
                                    val absDeltaY = abs(deltaY)
                                    
                                    // Calculate cell size for column snapping
                                    // This ensures movement snaps to grid columns, not free pixel movement
                                    // Cell size is calculated from canvas width and grid width
                                    val cellSize = if (canvasWidth > 0f) {
                                        canvasWidth / gridWidth
                                    } else {
                                        continue // Wait for size to be measured
                                    }
                                    
                                    // Drag threshold calculation: Move piece when crossing column boundary
                                    // Small finger movements move piece one column at a time
                                    // Snapping logic: Only move when crossing half cell width threshold
                                    val currentColumnX = position.x
                                    val columnDelta = currentColumnX - lastColumnX
                                    
                                    // Horizontal movement: Snap to grid columns
                                    // Movement must snap to grid columns (no free pixel movement)
                                    // Vertical dragging should NOT freely move the piece upward
                                    if (absDeltaX > absDeltaY && abs(columnDelta) > cellSize * 0.5f) {
                                        // Calculate how many columns to move based on pixel delta
                                        // Convert pixel delta to column shifts using block width
                                        val columnsToMove = (columnDelta / cellSize).toInt()
                                        
                                        if (columnsToMove != 0) {
                                            // Move piece column by column, respecting collision bounds
                                            // Clamp movement to grid bounds and collision rules at all times
                                            // Prevent the piece from passing through existing blocks
                                            repeat(abs(columnsToMove)) {
                                                if (columnsToMove > 0) {
                                                    game.moveRight()  // Collision checked inside
                                                } else {
                                                    game.moveLeft()   // Collision checked inside
                                                }
                                            }
                                            // Update last column position for next threshold calculation
                                            // This prevents multiple moves for the same drag distance
                                            // Do NOT move the piece every frame—only when crossing a column threshold
                                            lastColumnX = position.x
                                        }
                                    }
                                    
                                    // Optional: slight downward drag can trigger a soft drop
                                    // Only trigger if downward movement is significant (0.8 cell size)
                                    if (absDeltaY > cellSize * 0.8f && deltaY > 0 && !isDraggingDown) {
                                        isDraggingDown = true
                                        game.softDrop()
                                    }
                                }
                                
                                // ACTION_UP equivalent: Touch released
                                !change.pressed && isDragging -> {
                                    val deltaX = position.x - dragStartX
                                    val deltaY = position.y - dragStartY
                                    val totalDistance = kotlin.math.sqrt(deltaX * deltaX + deltaY * deltaY)
                                    
                                    // Single tap detection: Small movement = rotation
                                    // Single tap anywhere on the gameplay area rotates the active tetromino
                                    // Drag gestures must not trigger rotation
                                    val TAP_THRESHOLD = 20f // Pixels - very small movement = tap
                                    if (totalDistance < TAP_THRESHOLD && !isDraggingDown) {
                                        game.rotate()
                                    }
                                    
                                    // Reset drag state
                                    isDragging = false
                                    isDraggingDown = false
                                    dragHighlightAlpha = 0f
                                }
                            }
                        }
                    }
                }
            }
            .onSizeChanged { measuredSize ->
                // Canvas size tracking: The measuredSize reflects the actual available space
                // after system insets are applied at the layout level.
                // The Canvas uses size.width and size.height which come from this measured size,
                // ensuring the game board respects system bars and doesn't draw under them.
                // Store size for drag threshold calculation in pointer input
                canvasWidth = measuredSize.width.toFloat()
                canvasHeight = measuredSize.height.toFloat()
            }
    ) {
        // Use grid dimensions from game (supports configurable grid sizes)
        val gameGridWidth = gridWidth
        val gameGridHeight = gridHeight
        
        // Calculate cell size to fit the available space properly
        // size.width and size.height come from the Canvas's actual measured dimensions
        // which already account for system insets applied at the layout level
        val availableWidth = size.width
        val availableHeight = size.height
        val cellSize = min(availableWidth / gameGridWidth, availableHeight / gameGridHeight)
        
        val boardWidth = cellSize * gameGridWidth
        val boardHeight = cellSize * gameGridHeight
        val offsetX = (availableWidth - boardWidth) / 2
        val offsetY = (availableHeight - boardHeight) / 2
        val padding = cellSize * 0.08f // Slightly reduced padding for better visibility
        
        // Draw background with dark gradient for gameplay visual comfort
        // Visual darkening: Darker game board background reduces brightness
        // while maintaining good contrast for tetromino blocks
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    GameBoardDark,        // Very dark base
                    GameBgDarkSecondary   // Slightly lighter for depth
                )
            ),
            topLeft = Offset(offsetX, offsetY),
            size = Size(boardWidth, boardHeight)
        )
        
        // Draw grid lines
        for (row in 0..gameGridHeight) {
            val y = offsetY + row * cellSize
            drawLine(
                color = GridLineColor,
                start = Offset(offsetX, y),
                end = Offset(offsetX + boardWidth, y),
                strokeWidth = 1f
            )
        }
        for (col in 0..gameGridWidth) {
            val x = offsetX + col * cellSize
            drawLine(
                color = GridLineColor,
                start = Offset(x, offsetY),
                end = Offset(x, offsetY + boardHeight),
                strokeWidth = 1f
            )
        }
        
        // Draw locked blocks
        for (row in 0 until gameGridHeight) {
            for (col in 0 until gameGridWidth) {
                if (grid[row][col] != 0) {
                    val x = offsetX + col * cellSize + padding
                    val y = offsetY + row * cellSize + padding
                    val cellSizeWithPadding = cellSize - padding * 2
                    
                    drawBlock(
                        color = Color(grid[row][col]),
                        topLeft = Offset(x, y),
                        size = cellSizeWithPadding
                    )
                }
            }
        }
        
        // Draw ghost piece
        // Visual darkening: Ghost piece uses subtle color for dark theme
        if (gameState == GameState.RUNNING && currentTetromino != null) {
            val ghostY = game.calculateGhostY()
            if (ghostY != currentY) {
                drawTetromino(
                    tetromino = currentTetromino,
                    x = currentX,
                    y = ghostY,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    cellSize = cellSize,
                    padding = padding,
                    isGhost = true,
                    gridWidth = gameGridWidth,
                    ghostColor = GhostPieceColor
                )
            }
        }
        
        // Draw current tetromino with drag highlight feedback
        // Visual feedback: Subtle glow/highlight while dragging for better UX
        if (gameState == GameState.RUNNING && currentTetromino != null) {
            // Draw highlight glow if dragging
            if (isDragging && dragHighlightAlpha > 0f) {
                drawTetromino(
                    tetromino = currentTetromino,
                    x = currentX,
                    y = currentY,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    cellSize = cellSize,
                    padding = padding,
                    isGhost = false,
                    gridWidth = gameGridWidth,
                    ghostColor = GhostPieceColor,
                    highlightAlpha = dragHighlightAlpha
                )
            } else {
                drawTetromino(
                    tetromino = currentTetromino,
                    x = currentX,
                    y = currentY,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    cellSize = cellSize,
                    padding = padding,
                    isGhost = false,
                    gridWidth = gameGridWidth,
                    ghostColor = GhostPieceColor,
                    highlightAlpha = 0f
                )
            }
        }
        
        // Draw pause overlay
        if (gameState == GameState.PAUSED) {
            drawRect(
                color = Color.Black.copy(alpha = 0.7f),
                topLeft = Offset(offsetX, offsetY),
                size = Size(boardWidth, boardHeight)
            )
        }
    }
}

/**
 * Draw block with glossy, candy-like effect
 * Features rounded corners, soft gradients, and shine highlights
 * Creates a juicy, playful appearance like Candy Crush blocks
 */
private fun DrawScope.drawBlock(
    color: Color,
    topLeft: Offset,
    size: Float
) {
    val cornerRadius = size * 0.2f // More rounded for candy look
    
    // Soft shadow for depth
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.1f),
        topLeft = Offset(topLeft.x + 2f, topLeft.y + 2f),
        size = Size(size, size),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
    
    // Main block with gradient for glossy effect
    val mainGradient = Brush.verticalGradient(
        colors = listOf(
            color.copy(alpha = 1f),      // Full color at top
            color.copy(alpha = 0.9f),     // Slightly darker at bottom
            color.copy(alpha = 0.85f)     // Even darker for depth
        )
    )
    drawRoundRect(
        brush = mainGradient,
        topLeft = topLeft,
        size = Size(size, size),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
    
    // Top shine highlight - glossy candy effect
    val highlightSize = size * 0.5f
    val shineGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.6f),  // Strong shine at top
            Color.White.copy(alpha = 0.2f),  // Medium in middle
            Color.Transparent                // Fade to transparent
        )
    )
    drawRoundRect(
        brush = shineGradient,
        topLeft = topLeft,
        size = Size(size, highlightSize),
        cornerRadius = CornerRadius(cornerRadius * 0.8f, cornerRadius * 0.8f)
    )
    
    // Left edge shine - side highlight
    val edgeWidth = size * 0.2f
    val edgeGradient = Brush.horizontalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.1f),
            Color.Transparent
        )
    )
    drawRoundRect(
        brush = edgeGradient,
        topLeft = topLeft,
        size = Size(edgeWidth, size),
        cornerRadius = CornerRadius(cornerRadius * 0.8f, cornerRadius * 0.8f)
    )
    
    // Center highlight - inner glow
    val centerSize = size * 0.35f
    val centerOffset = (size - centerSize) / 2
    drawRoundRect(
        color = Color.White.copy(alpha = 0.3f),
        topLeft = Offset(topLeft.x + centerOffset, topLeft.y + centerOffset),
        size = Size(centerSize, centerSize),
        cornerRadius = CornerRadius(centerSize * 0.3f, centerSize * 0.3f)
    )
    
    // Soft border for definition
    drawRoundRect(
        color = color.copy(alpha = 0.4f),
        topLeft = topLeft,
        size = Size(size, size),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawTetromino(
    tetromino: Tetromino,
    x: Int,
    y: Int,
    offsetX: Float,
    offsetY: Float,
    cellSize: Float,
    padding: Float,
    isGhost: Boolean,
    gridWidth: Int = 10, // Default for compatibility
    ghostColor: Color = CandyBlue.copy(alpha = 0.3f), // Dark theme ghost color
    highlightAlpha: Float = 0f // Visual feedback alpha for drag highlight
) {
    val shape = tetromino.shape
    val color = if (isGhost) {
        ghostColor
    } else {
        Color(tetromino.color)
    }
    
    // Visual feedback: Draw subtle glow around active piece while dragging
    if (!isGhost && highlightAlpha > 0f) {
        val glowRadius = cellSize * 0.15f
        for (i in shape.indices) {
            for (j in shape[i].indices) {
                if (shape[i][j] != 0) {
                    val gridX = x + j
                    val gridY = y + i
                    if (gridY >= 0 && gridX >= 0 && gridX < gridWidth) {
                        val blockX = offsetX + gridX * cellSize + padding
                        val blockY = offsetY + gridY * cellSize + padding
                        val blockSize = cellSize - padding * 2
                        
                        // Draw glow effect
                        drawRoundRect(
                            color = CandyBlue.copy(alpha = highlightAlpha * 0.5f),
                            topLeft = Offset(blockX - glowRadius, blockY - glowRadius),
                            size = Size(blockSize + glowRadius * 2, blockSize + glowRadius * 2),
                            cornerRadius = CornerRadius((blockSize + glowRadius * 2) * 0.2f, (blockSize + glowRadius * 2) * 0.2f)
                        )
                    }
                }
            }
        }
    }
    
    for (i in shape.indices) {
        for (j in shape[i].indices) {
            if (shape[i][j] != 0) {
                val gridX = x + j
                val gridY = y + i
                
                if (gridY >= 0 && gridX >= 0 && gridX < gridWidth) {
                    val blockX = offsetX + gridX * cellSize + padding
                    val blockY = offsetY + gridY * cellSize + padding
                    val blockSize = cellSize - padding * 2
                    
                    if (isGhost) {
                        // Draw ghost as outline
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(blockX, blockY),
                            size = Size(blockSize, blockSize),
                            cornerRadius = CornerRadius(8f, 8f),
                            style = Stroke(width = 2f)
                        )
                    } else {
                        drawBlock(
                            color = color,
                            topLeft = Offset(blockX, blockY),
                            size = blockSize
                        )
                    }
                }
            }
        }
    }
}

// Color constants for Compose
// Grid line and ghost piece colors are now defined inline in TetrisBoard
// to use dark theme colors that match the gameplay visual darkening

