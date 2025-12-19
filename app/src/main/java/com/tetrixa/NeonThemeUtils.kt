package com.tetrixa

import android.graphics.Paint
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat

/**
 * Utility functions for applying neon theme effects to UI elements
 * Provides easy-to-use methods for consistent neon styling
 */
object NeonThemeUtils {
    
    /**
     * Apply neon glow effect to a View
     * Adds elevation and shadow for a glowing appearance
     * @param view The view to apply glow to
     * @param glowColor The neon color for the glow (default: NEON_CYAN)
     * @param elevation The elevation value for the glow (default: 8dp)
     */
    fun applyNeonGlow(
        view: View,
        glowColor: Int = NeonTheme.NEON_CYAN,
        elevation: Float = 8f
    ) {
        ViewCompat.setElevation(view, elevation)
        // Note: For actual glow effect, use custom drawable or canvas drawing
        // This provides elevation-based shadow which gives depth
    }
    
    /**
     * Style a button with neon theme
     * Applies neon background, text color, and glow effect
     * @param button The button to style
     * @param backgroundColor Neon background color (default: NEON_CYAN)
     * @param textColor Text color (default: black for contrast)
     */
    fun styleNeonButton(
        button: Button,
        backgroundColor: Int = NeonTheme.BUTTON_BG,
        textColor: Int = NeonTheme.BUTTON_TEXT
    ) {
        button.setBackgroundColor(backgroundColor)
        button.setTextColor(textColor)
        button.textSize = 16f
        button.minHeight = 60 // Touch-friendly size
        ViewCompat.setElevation(button, 8f)
    }
    
    /**
     * Style text with neon glow effect
     * Applies neon color and shadow for glow appearance
     * @param textView The text view to style
     * @param neonColor The neon color (default: NEON_CYAN)
     * @param shadowRadius The shadow radius for glow (default: 10)
     */
    fun styleNeonText(
        textView: TextView,
        neonColor: Int = NeonTheme.TEXT_NEON,
        shadowRadius: Float = 10f
    ) {
        textView.setTextColor(neonColor)
        textView.setShadowLayer(shadowRadius, 0f, 0f, neonColor)
    }
    
    /**
     * Create a paint object with neon glow effect
     * Useful for custom drawing
     * @param color The neon color
     * @param blurRadius The blur radius for glow
     * @return Configured Paint object
     */
    fun createGlowPaint(
        color: Int = NeonTheme.NEON_CYAN,
        blurRadius: Float = NeonTheme.GLOW_BLUR_RADIUS
    ): Paint {
        val paint = Paint().apply {
            this.color = color
            isAntiAlias = true
            style = Paint.Style.FILL
            maskFilter = android.graphics.BlurMaskFilter(
                blurRadius,
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
            alpha = NeonTheme.GLOW_ALPHA
        }
        return paint
    }
    
    /**
     * Animate button press with neon glow intensification
     * Scales button and increases elevation for tactile feedback
     * @param button The button to animate
     * @param onComplete Callback when animation completes
     */
    fun animateNeonButtonPress(
        button: Button,
        onComplete: (() -> Unit)? = null
    ) {
        val originalElevation = ViewCompat.getElevation(button)
        val targetElevation = originalElevation * 1.5f
        
        // Scale animation
        button.animate()
            .scaleX(0.92f)
            .scaleY(0.92f)
            .setDuration(NeonTheme.BUTTON_PRESS_DURATION)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(NeonTheme.BUTTON_PRESS_DURATION)
                    .withEndAction {
                        onComplete?.invoke()
                    }
                    .start()
            }
            .start()
        
        // Elevation animation for glow effect
        ViewCompat.animate(button)
            .translationZ(targetElevation)
            .setDuration(NeonTheme.BUTTON_PRESS_DURATION)
            .withEndAction {
                ViewCompat.animate(button)
                    .translationZ(originalElevation)
                    .setDuration(NeonTheme.BUTTON_PRESS_DURATION)
                    .start()
            }
            .start()
    }
}

