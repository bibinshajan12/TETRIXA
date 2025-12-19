package com.example.tetrixa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.animation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.tetrixa.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Main Activity with Neon Theme
 * Home screen with navigation to game
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge mode: content draws behind system bars
        enableEdgeToEdge()
        
        // Configure window to not fit system windows (allows edge-to-edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            TETRIXATheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var showGame by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    
    // Get system window insets to handle status bar and navigation bar
    // This ensures content is not drawn under system UI
    val windowInsets = androidx.compose.foundation.layout.WindowInsets.systemBars
    val insets = windowInsets.asPaddingValues()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BgPrimary,
                        BgSecondary,
                        BgAccent,
                        BgTertiary
                    )
                )
            )
            // Apply system bar insets: add padding to avoid system UI overlap
            .padding(insets)
    ) {
        // Screen transitions with slide + fade
        AnimatedContent(
            targetState = when {
                showGame -> "game"
                showSettings -> "settings"
                else -> "menu"
            },
            transitionSpec = {
                // Slide + fade animation
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(
                    animationSpec = tween(300)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                "game" -> GameScreen(
                    onBack = { showGame = false },
                    settingsManager = settingsManager
                )
                "settings" -> SettingsScreen(
                    onBack = { showSettings = false },
                    settingsManager = settingsManager
                )
                else -> MainMenu(
                    onPlayClick = { showGame = true },
                    onSettingsClick = { showSettings = true }
                )
            }
        }
    }
}

@Composable
fun MainMenu(onPlayClick: () -> Unit, onSettingsClick: () -> Unit) {
    // Pop-in animation for elements
    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }
    var playButtonVisible by remember { mutableStateOf(false) }
    var settingsButtonVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        titleVisible = true
        kotlinx.coroutines.delay(100)
        subtitleVisible = true
        kotlinx.coroutines.delay(150)
        playButtonVisible = true
        kotlinx.coroutines.delay(150)
        settingsButtonVisible = true
    }
    
    // Note: System insets are already applied at HomeScreen level
    // This ensures menu content is within the safe area
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title with pop-in animation
        AnimatedVisibility(
            visible = titleVisible,
            enter = scaleIn(
                initialScale = 0.5f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(400)),
            label = "title_pop_in"
        ) {
            Text(
                text = "TETRIXA",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = CandyBlue,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Subtitle with pop-in
        AnimatedVisibility(
            visible = subtitleVisible,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(400)),
            label = "subtitle_pop_in"
        ) {
            Text(
                text = "Sweet Puzzle Fun!",
                fontSize = 22.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 48.dp)
            )
        }
        
        // Play Button with pop-in
        AnimatedVisibility(
            visible = playButtonVisible,
            enter = scaleIn(
                initialScale = 0.7f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(400)),
            label = "play_button_pop_in"
        ) {
            JuicyButton(
                text = "PLAY",
                onClick = onPlayClick,
                color = ButtonPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Settings Button with pop-in
        AnimatedVisibility(
            visible = settingsButtonVisible,
            enter = scaleIn(
                initialScale = 0.7f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(400)),
            label = "settings_button_pop_in"
        ) {
            JuicyButton(
                text = "SETTINGS",
                onClick = onSettingsClick,
                color = ButtonSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Developer credit
        Text(
            text = "Developed by Bibin Shajan",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

// NeonButton replaced by RGBNeonButton - see RGBButton.kt

@Composable
fun GameScreen(onBack: () -> Unit, settingsManager: SettingsManager) {
    // Track current game speed setting to observe changes
    // This ensures the game responds to speed changes from settings
    // Speed setting flow: SettingsManager.gameSpeed -> getInitialFallDelay() -> TetrisGame
    var currentGameSpeed by remember { mutableStateOf(settingsManager.gameSpeed) }
    var currentGridWidth by remember { mutableStateOf(settingsManager.gridWidth) }
    var currentGridHeight by remember { mutableStateOf(settingsManager.gridHeight) }
    
    // Initialize game with current settings
    // The game will use these settings for fall delay calculation
    val game = remember(currentGridWidth, currentGridHeight, currentGameSpeed) { 
        TetrisGame(
            gridWidth = currentGridWidth,
            gridHeight = currentGridHeight,
            initialFallDelay = settingsManager.getInitialFallDelay(), // Read from current speed setting
            onScoreChanged = { _, _ -> },
            onGameStateChanged = { }
        )
    }
    
    // Poll settings periodically to detect changes
    // This ensures speed changes in settings are applied to gameplay
    // Speed flow: Settings screen -> SettingsManager.gameSpeed -> getInitialFallDelay() -> game.updateInitialFallDelay()
    LaunchedEffect(Unit) {
        while (true) {
            // Check if speed setting has changed
            val newSpeed = settingsManager.gameSpeed
            if (newSpeed != currentGameSpeed) {
                currentGameSpeed = newSpeed
                // Update the game's fall delay to reflect new speed setting
                // This makes speed changes take effect immediately, even during gameplay
                game.updateInitialFallDelay(settingsManager.getInitialFallDelay())
            }
            
            // Check if grid size has changed
            val newWidth = settingsManager.gridWidth
            val newHeight = settingsManager.gridHeight
            if (newWidth != currentGridWidth || newHeight != currentGridHeight) {
                currentGridWidth = newWidth
                currentGridHeight = newHeight
                // Grid size changes require game restart, handled in startNewGame
            }
            
            delay(500) // Check settings every 500ms
        }
    }
    
    var score by remember { mutableStateOf(0) }
    var level by remember { mutableStateOf(1) }
    var gameState by remember { mutableStateOf(GameState.GAME_OVER) }
    var isPaused by remember { mutableStateOf(false) }
    
    // Force recomposition when game state changes
    LaunchedEffect(Unit) {
        while (true) {
            score = game.getScore()
            level = game.getLevel()
            gameState = game.getGameState()
            delay(100)
        }
    }
    
    // Responsive layout with dark gameplay theme
    // Visual darkening: Gameplay screen uses darker background for visual comfort
    // This darker theme applies only to gameplay, keeping menus bright and friendly
    // Note: System insets are already applied at HomeScreen level via padding
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GameBgDark,           // Dark base
                        GameBgDarkSecondary,  // Slightly lighter
                        GameBgDarkTertiary    // Gradient end
                    )
                )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Header with back button and score - playful design
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button - juicy style
            JuicyButton(
                text = "←",
                onClick = onBack,
                color = ButtonSecondary,
                modifier = Modifier
                    .width(70.dp)
                    .height(52.dp)
            )
            
            // Score display - dark theme card design
            // Visual darkening: Darker card matches gameplay theme while maintaining readability
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                GameBgDarkSecondary,
                                GameBgDarkTertiary.copy(alpha = 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = CandyBlue.copy(alpha = 0.3f)
                    )
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "$score",
                    color = CandyBlue,  // Bright accent for visibility on dark background
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Level $level",
                    color = TextSecondary.copy(alpha = 0.9f),  // Slightly brighter for dark theme
                    fontSize = 16.sp
                )
            }
            
            // Pause/Resume button - juicy style
            JuicyButton(
                text = if (isPaused) "▶" else "⏸",
                onClick = {
                    if (gameState == GameState.RUNNING) {
                        game.pause()
                        isPaused = true
                    } else if (gameState == GameState.PAUSED) {
                        game.resume()
                        isPaused = false
                    }
                },
                enabled = gameState != GameState.GAME_OVER,
                color = if (isPaused) ButtonSuccess else ButtonDanger,
                modifier = Modifier
                    .width(70.dp)
                    .height(52.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Game board - dark rounded design with subtle glow
        // Visual darkening: Darker container matches gameplay theme
        TetrisBoard(
            game = game,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GameBgDarkSecondary,
                            GameBgDarkTertiary.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = CandyBlue.copy(alpha = 0.2f),  // Subtle glow on dark background
                    ambientColor = CandyPurple.copy(alpha = 0.1f)
                )
                .padding(10.dp),
            onScoreChanged = { newScore, newLevel ->
                score = newScore
                level = newLevel
            },
            onGameStateChanged = { state ->
                gameState = state
                isPaused = (state == GameState.PAUSED)
            }
        )
        
        // Game over or start message - always visible at bottom
        // Fixed height container ensures it's never clipped
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Fixed height to ensure visibility
                .padding(top = 8.dp)
        ) {
            if (gameState == GameState.GAME_OVER) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                if (score > 0) {
                    Text(
                        text = "Game Over!",
                        color = CandyRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                JuicyButton(
                    text = if (score > 0) "Play Again" else "Start Game",
                    onClick = {
                        game.updateInitialFallDelay(settingsManager.getInitialFallDelay())
                        game.startNewGame()
                        score = 0
                        level = 1
                        isPaused = false
                    },
                    color = ButtonSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
                }
            }
        }
    }
}