package com.nashat.scorecounter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nashat.scorecounter.data.GameRepository
import com.nashat.scorecounter.data.SettingsDataStore
import com.nashat.scorecounter.model.AppLanguage
import com.nashat.scorecounter.model.AppSettings
import com.nashat.scorecounter.model.GameMode
import com.nashat.scorecounter.model.PlayerState
import com.nashat.scorecounter.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val gameMode: GameMode = GameMode.TWO_TEAMS,
    val players: List<PlayerState> = GameViewModel.defaultPlayers(GameMode.TWO_TEAMS)
)

class GameViewModel(
    private val settingsDataStore: SettingsDataStore,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val gameState = MutableStateFlow(GameUiState())
    private var hasLoadedSavedState = false

    init {
        viewModelScope.launch {
            val savedState = gameRepository.observeGameState().first()
            if (savedState != null) {
                gameState.value = GameUiState(
                    gameMode = savedState.gameMode,
                    players = savedState.players
                )
            }
            hasLoadedSavedState = true
        }

        viewModelScope.launch {
            gameState.collect { state ->
                if (hasLoadedSavedState) {
                    gameRepository.save(state.gameMode, state.players)
                }
            }
        }
    }

    val settings: StateFlow<AppSettings> = settingsDataStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings()
    )

    val uiState: StateFlow<GameUiState> = combine(gameState, settings) { state, appSettings ->
        if (state.players.size == state.gameMode.playerCount) {
            state.copy(players = ensureNames(state.players, state.gameMode, appSettings.language))
        } else {
            state
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GameUiState()
    )

    fun selectMode(mode: GameMode) {
        val language = settings.value.language
        gameState.value = GameUiState(
            gameMode = mode,
            players = defaultPlayers(mode, language)
        )
    }

    fun updateName(playerId: Int, name: String) {
        gameState.update { state ->
            state.copy(players = state.players.map { player ->
                if (player.id == playerId) player.copy(name = name) else player
            })
        }
    }

    fun changeScore(playerId: Int, delta: Int): Int {
        var effectiveDelta = 0
        gameState.update { state ->
            state.copy(players = state.players.map { player ->
                if (player.id == playerId) {
                    val newScore = (player.score + delta).coerceAtLeast(0)
                    effectiveDelta = newScore - player.score
                    player.copy(
                        score = newScore,
                        undoStack = player.undoStack + effectiveDelta,
                        redoStack = emptyList()
                    )
                } else player
            })
        }
        return effectiveDelta
    }

    fun undo(playerId: Int) {
        gameState.update { state ->
            state.copy(players = state.players.map { player ->
                if (player.id == playerId && player.undoStack.isNotEmpty()) {
                    val last = player.undoStack.last()
                    player.copy(
                        score = player.score - last,
                        undoStack = player.undoStack.dropLast(1),
                        redoStack = player.redoStack + last
                    )
                } else player
            })
        }
    }

    fun redo(playerId: Int) {
        gameState.update { state ->
            state.copy(players = state.players.map { player ->
                if (player.id == playerId && player.redoStack.isNotEmpty()) {
                    val last = player.redoStack.last()
                    player.copy(
                        score = player.score + last,
                        undoStack = player.undoStack + last,
                        redoStack = player.redoStack.dropLast(1)
                    )
                } else player
            })
        }
    }

    fun resetAll() {
        gameState.update { state ->
            state.copy(players = state.players.map { player ->
                player.copy(score = 0, undoStack = emptyList(), redoStack = emptyList())
            })
        }
    }

    fun resetToDefaultMode() {
        selectMode(GameMode.TWO_TEAMS)
    }

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch { settingsDataStore.updateTheme(themeMode) }
    }

    fun setSound(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.updateSound(enabled) }
    }

    fun setVoice(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.updateVoice(enabled) }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.updateVibration(enabled) }
    }

    fun setAnimation(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.updateAnimation(enabled) }
    }

    fun setCelebration(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.updateCelebration(enabled) }
    }

    fun setFontSize(size: Int) {
        viewModelScope.launch { settingsDataStore.updateFontSize(size) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsDataStore.updateLanguage(language)
            gameState.update { state ->
                state.copy(players = ensureNames(state.players, state.gameMode, language))
            }
        }
    }

    private fun ensureNames(players: List<PlayerState>, mode: GameMode, language: AppLanguage): List<PlayerState> {
        val defaults = defaultPlayers(mode, language)
        val knownDefaultNames = GameMode.entries.flatMap { gameMode ->
            AppLanguage.entries.flatMap { appLanguage ->
                defaultPlayers(gameMode, appLanguage).map { it.name }
            }
        }.toSet()
        return players.mapIndexed { index, player ->
            if (player.name in knownDefaultNames) {
                player.copy(name = defaults.getOrNull(index)?.name ?: player.name)
            } else player
        }
    }

    companion object {
        fun defaultPlayers(mode: GameMode, language: AppLanguage = AppLanguage.KURDISH): List<PlayerState> {
            return List(mode.playerCount) { index ->
                PlayerState(id = index, name = defaultName(index, mode, language))
            }
        }

        private fun defaultName(index: Int, mode: GameMode, language: AppLanguage): String {
            return when (language) {
                AppLanguage.KURDISH -> when (mode) {
                    GameMode.TWO_TEAMS -> if (index == 0) "تیمی یەکەم" else "تیمی دووەم"
                    GameMode.THREE_PLAYERS,
                    GameMode.FOUR_PLAYERS -> listOf("یاریزانی یەکەم", "یاریزانی دووەم", "یاریزانی سێیەم", "یاریزانی چوارەم")[index]
                }

                AppLanguage.ARABIC -> when (mode) {
                    GameMode.TWO_TEAMS -> if (index == 0) "الفريق الأول" else "الفريق الثاني"
                    GameMode.THREE_PLAYERS,
                    GameMode.FOUR_PLAYERS -> listOf("اللاعب الأول", "اللاعب الثاني", "اللاعب الثالث", "اللاعب الرابع")[index]
                }

                AppLanguage.ENGLISH -> when (mode) {
                    GameMode.TWO_TEAMS -> if (index == 0) "Team One" else "Team Two"
                    GameMode.THREE_PLAYERS,
                    GameMode.FOUR_PLAYERS -> listOf("Player One", "Player Two", "Player Three", "Player Four")[index]
                }
            }
        }
    }
}

class GameViewModelFactory(
    private val settingsDataStore: SettingsDataStore,
    private val gameRepository: GameRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(settingsDataStore, gameRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
