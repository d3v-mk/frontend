package com.panopoker.ui.mesa

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.panopoker.R

@Composable
fun MesaImagemDeFundo() {
    Image(
        painter = painterResource(id = R.drawable.mesa_pano),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .requiredWidth(700.dp)
            .requiredHeight(735.dp)
            .zIndex(0f)
    )
}
