package com.panopoker.ui.mesa.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

@Composable
fun MainPot(
    poteTotal: Float,
    faseDaRodada: String?,
    modifier: Modifier = Modifier,
    offsetPercentX: Float = -0.13f,     // ← centralizado
    offsetPercentY: Float = -0.18f   // ← 18% da tela pra baixo (ajuste livre)
) {
    val mostrar = faseDaRodada in listOf("flop", "turn", "river", "showdown")

    if (mostrar && poteTotal > 0f) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .zIndex(4f),
            contentAlignment = Alignment.Center
        ) {
            val offsetX = maxWidth * offsetPercentX
            val offsetY = maxHeight * offsetPercentY

            Box(
                modifier = Modifier.offset(x = offsetX, y = offsetY)
            ) {
                FichaAposta(valor = poteTotal)
            }
        }
    }
}
