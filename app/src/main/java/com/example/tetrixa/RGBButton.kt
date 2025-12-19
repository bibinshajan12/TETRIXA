package com.example.tetrixa

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetrixa.ui.theme.*
import kotlinx.coroutines.delay

/**
 * RGB Neon Button with animated color cycling accent and size-scaled glow
 * Features:
 * - RGB ring that cycles through red → green → blue
 * - Glow prominence scales with button size (larger = stronger glow)
 * - Smooth animations and touch feedback
 */
@Composable
fun RGBNeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Measure button size for glow scaling (in pixels for calculations)
    var buttonHeight by remember { mutableStateOf(64f) }
    var buttonWidth by remember { mutableStateOf(0f) }
    // RGB color animation - cycles through red, green, blue
    val infiniteTransition = rememberInfiniteTransition(label = "rgb_animation")
    val rgbProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000),
                repeatMode = RepeatMode.Restart
            ),
        label = "rgb_progress"
    )
    
    // Calculate RGB color based on progress (0-1)
    // Cycles: Red (0) → Green (0.33) → Blue (0.66) → Red (1)
    val rgbColor = when {
        rgbProgress < 0.33f -> {
            // Red to Green
            val t = rgbProgress / 0.33f
            Color(
                red = 1f - t,
                green = t,
                blue = 0f
            )
        }
        rgbProgress < 0.66f -> {
            // Green to Blue
            val t = (rgbProgress - 0.33f) / 0.33f
            Color(
                red = 0f,
                green = 1f - t,
                blue = t
            )
        }
        else -> {
            // Blue to Red
            val t = (rgbProgress - 0.66f) / 0.34f
            Color(
                red = t,
                green = 0f,
                blue = 1f - t
            )
        }
    }
    
    // Button press animation
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "button_scale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 12f else 8f,
        animationSpec = tween(durationMillis = 150),
        label = "button_elevation"
    )
    
    // Reset pressed state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
    
    Button(
        onClick = {
            if (enabled) {
                isPressed = true
                onClick()
            }
        },
        enabled = enabled,
        modifier = modifier
            .height(64.dp) // Larger touch-friendly size
            .scale(scale)
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .then(
                // Size-based glow scaling: larger buttons get stronger glow
                // Calculate glow intensity based on button dimensions
                Modifier.onSizeChanged { size ->
                    buttonWidth = size.width.toFloat()
                    buttonHeight = size.height.toFloat()
                }
            )
            .border(
                width = (2.dp + (buttonHeight / 64f).coerceIn(0f, 2f).dp), // 2-4dp based on size
                brush = Brush.linearGradient(
                    colors = listOf(
                        // Scale alpha based on button size (0.75-1.0 for larger buttons)
                        // Larger buttons get more prominent glow
                        rgbColor.copy(alpha = (0.75f + (buttonHeight / 250f).coerceIn(0f, 0.25f))),
                        rgbColor.copy(alpha = (0.55f + (buttonHeight / 350f).coerceIn(0f, 0.25f)))
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonPrimary,
            disabledContainerColor = ButtonPrimary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

