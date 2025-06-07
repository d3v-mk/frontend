package com.panopoker.ui.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.composed

fun Modifier.glowEffect(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "glowEffectTransition")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f, // 👈 antes era 0.3f
        targetValue = 0.4f,   // 👈 antes era 0.9f
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    this
        .graphicsLayer {
            shadowElevation = 16f * alpha
            shape = RoundedCornerShape(6.dp)
            clip = true
        }
        .background(
            color = Color.Yellow.copy(alpha = alpha),
            shape = RoundedCornerShape(6.dp)
        )
}
