package com.panopoker.ui.mesa.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.foundation.layout.size

@Composable
fun TimerCircular(
    progresso: Float, // 0.0 a 1.0
    tamanho: Dp = 72.dp,
    cor: Color = Color.Yellow
) {
    Canvas(modifier = Modifier.size(tamanho)) {
        val stroke = 6.dp.toPx()
        val canvasSize = this.size // <-- PEGA O size corretamente aqui
        drawArc(
            color = cor,
            startAngle = -90f,
            sweepAngle = 360f * progresso,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
            size = Size(canvasSize.width, canvasSize.height)
        )
    }
}///
