package com.panopoker.ui.mesa.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import com.panopoker.R

@Composable
fun MesaImagemDeFundo() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(0f)
    ) {

        Image(
            painter = painterResource(id = R.drawable.mesa_pano),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize() // NADA DE fillMaxWidth
        )
    }
}




