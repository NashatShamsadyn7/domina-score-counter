package com.nashat.scorecounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nashat.scorecounter.data.SettingsDataStore
import com.nashat.scorecounter.data.GameDatabase
import com.nashat.scorecounter.data.GameRepository
import com.nashat.scorecounter.sound.SoundManager
import com.nashat.scorecounter.sound.VibrationManager
import com.nashat.scorecounter.ui.screens.MainScreen
import com.nashat.scorecounter.ui.theme.ScoreCounterTheme
import com.nashat.scorecounter.viewmodel.GameViewModel
import com.nashat.scorecounter.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager
    private lateinit var vibrationManager: VibrationManager

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

        setContent {
            val settings by viewModel.settings.collectAsState()

            ScoreCounterTheme(themeMode = settings.themeMode, fontSize = settings.fontSize) {
                MainScreen(
                    viewModel = viewModel,
                    soundManager = soundManager,
                    vibrationManager = vibrationManager,
                    settings = settings
                )
            }
        }
    }

    override fun onDestroy() {
        soundManager.release()
        super.onDestroy()
    }
}
