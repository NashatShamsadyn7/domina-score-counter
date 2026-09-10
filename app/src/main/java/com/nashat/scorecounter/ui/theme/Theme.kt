package com.nashat.scorecounter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.nashat.scorecounter.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    background = AppBackground,
    onBackground = LightTextPrimary,
    surface = CardSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = Color(0xFFE4E8F7),
    onSurfaceVariant = LightTextSecondary,
    outline = CardBorder
)

private val DarkColors = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkCardSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkUndoDisabled,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder
)

/**
 * Extra semantic colors the Material scheme does not cover: gradients,
 * card borders and the small control tints used across the screens.
 */
@Immutable
data class DominaColors(
    val background: Color,
    val gradientTop: Color,
    val gradientMid: Color,
    val gradientBottom: Color,
    val cardSurface: Color,
    val cardTop: Color,
    val cardBottom: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val undoEnabled: Color,
    val undoDisabled: Color,
    val sheetCancelContainer: Color,
    val sheetCancelContent: Color,
    val orbAlpha1: Float,
    val orbAlpha2: Float
)

val LightDominaColors = DominaColors(
    background = AppBackground,
    gradientTop = AppBackground,
    gradientMid = Color(0xFFF7F8FF),
    gradientBottom = Color(0xFFE8ECFF),
    cardSurface = CardSurface,
    cardTop = Color.White,
    cardBottom = Color(0xFFEEF2FF),
    cardBorder = CardBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    undoEnabled = Color(0xFF50C8D7),
    undoDisabled = Color(0xFFE4E8F7),
    sheetCancelContainer = Color(0xFFEAECF8),
    sheetCancelContent = Color(0xFF474B67),
    orbAlpha1 = 0.06f,
    orbAlpha2 = 0.045f
)

val DarkDominaColors = DominaColors(
    background = DarkBackground,
    gradientTop = DarkBackground,
    gradientMid = DarkGradientMid,
    gradientBottom = DarkGradientBottom,
    cardSurface = DarkCardSurface,
    cardTop = DarkCardTop,
    cardBottom = DarkCardBottom,
    cardBorder = DarkCardBorder,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    undoEnabled = Color(0xFF50C8D7),
    undoDisabled = DarkUndoDisabled,
    sheetCancelContainer = DarkSheetCancelContainer,
    sheetCancelContent = DarkSheetCancelContent,
    orbAlpha1 = 0.10f,
    orbAlpha2 = 0.08f
)

val LocalDominaColors = staticCompositionLocalOf { LightDominaColors }
val LocalDarkTheme = staticCompositionLocalOf { false }

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
    val dominaColors = if (darkTheme) DarkDominaColors else LightDominaColors

    CompositionLocalProvider(
        LocalDominaColors provides dominaColors,
        LocalDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = scoreCounterTypography(fontSize),
            content = content
        )
    }
}
