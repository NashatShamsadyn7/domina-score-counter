package com.nashat.scorecounter.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nashat.scorecounter.model.AppLanguage
import com.nashat.scorecounter.model.AppSettings
import com.nashat.scorecounter.model.ThemeMode
import com.nashat.scorecounter.ui.theme.LocalDominaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: AppSettings,
    text: UiText,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onVibrationChanged: (Boolean) -> Unit,
    onAnimationChanged: (Boolean) -> Unit,
    onCelebrationChanged: (Boolean) -> Unit,
    onVoiceChanged: (Boolean) -> Unit,
    onFontSizeChanged: (Int) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text.settingsTitle, style = MaterialTheme.typography.titleLarge)

            SectionCard {
                Text(text.theme, style = MaterialTheme.typography.titleMedium)
                ThemeMode.entries.toList().forEach { mode: ThemeMode ->
                    SettingRadioRow(
                        label = when (mode) {
                            ThemeMode.LIGHT -> text.light
                            ThemeMode.DARK -> text.dark
                            ThemeMode.SYSTEM -> text.system
                        },
                        selected = settings.themeMode == mode,
                        onSelect = { onThemeSelected(mode) }
                    )
                }
            }

            SectionCard {
                SettingToggleRow(text.sound, settings.soundEnabled, onSoundChanged)
                SettingToggleRow(text.vibration, settings.vibrationEnabled, onVibrationChanged)
                SettingToggleRow(text.voice, settings.voiceEnabled, onVoiceChanged)
                SettingToggleRow(text.animation, settings.animationEnabled, onAnimationChanged)
                SettingToggleRow(text.celebration, settings.celebrationEnabled, onCelebrationChanged)
                FontSizeRow(text.fontSize, settings.fontSize, onFontSizeChanged)
            }

            SectionCard {
                Text(text.language, style = MaterialTheme.typography.titleMedium)
                AppLanguage.entries.toList().forEach { language: AppLanguage ->
                    SettingRadioRow(
                        label = when (language) {
                            AppLanguage.KURDISH -> text.kurdish
                            AppLanguage.ARABIC -> text.arabic
                            AppLanguage.ENGLISH -> text.english
                        },
                        selected = settings.language == language,
                        onSelect = { onLanguageSelected(language) }
                    )
                }
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text.cancel)
            }
        }
    }
}

@Composable
private fun FontSizeRow(label: String, value: Int, onValueChange: (Int) -> Unit) {
    val haptic = LocalHapticFeedback.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label)
            Text(value.toString(), color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value.toFloat().coerceIn(12f, 32f),
            onValueChange = { onValueChange(it.toInt().coerceIn(12, 32)) },
            onValueChangeFinished = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            valueRange = 12f..32f
        )
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalDominaColors.current
    Card(
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, colors.cardBorder.copy(alpha = 0.45f)),
        colors = CardDefaults.cardColors(containerColor = colors.cardSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun SettingToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onCheckedChange(!checked)
            }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(
            checked = checked,
            onCheckedChange = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onCheckedChange(it)
            }
        )
    }
}

@Composable
private fun SettingRadioRow(label: String, selected: Boolean, onSelect: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSelect()
            })
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onSelect()
        })
        Text(label)
    }
}
