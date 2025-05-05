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
import androidx.compose.ui.zIndex
import com.panopoker.ui.utils.getCartaDrawable

@Composable
fun CartasDoJogador(minhasCartas: List<String>, context: Context) {
    if (minhasCartas.isNotEmpty()) {
        Row(
            modifier = Modifier
                .offset(90.dp, -45.dp) // O .align() saiu
                .zIndex(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            minhasCartas.forEach { carta ->
                val id = getCartaDrawable(context, carta)
                Box(
                    modifier = Modifier
                        .size(width = 45.dp, height = 65.dp)
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(6.dp))
                ) {
                    Image(
                        painter = painterResource(id),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}
