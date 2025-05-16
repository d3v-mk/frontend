package com.panopoker.ui.mesa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.panopoker.R
import com.panopoker.model.Jogador
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

@Composable
fun AvatarJogador(jogador: Jogador) {
    val tempoTotal = 20
    var tempoRestante by remember { mutableStateOf(tempoTotal) }

    LaunchedEffect(jogador.vez) {
        if (jogador.vez) {
            tempoRestante = tempoTotal
            while (tempoRestante > 0) {
                delay(1000)
                tempoRestante--
            }
        }
    }

    val progresso = tempoRestante / tempoTotal.toFloat()
    val finalAvatarUrl = jogador.avatarUrl ?: "https://i.imgur.com/q0fxp3t.jpeg"

    BoxWithConstraints(
        modifier = Modifier.zIndex(1f)
    ) {
        val avatarBoxSize = maxWidth * 0.093f
        val imageSize = avatarBoxSize * 0.88f
        val iconSize = maxWidth * 0.017f
        val fontSizeNome = maxWidth.value * 0.014f
        val fontSizeSaldo = maxWidth.value * 0.014f
        val fontSizeBlind = maxWidth.value * 0.014f
        val paddingHorizontal = maxWidth * 0.005f
        val paddingVertical = maxWidth * 0.003f

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(avatarBoxSize)) {
                if (jogador.vez) {
                    TimerCircular(progresso = progresso, tamanho = avatarBoxSize)
                }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(finalAvatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar jogador",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(imageSize)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .shadow(6.dp, CircleShape)
                )
            }

            // ✅ NOME + SALDO + FICHA + BLINDS TUDO JUNTO AQUI
            Box(
                modifier = Modifier
                    .background(Color(0xFF555555), RoundedCornerShape(6.dp))
                    .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        jogador.username,
                        color = Color.White,
                        fontSize = fontSizeNome.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ficha_poker),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )

                    Text(
                        "%.2f".format(jogador.saldo_atual),
                        color = Color(0xFFFFD700),
                        fontSize = fontSizeSaldo.sp
                    )

                    if (jogador.is_sb) {
                        Text(
                            "SB",
                            color = Color.Cyan,
                            fontSize = fontSizeBlind.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (jogador.is_bb) {
                        Text(
                            "BB",
                            color = Color.Magenta,
                            fontSize = fontSizeBlind.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}