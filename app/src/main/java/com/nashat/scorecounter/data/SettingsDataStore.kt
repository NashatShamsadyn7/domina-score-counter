package com.nashat.scorecounter.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nashat.scorecounter.model.AppLanguage
import com.nashat.scorecounter.model.AppSettings
import com.nashat.scorecounter.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsStore by preferencesDataStore(name = "score_counter_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val theme = stringPreferencesKey("theme")
        val sound = booleanPreferencesKey("sound")
        val vibration = booleanPreferencesKey("vibration")
        val animation = booleanPreferencesKey("animation")
        val celebration = booleanPreferencesKey("celebration")
        val fontSize = intPreferencesKey("font_size")
        val language = stringPreferencesKey("language")
    }

    val settings: Flow<AppSettings> = context.settingsStore.data.map { preferences ->
        AppSettings(
            themeMode = preferences[Keys.theme]?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM,
            soundEnabled = preferences[Keys.sound] ?: true,
            vibrationEnabled = preferences[Keys.vibration] ?: true,
            animationEnabled = preferences[Keys.animation] ?: true,
            celebrationEnabled = preferences[Keys.celebration] ?: true,
            fontSize = (preferences[Keys.fontSize] ?: 16).coerceIn(4, 40),
            language = preferences[Keys.language]?.let { AppLanguage.valueOf(it) } ?: AppLanguage.KURDISH
        )
    }

    suspend fun updateTheme(themeMode: ThemeMode) {
        update { it[Keys.theme] = themeMode.name }
    }

    suspend fun updateSound(enabled: Boolean) {
        update { it[Keys.sound] = enabled }
    }

    suspend fun updateVibration(enabled: Boolean) {
        update { it[Keys.vibration] = enabled }
    }

    suspend fun updateAnimation(enabled: Boolean) {
        update { it[Keys.animation] = enabled }
    }

    suspend fun updateCelebration(enabled: Boolean) {
        update { it[Keys.celebration] = enabled }
    }

    suspend fun updateFontSize(size: Int) {
        update { it[Keys.fontSize] = size.coerceIn(4, 40) }
    }

    suspend fun updateLanguage(language: AppLanguage) {
        update { it[Keys.language] = language.name }
    }

    private suspend fun update(transform: (MutablePreferencesProxy) -> Unit) {
        context.settingsStore.edit { preferences ->
            transform(MutablePreferencesProxy(preferences))
        }
    }

    class MutablePreferencesProxy(private val preferences: androidx.datastore.preferences.core.MutablePreferences) {
        operator fun set(key: Preferences.Key<String>, value: String) {
            preferences[key] = value
        }

        operator fun set(key: Preferences.Key<Boolean>, value: Boolean) {
            preferences[key] = value
        }

        operator fun set(key: Preferences.Key<Int>, value: Int) {
            preferences[key] = value
        }
    }
}
