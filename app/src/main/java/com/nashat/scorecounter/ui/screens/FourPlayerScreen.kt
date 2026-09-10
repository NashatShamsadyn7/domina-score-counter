package com.nashat.scorecounter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nashat.scorecounter.model.PlayerState
import com.nashat.scorecounter.ui.components.ScoreCard
import com.nashat.scorecounter.ui.theme.LocalDarkTheme
import com.nashat.scorecounter.ui.theme.LocalDominaColors
import com.nashat.scorecounter.ui.theme.MinusRed
import com.nashat.scorecounter.ui.theme.MinusRedDark
import com.nashat.scorecounter.ui.theme.PlusGreen
import com.nashat.scorecounter.ui.theme.PlusGreenDark
import com.nashat.scorecounter.ui.theme.ScoreAmber
import com.nashat.scorecounter.ui.theme.ScoreAmberDark
import com.nashat.scorecounter.ui.theme.ScoreGreen
import com.nashat.scorecounter.ui.theme.ScoreGreenDark
import com.nashat.scorecounter.ui.theme.ScorePurple
import com.nashat.scorecounter.ui.theme.ScorePurpleDark
import com.nashat.scorecounter.ui.theme.ScoreTeal
import com.nashat.scorecounter.ui.theme.ScoreTealDark

@Composable
fun FourPlayerScreen(
    players: List<PlayerState>,
    hintText: String,
    pointsLabel: String,
    appFontSize: Int,
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
    if (players.size >= 4) {
        val colors = LocalDominaColors.current
        val darkTheme = LocalDarkTheme.current
        val minusColor = if (darkTheme) MinusRedDark else MinusRed
        val plusColor = if (darkTheme) PlusGreenDark else PlusGreen
        val spacing = if (compactLayout) 10.dp else 16.dp
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                ScoreCard(
                    player = players[0],
                    scoreColor = if (darkTheme) ScorePurpleDark else ScorePurple,
                    minusColor = minusColor,
                    plusColor = plusColor,
                    pointsLabel = pointsLabel,
                    appFontSize = appFontSize,
                    animationEnabled = animationEnabled,
                    celebrationEnabled = celebrationEnabled,
                    compactLayout = compactLayout,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onNameChange = { onNameChange(players[0].id, it) },
                    onAddClick = { onAddClick(players[0].id) },
                    onSubtractClick = { onSubtractClick(players[0].id) },
                    onUndoClick = { onUndoClick(players[0].id) },
                    onRedoClick = { onRedoClick(players[0].id) }
                )
                ScoreCard(
                    player = players[1],
                    scoreColor = if (darkTheme) ScoreAmberDark else ScoreAmber,
                    minusColor = minusColor,
                    plusColor = plusColor,
                    pointsLabel = pointsLabel,
                    appFontSize = appFontSize,
                    animationEnabled = animationEnabled,
                    celebrationEnabled = celebrationEnabled,
                    compactLayout = compactLayout,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onNameChange = { onNameChange(players[1].id, it) },
                    onAddClick = { onAddClick(players[1].id) },
                    onSubtractClick = { onSubtractClick(players[1].id) },
                    onUndoClick = { onUndoClick(players[1].id) },
                    onRedoClick = { onRedoClick(players[1].id) }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                ScoreCard(
                    player = players[2],
                    scoreColor = if (darkTheme) ScoreGreenDark else ScoreGreen,
                    minusColor = minusColor,
                    plusColor = plusColor,
                    pointsLabel = pointsLabel,
                    appFontSize = appFontSize,
                    animationEnabled = animationEnabled,
                    celebrationEnabled = celebrationEnabled,
                    compactLayout = compactLayout,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onNameChange = { onNameChange(players[2].id, it) },
                    onAddClick = { onAddClick(players[2].id) },
                    onSubtractClick = { onSubtractClick(players[2].id) },
                    onUndoClick = { onUndoClick(players[2].id) },
                    onRedoClick = { onRedoClick(players[2].id) }
                )
                ScoreCard(
                    player = players[3],
                    scoreColor = if (darkTheme) ScoreTealDark else ScoreTeal,
                    minusColor = minusColor,
                    plusColor = plusColor,
                    pointsLabel = pointsLabel,
                    appFontSize = appFontSize,
                    animationEnabled = animationEnabled,
                    celebrationEnabled = celebrationEnabled,
                    compactLayout = compactLayout,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onNameChange = { onNameChange(players[3].id, it) },
                    onAddClick = { onAddClick(players[3].id) },
                    onSubtractClick = { onSubtractClick(players[3].id) },
                    onUndoClick = { onUndoClick(players[3].id) },
                    onRedoClick = { onRedoClick(players[3].id) }
                )
            }
            Text(
                text = hintText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
