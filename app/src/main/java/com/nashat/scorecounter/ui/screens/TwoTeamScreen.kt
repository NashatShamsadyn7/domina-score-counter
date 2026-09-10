package com.nashat.scorecounter.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nashat.scorecounter.model.PlayerState
import com.nashat.scorecounter.ui.components.ScoreCard
import com.nashat.scorecounter.ui.theme.DifferenceRed
import com.nashat.scorecounter.ui.theme.DifferenceRedDark
import com.nashat.scorecounter.ui.theme.LocalDarkTheme
import com.nashat.scorecounter.ui.theme.LocalDominaColors
import com.nashat.scorecounter.ui.theme.MinusTeal
import com.nashat.scorecounter.ui.theme.MinusTealDark
import com.nashat.scorecounter.ui.theme.PlusGreen
import com.nashat.scorecounter.ui.theme.PlusGreenDark
import com.nashat.scorecounter.ui.theme.ScoreGreen
import com.nashat.scorecounter.ui.theme.ScoreGreenDark
import com.nashat.scorecounter.ui.theme.ScoreTeal
import com.nashat.scorecounter.ui.theme.ScoreTealDark

@Composable
fun TwoTeamScreen(
    players: List<PlayerState>,
    hintText: String,
    pointsLabel: String,
    appFontSize: Int,
    differenceLabel: String,
    animatedDifferenceScale: Float,
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
    val colors = LocalDominaColors.current
    val darkTheme = LocalDarkTheme.current
    val scoreColorFirst = if (darkTheme) ScoreGreenDark else ScoreGreen
    val scoreColorSecond = if (darkTheme) ScoreTealDark else ScoreTeal
    val minusColor = if (darkTheme) MinusTealDark else MinusTeal
    val plusColor = if (darkTheme) PlusGreenDark else PlusGreen
    val differenceColor = if (darkTheme) DifferenceRedDark else DifferenceRed
    val spacing = if (compactLayout) 10.dp else 16.dp
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            players.take(2).forEachIndexed { index, player ->
                ScoreCard(
                    player = player,
                    scoreColor = if (index == 0) scoreColorFirst else scoreColorSecond,
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
                    onNameChange = { onNameChange(player.id, it) },
                    onAddClick = { onAddClick(player.id) },
                    onSubtractClick = { onSubtractClick(player.id) },
                    onUndoClick = { onUndoClick(player.id) },
                    onRedoClick = { onRedoClick(player.id) }
                )
            }
        }

        if (players.size >= 2) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(animatedDifferenceScale),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, colors.cardBorder),
                colors = CardDefaults.cardColors(containerColor = colors.cardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(if (compactLayout) 12.dp else 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = differenceLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = kotlin.math.abs(players[0].score - players[1].score).toString(),
                        color = differenceColor,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
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
