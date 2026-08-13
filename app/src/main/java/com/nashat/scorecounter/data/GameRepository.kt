package com.nashat.scorecounter.data

import com.nashat.scorecounter.model.GameMode
import com.nashat.scorecounter.model.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class SavedGameState(
    val gameMode: GameMode,
    val players: List<PlayerState>
)

class GameRepository(
    private val dao: GameSessionDao
) {
    fun observeGameState(): Flow<SavedGameState?> {
        return dao.observeSession().map { session ->
            session?.let {
                SavedGameState(
                    gameMode = GameMode.valueOf(it.gameMode),
                    players = PlayerStateJson.decode(it.playersJson)
                )
            }
        }
    }

    suspend fun save(gameMode: GameMode, players: List<PlayerState>) {
        dao.upsertSession(
            GameSessionEntity(
                gameMode = gameMode.name,
                playersJson = PlayerStateJson.encode(players)
            )
        )
    }
}
