package com.panopoker.ui.lobby.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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

@Composable
fun NewsMarquee(
    adminMsg: String,
    latestEvent: String,
    modifier: Modifier = Modifier,
    corTexto: Color = Color(0xFFFFC300)
) {
    val defaultMsg = "🔥 Bem-vindo ao PanoPoker, Lenda! insta: @panopoker 🔥"
    val fixedAdmin = adminMsg.takeIf { it.isNotBlank() } ?: defaultMsg
    val fixedEvent = latestEvent.takeIf { it.isNotBlank() } ?: defaultMsg
    val lista      = listOf(fixedAdmin, fixedEvent)

    var idx by remember { mutableStateOf(0) }
    var textWidth by remember { mutableStateOf(0f) }
    var boxWidth by remember { mutableStateOf(0f) }
    var readyToShow by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    // recria scrollState zerado sempre que mudar de mensagem
    val scrollState = remember(idx) { ScrollState(0) }

    // voltar ao primeiro ao trocar lista
    LaunchedEffect(lista) { idx = 0 }

    Box(
        modifier = modifier
            .clipToBounds()
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
                text = lista[idx],
                fontSize = 16.sp,
                color = corTexto,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.onGloballyPositioned {
                    textWidth = it.size.width.toFloat()
                }
            )
            Spacer(Modifier.width(with(density) { boxWidth.toDp() }))
        }
    }

    LaunchedEffect(idx, boxWidth, textWidth) {
        readyToShow = false

        // aguarda medida
        snapshotFlow { boxWidth }.filter { it > 0f }.first()
        snapshotFlow { textWidth }.filter { it > 0f }.first()

        // agora mostra e anima
        readyToShow = true
        val total = scrollState.maxValue.toFloat()
        val duration = 9_000L
        val frame = 16L
        val steps = (duration / frame).toInt().coerceAtLeast(1)
        val delta = total / steps

        repeat(steps) {
            scrollState.scrollBy(delta)
            delay(frame)
        }
        // corrige sobras
        val remainder = total - delta * steps
        if (remainder > 0f) scrollState.scrollBy(remainder)

        // antes de trocar, dá um pause
        delay(1_000)
        idx = (idx + 1) % lista.size
    }
}
