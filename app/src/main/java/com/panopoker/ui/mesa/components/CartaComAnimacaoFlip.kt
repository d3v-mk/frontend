package com.panopoker.ui.mesa.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.panopoker.R
import kotlinx.coroutines.delay
import androidx.compose.ui.res.painterResource


@Composable
fun CartaComAnimacaoFlip(
    frenteResId: Int,
    delayMs: Int,
    startTrigger: Boolean
) {
    val rotation = remember { Animatable(180f) }
    val cameraDistance = 12 * LocalContext.current.resources.displayMetrics.density

    val largura by remember { derivedStateOf { if (rotation.value <= 90f) 33.dp else 20.dp } }
    val altura by remember { derivedStateOf { if (rotation.value <= 90f) 48.dp else 30.dp } }

    LaunchedEffect(startTrigger) {
        if (startTrigger) {
            delay(delayMs.toLong())
            rotation.animateTo(0f, tween(1500))
        }
    }

    Box(
        modifier = Modifier
            .width(largura)
            .height(altura)
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
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = frenteResId),
            contentDescription = "Carta Frente",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CartaVerso() {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationY = 180f }
    ) {
        Image(
            painter = painterResource(id = R.drawable.carta_back),
            contentDescription = "Carta Verso",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}
