package com.panopoker.ui.mesa.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.panopoker.R
import com.panopoker.model.Jogador
import kotlinx.coroutines.delay

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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.zIndex(1f)
    ) {
        Box(modifier = Modifier.size(72.dp)) {
            if (jogador.vez) {
                TimerCircular(progresso = progresso)
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(finalAvatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Avatar jogador",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .shadow(6.dp, CircleShape)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                jogador.username,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .background(Color(0xFF555555), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.ficha_poker),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "%.2f".format(jogador.saldo_atual),
                        color = Color(0xFFFFD700),
                        fontSize = 12.sp
                    )
                    if (jogador.is_sb) {
                        Text("SB", color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    if (jogador.is_bb) {
                        Text("BB", color = Color.Magenta, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}///
