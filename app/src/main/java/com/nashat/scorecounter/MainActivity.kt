package com.nashat.scorecounter

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nashat.scorecounter.data.SettingsDataStore
import com.nashat.scorecounter.data.GameDatabase
import com.nashat.scorecounter.data.GameRepository
import com.nashat.scorecounter.sound.SoundManager
import com.nashat.scorecounter.sound.VibrationManager
import com.nashat.scorecounter.sound.VoiceAnnouncer
import com.nashat.scorecounter.ui.screens.MainScreen
import com.nashat.scorecounter.ui.theme.LocalDarkTheme
import com.nashat.scorecounter.ui.theme.ScoreCounterTheme
import com.nashat.scorecounter.viewmodel.GameViewModel
import com.nashat.scorecounter.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager
    private lateinit var vibrationManager: VibrationManager
    private lateinit var voiceAnnouncer: VoiceAnnouncer

    private val viewModel: GameViewModel by viewModels {
        GameViewModelFactory(
            settingsDataStore = SettingsDataStore(applicationContext),
            gameRepository = GameRepository(GameDatabase.getInstance(applicationContext).gameSessionDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        soundManager = SoundManager(this)
        vibrationManager = VibrationManager(this)
        voiceAnnouncer = VoiceAnnouncer(this)

        setContent {
            val settings by viewModel.settings.collectAsState()

            ScoreCounterTheme(themeMode = settings.themeMode, fontSize = settings.fontSize) {
                val darkTheme = LocalDarkTheme.current
                SideEffect {
                    val style = if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                    enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                }
                MainScreen(
                    viewModel = viewModel,
                    soundManager = soundManager,
                    vibrationManager = vibrationManager,
                    voiceAnnouncer = voiceAnnouncer,
                    settings = settings
                )
            }
        }
    }

    override fun onDestroy() {
        voiceAnnouncer.release()
        soundManager.release()
        super.onDestroy()
    }
}
