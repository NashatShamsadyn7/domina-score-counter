package com.nashat.scorecounter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nashat.scorecounter.model.AppLanguage
import com.nashat.scorecounter.model.GameMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameModeSheet(
    language: AppLanguage,
    title: String,
    cancelLabel: String,
    onDismiss: () -> Unit,
    onSelect: (GameMode) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            GameMode.entries.forEach { mode ->
                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSelect(mode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(gameModeLabel(mode, language), modifier = Modifier.fillMaxWidth())
                }
            }
            TextButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onDismiss()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(cancelLabel)
            }
        }
    }
}
