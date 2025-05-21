// 📁 com/panopoker/ui/mesa/components/CartasDoJogador.kt
package com.panopoker.ui.mesa.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.graphicsLayer
import com.panopoker.ui.utils.getCartaDrawable
import com.panopoker.ui.utils.glowEffect
import androidx.compose.runtime.key


@Composable
fun CartasDoJogador(
    cartas: List<String>,
    context: Context,
    modifier: Modifier = Modifier,
    cartasBrilhando: List<String> = emptyList()
) {
    if (cartas.isNotEmpty()) {
        BoxWithConstraints(
            modifier = modifier.fillMaxSize()
        ) {
            val largura = maxWidth
            val altura = maxHeight
            val cartaLargura = largura * 0.05f
            val espacamento = largura * -0.03f
            val offsetX = largura * 0.40f
            val offsetY = altura * 0.707f

            Row(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .graphicsLayer { rotationZ = -20f }
                    .zIndex(100f),
                horizontalArrangement = Arrangement.spacedBy(espacamento)
            ) {
                cartas.forEachIndexed { idx, carta ->
                    // chave única: carta + índice
                    key(carta + "_$idx") {
                        val id = getCartaDrawable(context, carta)
                        val isVencedora = carta in cartasBrilhando

                        Box(
                            modifier = Modifier
                                .width(cartaLargura)
                                .aspectRatio(0.68f)
                                .graphicsLayer {
                                    rotationZ = if (cartas.size == 2 && idx == 0) -15f else 15f
                                }
                                // eleva só as vencedoras acima das outras
                                .zIndex(if (isVencedora) 1f else 0f)
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .border(
                                    width = if (isVencedora) 2.dp else 1.dp,
                                    color = if (isVencedora) Color.Yellow else Color.Black,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .then(if (isVencedora) Modifier.glowEffect() else Modifier)
                        ) {
                            Image(
                                painter = painterResource(id),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(largura * 0.002f)
                            )
                        }
                    }
                }
            }
        }
    }
}///
