package com.panopoker.ui.mesa.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.panopoker.R
import com.panopoker.ui.utils.glowEffect
import kotlinx.coroutines.delay

@Composable
fun CartaComAnimacaoFlip(
    frenteResId: Int,
    delayMs: Int,
    startTrigger: Boolean,
    tamanho: Dp,
    brilhar: Boolean = false // ✨ novo param
) {
    val rotation = remember(startTrigger) { Animatable(if (startTrigger) 180f else 0f) }
    val cameraDistance = 12 * LocalContext.current.resources.displayMetrics.density

    LaunchedEffect(startTrigger, frenteResId, delayMs) {
        if (startTrigger) {
            delay(delayMs.toLong())
            rotation.animateTo(0f, tween(1500))
        }
    }

    Box(
        modifier = Modifier
            .width(tamanho)
            .aspectRatio(0.68f)
            .graphicsLayer {
                rotationY = rotation.value
                this.cameraDistance = cameraDistance
            }
    ) {
        if (!startTrigger || rotation.value > 90f) {
            CartaVerso()
        } else {
            CartaFrente(frenteResId, brilhar) // ✨ envia o glow
        }
    }
}

@Composable
private fun CartaFrente(frenteResId: Int, brilhar: Boolean) {
    // Anima o alpha só se precisar brilhar
    val infiniteTransition = rememberInfiniteTransition(label = "glowTop")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnimTop"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
    ) {
        // Imagem da carta
        Image(
            painter = painterResource(id = frenteResId),
            contentDescription = "Carta Frente",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Glow por cima com animação
        if (brilhar) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = Color.Yellow.copy(alpha = alpha),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}



@Composable
private fun CartaVerso() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .graphicsLayer { rotationY = 180f }
    ) {
        Image(
            painter = painterResource(id = R.drawable.carta_back),
            contentDescription = "Carta Verso",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}
