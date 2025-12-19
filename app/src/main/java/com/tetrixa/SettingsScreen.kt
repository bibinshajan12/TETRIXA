package com.tetrixa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tetrixa.ui.theme.*

/**
 * Settings screen with game configuration options
 * Allows users to adjust grid size and game speed
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsManager: SettingsManager
) {
    var gridWidth by remember { mutableStateOf(settingsManager.gridWidth) }
    var gridHeight by remember { mutableStateOf(settingsManager.gridHeight) }
    var gameSpeed by remember { mutableStateOf(settingsManager.gameSpeed) }
    
    // Note: System insets are already applied at HomeScreen level via padding
    // This ensures settings content is not drawn under status bar or navigation bar
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BgPrimary, BgSecondary, BgAccent, BgTertiary)
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Settings",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = CandyBlue
            )
            
            JuicyButton(
                text = "←",
                onClick = onBack,
                color = ButtonSecondary,
                modifier = Modifier
                    .width(70.dp)
                    .height(52.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Grid Size Section - vibrant card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = BgCard
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Grid Size",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CandyBlue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Grid Width
                Text(
                    text = "Columns: $gridWidth",
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    JuicyButton(
                        text = "-",
                        onClick = {
                            if (gridWidth > settingsManager.MIN_GRID_WIDTH) {
                                gridWidth--
                                settingsManager.gridWidth = gridWidth
                            }
                        },
                        enabled = gridWidth > settingsManager.MIN_GRID_WIDTH,
                        color = ButtonDanger,
                        modifier = Modifier.width(80.dp).height(56.dp)
                    )
                    JuicyButton(
                        text = "+",
                        onClick = {
                            if (gridWidth < settingsManager.MAX_GRID_WIDTH) {
                                gridWidth++
                                settingsManager.gridWidth = gridWidth
                            }
                        },
                        enabled = gridWidth < settingsManager.MAX_GRID_WIDTH,
                        color = ButtonSuccess,
                        modifier = Modifier.width(80.dp).height(56.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Grid Height
                Text(
                    text = "Rows: $gridHeight",
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    JuicyButton(
                        text = "-",
                        onClick = {
                            if (gridHeight > settingsManager.MIN_GRID_HEIGHT) {
                                gridHeight--
                                settingsManager.gridHeight = gridHeight
                            }
                        },
                        enabled = gridHeight > settingsManager.MIN_GRID_HEIGHT,
                        color = ButtonDanger,
                        modifier = Modifier.width(80.dp).height(56.dp)
                    )
                    JuicyButton(
                        text = "+",
                        onClick = {
                            if (gridHeight < settingsManager.MAX_GRID_HEIGHT) {
                                gridHeight++
                                settingsManager.gridHeight = gridHeight
                            }
                        },
                        enabled = gridHeight < settingsManager.MAX_GRID_HEIGHT,
                        color = ButtonSuccess,
                        modifier = Modifier.width(80.dp).height(56.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Game Speed Section - vibrant card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = BgCard
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Game Speed",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CandyBlue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                val speedLabels = listOf("Very Slow", "Slow", "Medium", "Fast", "Very Fast")
                Text(
                    text = "${speedLabels[gameSpeed - 1]} (${gameSpeed}/5)",
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    JuicyButton(
                        text = "-",
                        onClick = {
                            if (gameSpeed > settingsManager.MIN_GAME_SPEED) {
                                gameSpeed--
                                settingsManager.gameSpeed = gameSpeed
                            }
                        },
                        enabled = gameSpeed > settingsManager.MIN_GAME_SPEED,
                        color = ButtonDanger,
                        modifier = Modifier.width(80.dp).height(56.dp)
                    )
                    JuicyButton(
                        text = "+",
                        onClick = {
                            if (gameSpeed < settingsManager.MAX_GAME_SPEED) {
                                gameSpeed++
                                settingsManager.gameSpeed = gameSpeed
                            }
                        },
                        enabled = gameSpeed < settingsManager.MAX_GAME_SPEED,
                        color = ButtonSuccess,
                        modifier = Modifier.width(80.dp).height(56.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Speed description
                Text(
                    text = when (gameSpeed) {
                        1 -> "Relaxed pace, perfect for beginners"
                        2 -> "Comfortable speed for casual play"
                        3 -> "Balanced difficulty"
                        4 -> "Challenging speed"
                        5 -> "Maximum difficulty"
                        else -> ""
                    },
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Info text
        Text(
            text = "Settings are saved automatically and will apply to new games.",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

