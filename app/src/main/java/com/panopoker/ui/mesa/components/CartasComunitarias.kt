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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.panopoker.model.CartasComunitarias
import com.panopoker.ui.utils.getCartaDrawable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.unit.dp
import com.panopoker.ui.utils.glowEffect



@Composable
fun CartasComunitarias(
    cartas: CartasComunitarias?,
    context: Context,
    cartasBrilhando: List<String> = emptyList()
) {
    cartas?.let {
        val todas = mutableListOf<String>()

        if (it.flop.isNotEmpty()) todas.addAll(it.flop)
        if (!it.turn.isNullOrBlank()) todas.add(it.turn)
        if (!it.river.isNullOrBlank()) todas.add(it.river)

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val cartaLargura = this.maxWidth * 0.065f // 👈 usa this.maxWidth se precisar ser explícito
            val espacamento = this.maxWidth * 0.008f

            Row(
                modifier = Modifier
                    .offset(x = maxWidth * -0.02f, y = (-7).dp), // mexe o conjunto horizontalmente
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(espacamento)
            ) {
                todas.forEach { carta ->
                    val id = getCartaDrawable(context, carta)

                    val isVencedora = cartasBrilhando.contains(carta)

                    Box(
                        modifier = Modifier
                            .width(cartaLargura)
                            .aspectRatio(0.68f)
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .border(
                                width = if (isVencedora) 2.dp else 1.dp,
                                color = if (isVencedora) Color.Yellow else Color.Black,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .then(if (isVencedora) Modifier.glowEffect() else Modifier)  // vamos criar isso
                    ) {
                        Image(
                            painter = painterResource(id),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(this@BoxWithConstraints.maxWidth * 0.002f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}
