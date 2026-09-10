package com.nashat.scorecounter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.nashat.scorecounter.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = PrimaryIndigo,
    background = AppBackground,
    surface = CardSurface
)

private val DarkColors = darkColorScheme(
    primary = PrimaryIndigo,
    background = androidx.compose.ui.graphics.Color(0xFF0F1222),
    surface = androidx.compose.ui.graphics.Color(0xFF171B2F)
)

@Composable
fun ScoreCounterTheme(
    themeMode: ThemeMode,
    fontSize: Int,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = scoreCounterTypography(fontSize),
        content = content
    )
}
