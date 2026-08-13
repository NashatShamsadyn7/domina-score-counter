package com.nashat.scorecounter.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "game_session")
data class GameSessionEntity(
    @PrimaryKey val id: Int = 0,
    val gameMode: String,
    val playersJson: String
)

@Dao
interface GameSessionDao {
    @Query("SELECT * FROM game_session WHERE id = 0")
    fun observeSession(): Flow<GameSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: GameSessionEntity)
}

@Database(entities = [GameSessionEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameSessionDao(): GameSessionDao

    companion object {
        @Volatile
        private var instance: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "score_counter.db"
                ).build().also { instance = it }
            }
        }
    }
}

object PlayerStateJson {
    fun encode(players: List<com.nashat.scorecounter.model.PlayerState>): String {
        val array = JSONArray()
        players.forEach { player ->
            array.put(
                JSONObject()
                    .put("id", player.id)
                    .put("name", player.name)
                    .put("score", player.score)
                    .put("undoStack", JSONArray(player.undoStack))
                    .put("redoStack", JSONArray(player.redoStack))
            )
        }
        return array.toString()
    }

    fun decode(json: String): List<com.nashat.scorecounter.model.PlayerState> {
        val array = JSONArray(json)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            com.nashat.scorecounter.model.PlayerState(
                id = item.getInt("id"),
                name = item.getString("name"),
                score = item.getInt("score"),
                undoStack = item.getJSONArray("undoStack").toIntList(),
                redoStack = item.getJSONArray("redoStack").toIntList()
            )
        }
    }

    private fun JSONArray.toIntList(): List<Int> = List(length()) { getInt(it) }
}
