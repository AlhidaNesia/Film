package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CinemaRed,
    onPrimary = Color.White,
    primaryContainer = CinemaRedDark,
    onPrimaryContainer = Color.White,
    secondary = CinemaGold,
    onSecondary = Color.Black,
    secondaryContainer = CinemaSurfaceVariant,
    onSecondaryContainer = CinemaGoldMuted,
    tertiary = CinemaAccentBlue,
    onTertiary = Color.White,
    background = CinemaBg,
    onBackground = CinemaTextPrimary,
    surface = CinemaSurface,
    onSurface = CinemaTextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = CinemaTextSecondary,
    surfaceContainerHigh = CinemaSurfaceBright,
    outline = CinemaTextMuted
)

private val LightColorScheme = darkColorScheme(
    // For a streaming movie app, dark cinema theme is always preferred for immersive viewing
    primary = CinemaRed,
    onPrimary = Color.White,
    primaryContainer = CinemaRedDark,
    onPrimaryContainer = Color.White,
    secondary = CinemaGold,
    onSecondary = Color.Black,
    secondaryContainer = CinemaSurfaceVariant,
    onSecondaryContainer = CinemaGoldMuted,
    tertiary = CinemaAccentBlue,
    onTertiary = Color.White,
    background = CinemaBg,
    onBackground = CinemaTextPrimary,
    surface = CinemaSurface,
    onSurface = CinemaTextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = CinemaTextSecondary,
    outline = CinemaTextMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to cinema dark mode
    dynamicColor: Boolean = false, // Keep cinema branding consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
