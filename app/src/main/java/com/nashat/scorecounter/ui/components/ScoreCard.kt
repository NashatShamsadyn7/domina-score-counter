package com.nashat.scorecounter.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nashat.scorecounter.model.PlayerState
import com.nashat.scorecounter.ui.theme.CardBorder
import com.nashat.scorecounter.ui.theme.CardSurface
import kotlinx.coroutines.launch

@Composable
fun ScoreCard(
    player: PlayerState,
    scoreColor: Color,
    minusColor: Color,
    hintText: String,
    pointsLabel: String,
    appFontSize: Int,
    animationEnabled: Boolean,
    celebrationEnabled: Boolean,
    compactLayout: Boolean,
    modifier: Modifier = Modifier,
    onNameChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onSubtractClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit
) {
    val contentPadding = if (compactLayout) 10.dp else 16.dp
    val spacing = if (compactLayout) 8.dp else 16.dp
    val buttonHeight = if (compactLayout) 46.dp else 58.dp
    val scoreFontSize = ((if (compactLayout) appFontSize + 10 else appFontSize + 20).coerceIn(14, 60)).sp
    var lastName by remember(player.id) { mutableStateOf(player.name) }
    var lastScore by remember(player.id) { mutableIntStateOf(player.score) }
    var showMilestone by remember(player.id) { mutableStateOf(false) }
    val nameScale = remember(player.id) { Animatable(1f) }
    val scorePulse = remember(player.id) { Animatable(1f) }
    val milestone = player.score.takeIf { it in MILESTONE_SCORES }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    var isNameFocused by remember(player.id) { mutableStateOf(false) }
    val flipRotation = remember(player.id) { Animatable(0f) }
    val cardLift by animateFloatAsState(
        targetValue = when {
            animationEnabled && isNameFocused -> 1.02f
            else -> 1f
        },
        label = "card-lift"
    )
    val fieldScale by animateFloatAsState(
        targetValue = if (animationEnabled && isNameFocused) 1.015f else 1f,
        label = "field-scale"
    )

    LaunchedEffect(player.name) {
        if (animationEnabled && player.name != lastName) {
            lastName = player.name
            nameScale.snapTo(1f)
            nameScale.animateTo(1.04f, spring(dampingRatio = 0.55f, stiffness = 280f))
            nameScale.animateTo(1f, spring(dampingRatio = 0.8f, stiffness = 320f))
        } else {
            lastName = player.name
        }
    }

    LaunchedEffect(player.score) {
        if (animationEnabled && player.score != lastScore) {
            lastScore = player.score
            scorePulse.snapTo(1f)
            scorePulse.animateTo(1.08f, spring(dampingRatio = 0.45f, stiffness = 240f))
            scorePulse.animateTo(1f, spring(dampingRatio = 0.8f, stiffness = 320f))

            if (milestone != null && celebrationEnabled) {
                showMilestone = true
                kotlinx.coroutines.delay(1150)
                showMilestone = false
            }
        } else {
            lastScore = player.score
        }
    }

    Card(
        modifier = modifier
            .shadow(if (compactLayout) 8.dp else 14.dp, RoundedCornerShape(20.dp), spotColor = scoreColor.copy(alpha = 0.22f))
            .scale(cardLift),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(2.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = if (compactLayout) 4.dp else 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.White, Color(0xFFF7F8FF), Color(0xFFEEF2FF))
                    )
                )
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = player.name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(nameScale.value * fieldScale)
                    .graphicsLayer {
                        rotationY = flipRotation.value
                        cameraDistance = 12f * density
                    }
                    .onFocusChanged { isNameFocused = it.isFocused },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        coroutineScope.launch {
                            if (animationEnabled) {
                                flipRotation.snapTo(0f)
                                flipRotation.animateTo(90f, tween(110))
                                flipRotation.snapTo(-90f)
                                flipRotation.animateTo(0f, tween(150))
                            }
                        }
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = scoreColor.copy(alpha = 0.9f),
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(spacing))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(18.dp), spotColor = scoreColor.copy(alpha = 0.16f))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White, scoreColor.copy(alpha = 0.05f), Color.White)
                        ),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(if (compactLayout) 10.dp else 16.dp),
                contentAlignment = Alignment.Center
            ) {
                val animatedScore by animateIntAsState(
                    targetValue = player.score,
                    animationSpec = if (animationEnabled) {
                        spring(dampingRatio = 0.8f, stiffness = 120f)
                    } else {
                        spring(dampingRatio = 1f, stiffness = 10_000f)
                    },
                    label = "score"
                )
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(2.dp, CardBorder),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {}
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = scoreColor.copy(alpha = if (celebrationEnabled) 0.08f else 0.04f),
                        radius = size.minDimension * 0.36f,
                        center = Offset(size.width * 0.5f, size.height * 0.4f)
                    )
                }
                Text(
                    text = animatedScore.toString(),
                    color = scoreColor,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = scoreFontSize),
                    modifier = Modifier.scale(scorePulse.value),
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = pointsLabel,
                    color = scoreColor.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = celebrationEnabled && showMilestone && milestone != null,
                    enter = fadeIn() + scaleIn(initialScale = 0.65f),
                    exit = fadeOut() + scaleOut(targetScale = 0.85f),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Card(
                        shape = RoundedCornerShape(999.dp),
                        colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "$milestone!",
                            color = scoreColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PressableActionButton(
                    modifier = Modifier.weight(1f),
                    buttonHeight = buttonHeight,
                    color = com.nashat.scorecounter.ui.theme.PlusGreen,
                    animationEnabled = animationEnabled,
                    onClick = onAddClick,
                    icon = { Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.White) }
                )
                PressableActionButton(
                    modifier = Modifier.weight(1f),
                    buttonHeight = buttonHeight,
                    color = minusColor,
                    animationEnabled = animationEnabled,
                    onClick = onSubtractClick,
                    icon = { Icon(Icons.Rounded.Remove, contentDescription = null, tint = Color.White) }
                )
            }

            Spacer(modifier = Modifier.height(if (compactLayout) 6.dp else 10.dp))

            Text(
                text = hintText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )

            Spacer(modifier = Modifier.height(if (compactLayout) 6.dp else 10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallIconAction(
                    modifier = Modifier.weight(1f),
                    compactLayout = compactLayout,
                    enabled = player.undoStack.isNotEmpty(),
                    onClick = onUndoClick,
                    icon = { Icon(Icons.AutoMirrored.Rounded.Undo, contentDescription = null) }
                )
                SmallIconAction(
                    modifier = Modifier.weight(1f),
                    compactLayout = compactLayout,
                    enabled = player.redoStack.isNotEmpty(),
                    onClick = onRedoClick,
                    icon = { Icon(Icons.AutoMirrored.Rounded.Redo, contentDescription = null) }
                )
            }
        }
    }
}

@Composable
private fun PressableActionButton(
    modifier: Modifier = Modifier,
    buttonHeight: androidx.compose.ui.unit.Dp,
    color: Color,
    animationEnabled: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val interactionSource = MutableInteractionSource()
    val pressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current
    androidx.compose.material3.Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .height(buttonHeight)
            .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = color.copy(alpha = 0.25f))
            .scale(if (animationEnabled && pressed) 0.94f else 1f),
        shape = RoundedCornerShape(14.dp),
        interactionSource = interactionSource,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = color),
        elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        icon()
    }
}

@Composable
private fun SmallIconAction(
    modifier: Modifier = Modifier,
    compactLayout: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color(0xFF50C8D7) else Color(0xFFE4E8F7)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
                enabled = enabled,
                modifier = Modifier.size(if (compactLayout) 36.dp else 46.dp)
            ) {
                icon()
            }
        }
    }
}

private val MILESTONE_SCORES = (50..550 step 50).toSet()
