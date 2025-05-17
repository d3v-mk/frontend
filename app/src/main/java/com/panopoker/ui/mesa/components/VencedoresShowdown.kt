package com.panopoker.ui.mesa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun VencedoresShowdown(
    vencedores: List<String>,
    maoFormada: String,
    modifier: Modifier = Modifier
) {
    val texto = "🏆 ${vencedores.joinToString()} | $maoFormada"


    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, bottom = 6.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = texto,
                color = Color.Yellow,
                fontSize = 18.sp
            )
        }

    }
}
