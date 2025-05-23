package com.panopoker.ui.lobby.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

@Composable
fun NewsMarquee(
    mensagens: List<String>,
    modifier: Modifier = Modifier,
    corTexto: Color = Color(0xFFFFC300)
) {
    var idx by remember { mutableStateOf(0) }
    var textWidth by remember { mutableStateOf(0f) }
    var boxWidth by remember { mutableStateOf(0f) }
    var readyToShow by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .clipToBounds()
            .background(Color.Black)
            .height(36.dp)
            .fillMaxWidth()
            .onGloballyPositioned { boxWidth = it.size.width.toFloat() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .alpha(if (readyToShow) 1f else 0f)
                .horizontalScroll(scrollState, enabled = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(with(density) { boxWidth.toDp() }))
            Text(
                text = mensagens[idx],
                fontSize = 16.sp,
                color = corTexto,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.onGloballyPositioned {
                    textWidth = it.size.width.toFloat()
                }
            )
            Spacer(Modifier.width(with(density) { boxWidth.toDp() }))
        }
    }///

    LaunchedEffect(Unit) {
        while (true) {
            // 1) reset e espera layout
            readyToShow = false
            snapshotFlow { boxWidth }.filter { it > 0f }.first()
            scrollState.scrollTo(0)

            // 2) espera o texto medido e mostra
            snapshotFlow { textWidth }.filter { it > 0f }.first()
            readyToShow = true

            // 3) pega total de scroll = maxValue
            val total = snapshotFlow { scrollState.maxValue }
                .filter { it > 0 }
                .first()
                .toFloat()

            // 4) anima ~60fps
            val duration = 9_000L
            val frame    = 16L
            val steps    = (duration / frame).toInt().coerceAtLeast(1)
            val delta    = total / steps

            repeat(steps) {
                scrollState.scrollBy(delta)
                delay(frame)
            }

            // 5) corrige o arredondamento: joga o resto que sobrou
            val scrolled = delta * steps
            val remainder = total - scrolled
            if (remainder > 0f) scrollState.scrollBy(remainder)

            // 6) pausa e troca mensagem
            //delay(10)
            idx = (idx + 1) % mensagens.size
        }
    }

}/////
