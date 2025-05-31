package com.panopoker.ui.mesa.components

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.panopoker.model.CartasComunitarias
import com.panopoker.ui.utils.getCartaDrawable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.BoxWithConstraints
import com.panopoker.model.CartaGlowInfo
import com.panopoker.ui.utils.glowEffect
import kotlinx.coroutines.delay
import com.panopoker.R

@Composable
fun CartasComunitarias(
    cartas: CartasComunitarias?,
    context: Context,
    cartasGlow: List<CartaGlowInfo> = emptyList()
) {
    cartas?.let {
        val todas = mutableListOf<String>()
        if (it.flop.isNotEmpty()) todas.addAll(it.flop)
        if (!it.turn.isNullOrBlank()) todas.add(it.turn)
        if (!it.river.isNullOrBlank()) todas.add(it.river)

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val cartaLargura = this.maxWidth * 0.065f
            val espacamento = this.maxWidth * 0.008f

            Row(
                modifier = Modifier
                    .offset(x = maxWidth * -0.02f, y = (-7).dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(espacamento)
            ) {
                todas.forEachIndexed { idx, carta ->
                    val frenteId = getCartaDrawable(context, carta)
                    val isVencedora = cartasGlow.any { it.carta == carta && it.indice == idx }

                    CartaAnimadaComposable(
                        frenteResId = frenteId,
                        versoResId = R.drawable.carta_back, // 👉 Cria esse drawable
                        largura = cartaLargura,
                        isGlow = isVencedora,
                        delayMs = 300L * idx
                    )
                }
            }
        }
    }
}

@Composable
fun CartaAnimadaComposable(
    frenteResId: Int,
    versoResId: Int,
    largura: androidx.compose.ui.unit.Dp,
    isGlow: Boolean = false,
    delayMs: Long = 0L
) {
    var virada by remember { mutableStateOf(false) }
    val rotacaoY by animateFloatAsState(
        targetValue = if (virada) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "flip"
    )
    val mostrarFrente = rotacaoY > 90f
    val density = LocalDensity.current.density

    LaunchedEffect(Unit) {
        delay(delayMs)
        virada = true
    }

    Box(
        modifier = Modifier
            .width(largura)
            .aspectRatio(0.68f)
            .clip(RoundedCornerShape(6.dp))
            .graphicsLayer {
                rotationY = rotacaoY
                cameraDistance = 12 * density
                scaleX = if (rotacaoY > 90f) -1f else 1f
            }
    ) {
        // Carta
        Image(
            painter = painterResource(if (mostrarFrente) frenteResId else versoResId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            contentScale = ContentScale.Crop
        )

        // Glow por cima
        if (isGlow) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .glowEffect()
            )
        }
    }
}
