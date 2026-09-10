package com.nashat.scorecounter.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nashat.scorecounter.model.AppLanguage
import com.nashat.scorecounter.model.AppSettings
import com.nashat.scorecounter.model.GameMode
import com.nashat.scorecounter.sound.SoundManager
import com.nashat.scorecounter.sound.VibrationManager
import com.nashat.scorecounter.sound.VoiceAnnouncer
import com.nashat.scorecounter.ui.components.CustomScoreDialog
import com.nashat.scorecounter.ui.components.GameModeSheet
import com.nashat.scorecounter.ui.components.ScorePickerSheet
import com.nashat.scorecounter.ui.components.SettingsSheet
import com.nashat.scorecounter.ui.components.gameModeLabel
import com.nashat.scorecounter.ui.components.uiText
import com.nashat.scorecounter.ui.theme.LocalDominaColors
import com.nashat.scorecounter.ui.theme.PrimaryIndigo
import com.nashat.scorecounter.ui.theme.ResetRed
import com.nashat.scorecounter.viewmodel.GameViewModel
import com.nashat.scorecounter.viewmodel.GameUiState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    viewModel: GameViewModel,
    soundManager: SoundManager,
    vibrationManager: VibrationManager,
    voiceAnnouncer: VoiceAnnouncer,
    settings: AppSettings
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val text = uiText(settings.language)
    val colors = LocalDominaColors.current
    var scoreAction by remember { mutableStateOf<Pair<Int, Boolean>?>(null) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showModeSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var pendingMode by remember { mutableStateOf<GameMode?>(null) }
    val differencePulse = remember { Animatable(1f) }
    var previousDifference by remember { mutableIntStateOf(0) }

    val isRtl = settings.language != AppLanguage.ENGLISH

    LaunchedEffect(settings.language) {
        voiceAnnouncer.applyLanguage(settings.language)
    }

    LaunchedEffect(uiState.players.map { it.score }) {
        if (uiState.gameMode == GameMode.TWO_TEAMS && uiState.players.size >= 2) {
            val diff = kotlin.math.abs(uiState.players[0].score - uiState.players[1].score)
            if (diff != previousDifference) {
                previousDifference = diff
                if (settings.animationEnabled) {
                    differencePulse.snapTo(1f)
                    differencePulse.animateTo(1.05f, tween(140))
                    differencePulse.animateTo(1f, tween(180))
                }
            }
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Scaffold(
            containerColor = colors.background,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showSettingsSheet = true
                    },
                    containerColor = PrimaryIndigo,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Rounded.Settings, contentDescription = null, tint = Color.White)
                }
            }
        ) { padding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                val compactLayout = maxHeight < 760.dp || (uiState.gameMode == GameMode.FOUR_PLAYERS && maxHeight < 880.dp)
                val spacing = if (compactLayout) 10.dp else 16.dp
                val buttonHeight = if (compactLayout) 48.dp else 56.dp
                val ambientMotion = rememberInfiniteTransition(label = "ambient-motion")
                val orbShift = ambientMotion.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "orb-shift"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(colors.gradientTop, colors.gradientMid, colors.gradientBottom)
                            )
                        ),
                ) {
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        drawCircle(
                            color = PrimaryIndigo.copy(alpha = colors.orbAlpha1),
                            radius = size.minDimension * 0.22f,
                            center = androidx.compose.ui.geometry.Offset(
                                x = size.width * (0.2f + 0.08f * orbShift.value),
                                y = size.height * 0.14f
                            )
                        )
                        drawCircle(
                            color = Color(0xFF12A56A).copy(alpha = colors.orbAlpha2),
                            radius = size.minDimension * 0.18f,
                            center = androidx.compose.ui.geometry.Offset(
                                x = size.width * (0.78f - 0.05f * orbShift.value),
                                y = size.height * 0.4f
                            )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        if (settings.animationEnabled) {
                            AnimatedContent(
                                targetState = uiState.gameMode,
                                transitionSpec = {
                                    (slideInHorizontally { it / 3 } + fadeIn() + scaleIn(initialScale = 0.96f)).togetherWith(
                                        slideOutHorizontally { -it / 5 } + fadeOut() + scaleOut(targetScale = 0.98f)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                label = "mode-content"
                            ) { mode ->
                                ModeContent(
                                    mode = mode,
                                    uiState = uiState,
                                    hintText = text.scoreHint,
                                    pointsLabel = text.pointsLabel,
                                    appFontSize = settings.fontSize,
                                    differenceLabel = text.difference,
                                    differenceScale = differencePulse.value,
                                    animationEnabled = true,
                                    celebrationEnabled = settings.celebrationEnabled,
                                    compactLayout = compactLayout,
                                    modifier = Modifier.fillMaxSize(),
                                    onNameChange = viewModel::updateName,
                                    onAddClick = { scoreAction = it to true },
                                    onSubtractClick = { scoreAction = it to false },
                                    onUndoClick = viewModel::undo,
                                    onRedoClick = viewModel::redo
                                )
                            }
                        } else {
                            ModeContent(
                                mode = uiState.gameMode,
                                uiState = uiState,
                                hintText = text.scoreHint,
                                pointsLabel = text.pointsLabel,
                                appFontSize = settings.fontSize,
                                differenceLabel = text.difference,
                                differenceScale = 1f,
                                animationEnabled = false,
                                celebrationEnabled = settings.celebrationEnabled,
                                compactLayout = compactLayout,
                                modifier = Modifier.weight(1f),
                                onNameChange = viewModel::updateName,
                                onAddClick = { scoreAction = it to true },
                                onSubtractClick = { scoreAction = it to false },
                                onUndoClick = viewModel::undo,
                                onRedoClick = viewModel::redo
                            )
                        }

                        if (uiState.gameMode != GameMode.TWO_TEAMS) {
                            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.resetToDefaultMode()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(buttonHeight),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryIndigo,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Rounded.Home, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text.backToMode)
                                }
                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showResetDialog = true
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(buttonHeight),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ResetRed,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text.resetAll)
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showResetDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(buttonHeight),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ResetRed,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Rounded.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text.resetAll)
                            }
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showModeSheet = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(buttonHeight),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryIndigo,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Rounded.Tune, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(gameModeLabel(uiState.gameMode, settings.language))
                        }

                        Text(
                            text = text.developer,
                            fontStyle = FontStyle.Italic,
                            fontSize = if (compactLayout) 14.sp else 18.sp,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(bottom = 72.dp)
                        )
                    }
                }
            }
        }

        scoreAction?.takeIf { !showCustomDialog }?.let { (playerId, isAdd) ->
            ScorePickerSheet(
                title = text.choosePoints,
                customLabel = text.customPoints,
                cancelLabel = text.cancel,
                onDismiss = { scoreAction = null },
                onPick = { value ->
                    applyScoreChange(
                        playerId = playerId,
                        delta = if (isAdd) value else -value,
                        uiState = uiState,
                        viewModel = viewModel,
                        soundManager = soundManager,
                        vibrationManager = vibrationManager,
                        voiceAnnouncer = voiceAnnouncer,
                        soundEnabled = settings.soundEnabled,
                        vibrationEnabled = settings.vibrationEnabled,
                        voiceEnabled = settings.voiceEnabled,
                        language = settings.language
                    )
                    scoreAction = null
                },
                onCustom = { showCustomDialog = true }
            )
        }

        if (showCustomDialog && scoreAction != null) {
            val action = scoreAction!!
            CustomScoreDialog(
                title = text.customPoints,
                inputLabel = text.enterPoints,
                confirmLabel = text.confirm,
                cancelLabel = text.cancel,
                onDismiss = { showCustomDialog = false },
                onConfirm = { value ->
                    applyScoreChange(
                        playerId = action.first,
                        delta = if (action.second) value else -value,
                        uiState = uiState,
                        viewModel = viewModel,
                        soundManager = soundManager,
                        vibrationManager = vibrationManager,
                        voiceAnnouncer = voiceAnnouncer,
                        soundEnabled = settings.soundEnabled,
                        vibrationEnabled = settings.vibrationEnabled,
                        voiceEnabled = settings.voiceEnabled,
                        language = settings.language
                    )
                    showCustomDialog = false
                    scoreAction = null
                }
            )
        }

        if (showModeSheet) {
            GameModeSheet(
                language = settings.language,
                title = text.modeTitle,
                cancelLabel = text.cancel,
                onDismiss = { showModeSheet = false },
                onSelect = { mode ->
                    showModeSheet = false
                    if (uiState.players.any { it.score != 0 }) {
                        pendingMode = mode
                    } else {
                        viewModel.selectMode(mode)
                    }
                }
            )
        }

        if (showSettingsSheet) {
            SettingsSheet(
                settings = settings,
                text = text,
                onDismiss = { showSettingsSheet = false },
                onThemeSelected = viewModel::setTheme,
                onSoundChanged = viewModel::setSound,
                onVibrationChanged = viewModel::setVibration,
                onAnimationChanged = viewModel::setAnimation,
                onCelebrationChanged = viewModel::setCelebration,
                onVoiceChanged = viewModel::setVoice,
                onFontSizeChanged = viewModel::setFontSize,
                onLanguageSelected = viewModel::setLanguage
            )
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(text.resetConfirmTitle) },
                text = { Text(text.resetConfirmBody) },
                confirmButton = {
                    Button(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.resetAll()
                        showResetDialog = false
                    }) {
                        Text(text.confirm)
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showResetDialog = false
                    }) {
                        Text(text.cancel)
                    }
                }
            )
        }

        pendingMode?.let { mode ->
            AlertDialog(
                onDismissRequest = { pendingMode = null },
                title = { Text(text.modeSwitchTitle) },
                text = { Text(text.modeSwitchBody) },
                confirmButton = {
                    Button(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.selectMode(mode)
                        pendingMode = null
                    }) {
                        Text(text.confirm)
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        pendingMode = null
                    }) {
                        Text(text.cancel)
                    }
                }
            )
        }
    }
}

@Composable
private fun ModeContent(
    mode: GameMode,
    uiState: GameUiState,
    hintText: String,
    pointsLabel: String,
    appFontSize: Int,
    differenceLabel: String,
    differenceScale: Float,
    animationEnabled: Boolean,
    celebrationEnabled: Boolean,
    compactLayout: Boolean,
    modifier: Modifier = Modifier,
    onNameChange: (Int, String) -> Unit,
    onAddClick: (Int) -> Unit,
    onSubtractClick: (Int) -> Unit,
    onUndoClick: (Int) -> Unit,
    onRedoClick: (Int) -> Unit
) {
    when (mode) {
        GameMode.TWO_TEAMS -> TwoTeamScreen(
            modifier = modifier,
            players = uiState.players,
            hintText = hintText,
            pointsLabel = pointsLabel,
            appFontSize = appFontSize,
            differenceLabel = differenceLabel,
            animatedDifferenceScale = differenceScale,
            animationEnabled = animationEnabled,
            celebrationEnabled = celebrationEnabled,
            compactLayout = compactLayout,
            onNameChange = onNameChange,
            onAddClick = onAddClick,
            onSubtractClick = onSubtractClick,
            onUndoClick = onUndoClick,
            onRedoClick = onRedoClick
        )

        GameMode.THREE_PLAYERS -> ThreePlayerScreen(
            modifier = modifier,
            players = uiState.players,
            hintText = hintText,
            pointsLabel = pointsLabel,
            appFontSize = appFontSize,
            animationEnabled = animationEnabled,
            celebrationEnabled = celebrationEnabled,
            compactLayout = compactLayout,
            onNameChange = onNameChange,
            onAddClick = onAddClick,
            onSubtractClick = onSubtractClick,
            onUndoClick = onUndoClick,
            onRedoClick = onRedoClick
        )

        GameMode.FOUR_PLAYERS -> FourPlayerScreen(
            modifier = modifier,
            players = uiState.players,
            hintText = hintText,
            pointsLabel = pointsLabel,
            appFontSize = appFontSize,
            animationEnabled = animationEnabled,
            celebrationEnabled = celebrationEnabled,
            compactLayout = compactLayout,
            onNameChange = onNameChange,
            onAddClick = onAddClick,
            onSubtractClick = onSubtractClick,
            onUndoClick = onUndoClick,
            onRedoClick = onRedoClick
        )
    }
}

private fun applyScoreChange(
    playerId: Int,
    delta: Int,
    uiState: GameUiState,
    viewModel: GameViewModel,
    soundManager: SoundManager,
    vibrationManager: VibrationManager,
    voiceAnnouncer: VoiceAnnouncer,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    voiceEnabled: Boolean,
    language: AppLanguage
) {
    val player = uiState.players.firstOrNull { it.id == playerId }
    val effectiveDelta = viewModel.changeScore(playerId, delta)
    if (effectiveDelta == 0) return

    val playerName = player?.name ?: ""
    val newScore = (player?.score ?: 0) + effectiveDelta
    vibrationManager.vibrateForScore(newScore, effectiveDelta, vibrationEnabled)

    val useVoice = voiceEnabled && voiceAnnouncer.isReady && playerName.isNotBlank()
    if (useVoice) {
        // The spoken announcement replaces the value/milestone sounds so the
        // two never overlap; vibration and the visual celebration stay on.
        voiceAnnouncer.speak(
            voiceAnnouncer.announcementPhrase(
                value = kotlin.math.abs(effectiveDelta),
                name = playerName,
                language = language,
                subtract = effectiveDelta < 0
            )
        )
    } else {
        val milestonePlayed = if (delta > 0) {
            soundManager.playMilestone(newScore, soundEnabled)
        } else {
            false
        }
        if (!milestonePlayed) {
            if (effectiveDelta > 0) {
                soundManager.playAdd(playerId, effectiveDelta, soundEnabled)
            } else {
                soundManager.playSubtract(playerId, soundEnabled)
            }
        }
    }
}
