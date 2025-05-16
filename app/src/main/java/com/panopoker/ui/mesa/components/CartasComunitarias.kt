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

@Composable
fun CartasComunitarias(cartas: CartasComunitarias?, context: Context) {
    cartas?.let {
        val todas = mutableListOf<String>()

        if (it.flop.isNotEmpty()) todas.addAll(it.flop)
        if (!it.turn.isNullOrBlank()) todas.add(it.turn)
        if (!it.river.isNullOrBlank()) todas.add(it.river)

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val cartaLargura = this.maxWidth * 0.068f // 👈 usa this.maxWidth se precisar ser explícito
            val espacamento = this.maxWidth * 0.008f

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(espacamento)
            ) {
                todas.forEach { carta ->
                    val id = getCartaDrawable(context, carta)

                    Box(
                        modifier = Modifier
                            .width(cartaLargura)
                            .aspectRatio(0.68f)
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(6.dp))
                    ) {
                        Image(
                            painter = painterResource(id),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(this@BoxWithConstraints.maxWidth * 0.004f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}
