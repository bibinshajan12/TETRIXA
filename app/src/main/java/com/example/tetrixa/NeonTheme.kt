package com.example.tetrixa

import android.graphics.Color

/**
 * Neon theme color palette and constants
 * Provides reusable neon colors for consistent theming across the app
 * All colors are defined here to avoid hardcoding throughout the codebase
 */
object NeonTheme {
    // ========== NEON COLOR PALETTE ==========
    val NEON_CYAN = 0xFF00FFFF.toInt()
    val NEON_PURPLE = 0xFF9D00FF.toInt()
    val NEON_PINK = 0xFFFF00FF.toInt()
    val NEON_BLUE = 0xFF0080FF.toInt()
    val NEON_GREEN = 0xFF00FF80.toInt()
    val NEON_YELLOW = 0xFFFFFF00.toInt()
    val NEON_ORANGE = 0xFFFF8000.toInt()
    val NEON_RED = 0xFFFF0040.toInt()
    
    // ========== DARK BACKGROUND COLORS ==========
    // Dark backgrounds with subtle blue tint for depth
    val DARK_BG_PRIMARY = 0xFF0A0A15.toInt()      // Very dark blue-black (main background)
    val DARK_BG_SECONDARY = 0xFF151520.toInt()    // Slightly lighter (cards, panels)
    val DARK_BG_TERTIARY = 0xFF1A1A2E.toInt()      // Dark blue-gray (elevated surfaces)
    val DARK_BG_QUATERNARY = 0xFF202035.toInt()   // Lighter for contrast
    
    // ========== TEXT COLORS ==========
    val TEXT_PRIMARY = 0xFFFFFFFF.toInt()         // Pure white for main text
    val TEXT_SECONDARY = 0xFFCCCCCC.toInt()       // Light gray for secondary text
    val TEXT_NEON = NEON_CYAN                     // Neon cyan for highlights
    val TEXT_DISABLED = 0xFF666666.toInt()        // Gray for disabled text
    
    // ========== UI ELEMENT COLORS ==========
    val GRID_LINE = 0x3300FFFF.toInt()            // Low opacity cyan grid lines
    val GHOST_PIECE = 0x8000FFFF.toInt()          // Semi-transparent cyan for ghost piece
    val BUTTON_GLOW = NEON_CYAN                   // Neon cyan for button glow
    val BUTTON_BG = NEON_CYAN                     // Button background
    val BUTTON_TEXT = 0xFF000000.toInt()          // Black text on neon buttons
    
    // ========== GLOW EFFECT SETTINGS ==========
    const val GLOW_BLUR_RADIUS = 15f              // Standard blur radius for glow
    const val GLOW_INTENSITY = 0.6f               // Glow intensity multiplier
    const val GLOW_ALPHA = 180                    // Alpha for glow effects (0-255)
    const val GLOW_BLUR_RADIUS_LARGE = 30f        // Large glow for backgrounds
    const val GLOW_BLUR_RADIUS_SMALL = 8f         // Small glow for details
    
    // ========== ANIMATION SETTINGS ==========
    const val GLOW_ANIMATION_DURATION = 3000L     // Background glow pulse duration (ms)
    const val BUTTON_PRESS_DURATION = 150L        // Button press animation duration (ms)
    
    /**
     * Apply neon glow effect to a paint object
     * @param paint The paint object to apply glow to
     * @param color The neon color for the glow
     * @param blurRadius The blur radius (default: GLOW_BLUR_RADIUS)
     * @param alpha The alpha value (default: GLOW_ALPHA)
     */
    fun applyGlow(
        paint: android.graphics.Paint,
        color: Int,
        blurRadius: Float = GLOW_BLUR_RADIUS,
        alpha: Int = GLOW_ALPHA
    ) {
        paint.color = color
        paint.maskFilter = android.graphics.BlurMaskFilter(
            blurRadius,
            android.graphics.BlurMaskFilter.Blur.NORMAL
        )
        paint.alpha = alpha
    }
    
    /**
     * Create a gradient shader for backgrounds
     * Creates a smooth gradient from start to end color
     * @param width Canvas width
     * @param height Canvas height
     * @param startColor Starting color (top-left)
     * @param endColor Ending color (bottom-right)
     * @return LinearGradient shader
     */
    fun createGradientShader(
        width: Float,
        height: Float,
        startColor: Int,
        endColor: Int
    ): android.graphics.LinearGradient {
        return android.graphics.LinearGradient(
            0f, 0f, width, height,
            startColor, endColor,
            android.graphics.Shader.TileMode.CLAMP
        )
    }
    
    /**
     * Create a radial gradient for glow effects
     * @param centerX Center X coordinate
     * @param centerY Center Y coordinate
     * @param radius Gradient radius
     * @param centerColor Color at center
     * @param edgeColor Color at edge
     * @return RadialGradient shader
     */
    fun createRadialGradient(
        centerX: Float,
        centerY: Float,
        radius: Float,
        centerColor: Int,
        edgeColor: Int
    ): android.graphics.RadialGradient {
        return android.graphics.RadialGradient(
            centerX, centerY, radius,
            centerColor, edgeColor,
            android.graphics.Shader.TileMode.CLAMP
        )
    }
    
    /**
     * Get neon color for tetromino based on original color
     * Maps standard colors to vibrant neon equivalents
     * @param originalColor The original tetromino color
     * @return Neon color equivalent
     */
    fun getNeonTetrominoColor(originalColor: Int): Int {
        return when (originalColor) {
            Color.CYAN -> NEON_CYAN
            Color.YELLOW -> NEON_YELLOW
            Color.MAGENTA -> NEON_PINK
            Color.rgb(255, 165, 0) -> NEON_ORANGE  // Orange
            Color.GREEN -> NEON_GREEN
            Color.BLUE -> NEON_BLUE
            Color.RED -> NEON_RED
            else -> NEON_CYAN  // Default to cyan
        }
    }
    
    /**
     * Get a random neon color from the palette
     * Useful for decorative elements
     */
    fun getRandomNeonColor(): Int {
        val colors = listOf(
            NEON_CYAN, NEON_PURPLE, NEON_PINK,
            NEON_BLUE, NEON_GREEN, NEON_YELLOW,
            NEON_ORANGE
        )
        return colors.random()
    }
    
    /**
     * Blend two colors with a given ratio
     * @param color1 First color
     * @param color2 Second color
     * @param ratio Blend ratio (0.0 = color1, 1.0 = color2)
     * @return Blended color
     */
    fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        val a1 = Color.alpha(color1)
        
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        val a2 = Color.alpha(color2)
        
        val r = (r1 + (r2 - r1) * ratio).toInt().coerceIn(0, 255)
        val g = (g1 + (g2 - g1) * ratio).toInt().coerceIn(0, 255)
        val b = (b1 + (b2 - b1) * ratio).toInt().coerceIn(0, 255)
        val a = (a1 + (a2 - a1) * ratio).toInt().coerceIn(0, 255)
        
        return Color.argb(a, r, g, b)
    }
}

