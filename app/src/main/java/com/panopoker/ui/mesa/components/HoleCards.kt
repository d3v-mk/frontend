package com.panopoker.ui.mesa.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.panopoker.ui.mesa.components.CartaComAnimacaoFlip
import com.panopoker.ui.mesa.components.nomeDaCarta
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment

@Composable
fun HoleCards(
    cartas: List<String>,
    delayBaseMs: Int = 0,
    cx: Float,
    cy: Float,
    ax: Float,
    ay: Float,
    tamanhoCarta: Dp,
    cadeira: Int = 0
) {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val largura = maxWidth
        val altura = maxHeight
        val espacamento = largura * -0.015f
        val larguraCartas = tamanhoCarta * 2 + espacamento

        val offsetX = largura * cx - (larguraCartas / 2)
        val offsetY = altura * cy - (tamanhoCarta / 2)

        Row(
            horizontalArrangement = Arrangement.spacedBy(espacamento),
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .zIndex(2f)
        ) {
            cartas.forEachIndexed { index, carta ->
                val resId = context.resources.getIdentifier(
                    nomeDaCarta(carta),
                    "drawable",
                    context.packageName
                )
                if (resId != 0) {
                    key(carta) {
                        CartaComAnimacaoFlip(
                            frenteResId = resId,
                            delayMs = cadeira * 800 + index * 600,
                            startTrigger = true,
                            tamanho = tamanhoCarta
                        )
                    }
                }
            }
        }
    }
}