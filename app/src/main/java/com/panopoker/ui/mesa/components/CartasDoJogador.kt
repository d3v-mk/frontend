package com.panopoker.ui.mesa.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.panopoker.ui.utils.getCartaDrawable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Density
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun CartasDoJogador(minhasCartas: List<String>, context: Context) {
    if (minhasCartas.isNotEmpty()) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
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
                    .graphicsLayer {
                        rotationZ = -20f // ou -10f se quiser mais suave
                    }
                    .zIndex(10f),
                horizontalArrangement = Arrangement.spacedBy(espacamento)
            ) {
                minhasCartas.forEach { carta ->
                    val id = getCartaDrawable(context, carta)
                    Box(
                        modifier = Modifier
                            .width(cartaLargura)
                            .aspectRatio(0.68f)
                            .graphicsLayer {
                                rotationZ = if (minhasCartas.size == 2 && carta == minhasCartas.first()) -15f else 15f
                            }
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                    )
                    {
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
