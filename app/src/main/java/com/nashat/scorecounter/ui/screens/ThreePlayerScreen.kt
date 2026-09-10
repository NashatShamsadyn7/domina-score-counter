package com.nashat.scorecounter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nashat.scorecounter.model.PlayerState
import com.nashat.scorecounter.ui.components.ScoreCard
import com.nashat.scorecounter.ui.theme.MinusRed
import com.nashat.scorecounter.ui.theme.ScoreAmber
import com.nashat.scorecounter.ui.theme.ScoreGreen
import com.nashat.scorecounter.ui.theme.ScorePurple

@Composable
fun ThreePlayerScreen(
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
    if (players.size >= 3) {
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
                    scoreColor = ScorePurple,
                    minusColor = MinusRed,
                    hintText = hintText,
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
                    scoreColor = ScoreAmber,
                    minusColor = MinusRed,
                    hintText = hintText,
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
            ScoreCard(
                player = players[2],
                scoreColor = ScoreGreen,
                minusColor = MinusRed,
                hintText = hintText,
                pointsLabel = pointsLabel,
                appFontSize = appFontSize,
                animationEnabled = animationEnabled,
                celebrationEnabled = celebrationEnabled,
                compactLayout = compactLayout,
                modifier = Modifier.weight(1f),
                onNameChange = { onNameChange(players[2].id, it) },
                onAddClick = { onAddClick(players[2].id) },
                onSubtractClick = { onSubtractClick(players[2].id) },
                onUndoClick = { onUndoClick(players[2].id) },
                onRedoClick = { onRedoClick(players[2].id) }
            )
        }
    }
}
