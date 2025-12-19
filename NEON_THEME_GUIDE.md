# Neon Theme Implementation Guide

This document describes the comprehensive neon theme system implemented for the Tetris game.

## Overview

The neon theme provides a vibrant, modern aesthetic with:
- Dark backgrounds with subtle gradients
- Neon color palette (cyan, purple, pink, blue, green, yellow, orange)
- Glow effects on UI elements
- Smooth animations
- High contrast for readability
- Performance-optimized rendering

## Components

### 1. NeonTheme.kt
Central theme constants and utility functions:
- **Color Palette**: All neon colors defined as constants
- **Background Colors**: Dark blue-black gradients
- **Glow Settings**: Blur radius, intensity, alpha values
- **Helper Functions**:
  - `applyGlow()` - Apply glow effect to Paint objects
  - `createGradientShader()` - Create gradient backgrounds
  - `createRadialGradient()` - Create radial glow effects
  - `getNeonTetrominoColor()` - Map colors to neon equivalents
  - `blendColors()` - Blend two colors

### 2. NeonThemeUtils.kt
Utility functions for applying neon effects to Views:
- `applyNeonGlow()` - Add glow to any View
- `styleNeonButton()` - Style buttons with neon theme
- `styleNeonText()` - Style text with neon glow
- `createGlowPaint()` - Create Paint with glow effect
- `animateNeonButtonPress()` - Animate button press with glow

### 3. Color Resources (colors.xml)
XML color definitions for use in layouts:
- Neon colors: `neon_cyan`, `neon_purple`, `neon_pink`, etc.
- Dark backgrounds: `dark_bg_primary`, `dark_bg_secondary`, etc.
- Text colors: `text_primary`, `text_secondary`, `text_neon`
- UI elements: `grid_line`, `ghost_piece`, `button_glow`

### 4. Drawable Resources
- `neon_button_background.xml` - Neon button background
- `neon_button_pressed.xml` - Button pressed state with glow
- `neon_card_background.xml` - Card background with neon border

### 5. Styles (styles.xml)
Pre-defined styles for common UI elements:
- `NeonButton` - Styled button with neon colors
- `NeonText` - Text with neon color and glow
- `NeonTextLarge` - Large neon text
- `NeonCard` - Card with neon styling

### 6. Compose Theme (ui/theme/)
For Jetpack Compose UI:
- `Color.kt` - Compose color definitions
- `Theme.kt` - Material3 theme with neon colors
- `Type.kt` - Typography settings

## Usage Examples

### In View-based Code:
```kotlin
// Style a button
NeonThemeUtils.styleNeonButton(button)

// Style text with glow
NeonThemeUtils.styleNeonText(textView, NeonTheme.NEON_CYAN)

// Apply glow to any view
NeonThemeUtils.applyNeonGlow(view, NeonTheme.NEON_PURPLE, 12f)

// Animate button press
NeonThemeUtils.animateNeonButtonPress(button) {
    // Action on complete
}
```

### In Custom Drawing:
```kotlin
// Create glow paint
val glowPaint = NeonThemeUtils.createGlowPaint(NeonTheme.NEON_CYAN)

// Apply glow to existing paint
NeonTheme.applyGlow(paint, NeonTheme.NEON_PINK, 15f)

// Create gradient background
val gradient = NeonTheme.createGradientShader(
    width, height,
    NeonTheme.DARK_BG_PRIMARY,
    NeonTheme.DARK_BG_TERTIARY
)
backgroundPaint.shader = gradient
```

### In XML Layouts:
```xml
<!-- Use neon button style -->
<Button
    style="@style/NeonButton"
    android:text="Play" />

<!-- Use neon text style -->
<TextView
    style="@style/NeonText"
    android:text="Score" />

<!-- Use neon colors directly -->
<View
    android:background="@color/neon_cyan" />
```

### In Compose:
```kotlin
GBTetrisTheme {
    // Theme automatically applied
    Text(
        text = "Hello",
        color = NeonCyan
    )
}
```

## Color Palette

### Neon Colors
- **Cyan** (`#00FFFF`) - Primary accent, buttons, highlights
- **Purple** (`#9D00FF`) - Secondary accent
- **Pink** (`#FF00FF`) - Tertiary accent, game over
- **Blue** (`#0080FF`) - Information, links
- **Green** (`#00FF80`) - Success states
- **Yellow** (`#FFFF00`) - Warnings, highlights
- **Orange** (`#FF8000`) - Alerts, special elements
- **Red** (`#FF0040`) - Errors, critical states

### Background Colors
- **Primary** (`#0A0A15`) - Main background
- **Secondary** (`#151520`) - Cards, panels
- **Tertiary** (`#1A1A2E`) - Elevated surfaces
- **Quaternary** (`#202035`) - Highest elevation

## Performance Considerations

1. **Glow Effects**: Use `BlurMaskFilter` sparingly - it's GPU-intensive
2. **Gradients**: Cache shaders, recreate only on size changes
3. **Animations**: Use `ValueAnimator` with proper interpolation
4. **Invalidation**: Only invalidate necessary regions
5. **Paint Reuse**: Reuse Paint objects instead of creating new ones

## Best Practices

1. **Consistency**: Always use `NeonTheme` constants, never hardcode colors
2. **Contrast**: Ensure text is readable on dark backgrounds
3. **Glow Intensity**: Keep glows subtle to avoid distraction
4. **Animation Duration**: Use consistent timing (150ms for buttons, 3000ms for backgrounds)
5. **Color Mapping**: Use `getNeonTetrominoColor()` for game pieces

## Future Enhancements

- Custom shaders for advanced glow effects
- Animated gradient backgrounds
- Particle effects for line clears
- Dynamic color intensity based on game state
- Theme variants (different neon color schemes)

