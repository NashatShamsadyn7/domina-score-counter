package com.nashat.scorecounter.model

data class PlayerState(
    val id: Int,
    val name: String,
    val score: Int = 0,
    val undoStack: List<Int> = emptyList(),
    val redoStack: List<Int> = emptyList()
)

enum class GameMode(val playerCount: Int) {
    TWO_TEAMS(2),
    THREE_PLAYERS(3),
    FOUR_PLAYERS(4)
}

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class AppLanguage {
    KURDISH,
    ARABIC,
    ENGLISH
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val animationEnabled: Boolean = true,
    val celebrationEnabled: Boolean = true,
    val fontSize: Int = 16,
    val language: AppLanguage = AppLanguage.KURDISH
)
