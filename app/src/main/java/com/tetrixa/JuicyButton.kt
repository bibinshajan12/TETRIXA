package com.tetrixa

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tetrixa.ui.theme.*

/**
 * Juicy Candy Crush-style button with squash + bounce animation
 * Features:
 * - Vibrant gradients with strong contrast
 * - Squash + bounce animation on press
 * - Glossy highlights and shadows
 * - Playful, pressable appearance
 */
@Composable
fun JuicyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = ButtonPrimary
) {
    // Animation states
    var isPressed by remember { mutableStateOf(false) }
    
    // Squash + bounce animation (scale down then bounce back)
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_squash_bounce"
    )
    
    // Shadow elevation animation (press down = less shadow)
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 16f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_elevation"
    )
    
    // Reset pressed state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(200)
            isPressed = false
        }
    }
    
    // Create vibrant gradient colors (darker at bottom for depth)
    val gradientColors = listOf(
        color,                           // Full color at top
        color.copy(alpha = 0.95f),      // Slightly darker
        color.copy(red = (color.red * 0.85f).coerceIn(0f, 1f),
                   green = (color.green * 0.85f).coerceIn(0f, 1f),
                   blue = (color.blue * 0.85f).coerceIn(0f, 1f)) // Darker at bottom
    )
    
    Box(
        modifier = modifier
            .height(68.dp)
            .scale(scale)
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = color.copy(alpha = 0.4f),
                ambientColor = color.copy(alpha = 0.2f)
            )
            .then(
                if (enabled) {
                    Modifier.clickable(
                        onClick = {
                            isPressed = true
                            onClick()
                        }
                    )
                } else {
                    Modifier
                }
            )
    ) {
        // Gradient background with glossy effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Top highlight for glossy effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            )
            
            // Content
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
    
}
