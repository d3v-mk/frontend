package com.panopoker.ui.mesa.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.panopoker.ui.mesa.components.CartaComAnimacaoFlip
import com.panopoker.ui.mesa.components.nomeDaCarta

@Composable
fun HoleCards(
    cartas: List<String>,
    delayBaseMs: Int = 0,
    offsetX: Int = 0,
    offsetY: Int = 0,
    cadeira: Int = 0
) {
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .offset(x = offsetX.dp, y = offsetY.dp)
            .zIndex(2f)
    ) {
        cartas.forEachIndexed { index, carta ->
            val resId = context.resources.getIdentifier(
                nomeDaCarta(carta),
                "drawable",
                context.packageName
            )
            if (resId != 0) {
                CartaComAnimacaoFlip(
                    frenteResId = resId,
                    delayMs = cadeira * 800 + index * 600,
                    startTrigger = true
                )
            }
        }
    }
}