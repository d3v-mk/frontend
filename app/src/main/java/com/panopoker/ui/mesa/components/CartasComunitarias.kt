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

        // Só adiciona o flop se tiver
        if (it.flop.isNotEmpty()) {
            todas.addAll(it.flop)
        }

        // Só adiciona o turn se já foi distribuído
        if (!it.turn.isNullOrBlank()) {
            todas.add(it.turn)
        }

        // Só adiciona o river se já foi distribuído
        if (!it.river.isNullOrBlank()) {
            todas.add(it.river)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.wrapContentSize()
        ) {
            todas.forEach { carta ->
                val id = getCartaDrawable(context, carta)
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 75.dp)
                        .offset(x = -40.dp, y = -5.dp)
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(6.dp))
                ) {
                    Image(
                        painter = painterResource(id),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
