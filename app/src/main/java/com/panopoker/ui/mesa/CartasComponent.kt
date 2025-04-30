package com.panopoker.ui.mesa

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.panopoker.ui.utils.getCartaDrawable

@Composable
fun CartasDoJogador(cartas: List<String>) {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        cartas.forEach { carta ->
            val drawableId = getCartaDrawable(context, carta)
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Carta $carta",
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
            )
        }
    }
}
