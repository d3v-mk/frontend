// CarrosselLobby.kt
package com.panopoker.ui.lobby.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.panopoker.R

@Composable
fun CarrosselLoby(
    imagens: List<Int>,
    modifier: Modifier = Modifier
) {
    var indexAtual by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            indexAtual = (indexAtual + 1) % imagens.size
        }
    }

    AnimatedContent(
        targetState = imagens[indexAtual],
        transitionSpec = {
            slideInHorizontally(
                animationSpec = tween(durationMillis = 300),
                initialOffsetX = { fullWidth -> fullWidth } // entra da direita
            ) togetherWith slideOutHorizontally(
                animationSpec = tween(durationMillis = 300),
                targetOffsetX = { fullWidth -> -fullWidth } // sai pela esquerda
            )
        },
        label = "slide-carta"
    ) { imagem ->
        Image(
            painter = painterResource(id = imagem),
            contentDescription = "Carta Rotativa",
            modifier = modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
        )
    }
}
