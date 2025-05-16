package com.panopoker.ui.mesa.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.delay

@Composable
fun CartaComAnimacaoFlip(
    frenteResId: Int,
    delayMs: Int,
    startTrigger: Boolean,
    tamanho: Dp
) {
    val rotation = remember(startTrigger) { Animatable(180f) }
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
                val scale = if (rotation.value <= 90f) 1.2f else 1f
                scaleX = scale
                scaleY = scale
            }
    ) {
        if (rotation.value <= 90f) {
            CartaFrente(frenteResId)
        } else {
            CartaVerso()
        }
    }
}

@Composable
private fun CartaFrente(frenteResId: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = frenteResId),
            contentDescription = "Carta Frente",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
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
