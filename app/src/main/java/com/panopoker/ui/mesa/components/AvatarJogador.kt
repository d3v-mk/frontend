package com.panopoker.ui.mesa.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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

    val tempoTotal = 20 // timer
    var tempoRestante by remember { mutableStateOf(tempoTotal) } // timer

    LaunchedEffect(jogador.vez) { // timer
        if (jogador.vez) {
            tempoRestante = tempoTotal
            while (tempoRestante > 0) {
                delay(1000)
                tempoRestante--
            }
        }
    }

    val finalAvatarUrl = jogador.avatarUrl ?: "https://i.imgur.com/q0fxp3t.jpeg" // fallback lendário

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.zIndex(1f)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(finalAvatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Avatar jogador",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .border(
                    3.dp,
                    if (jogador.vez) Color.Yellow else Color.Gray,
                    CircleShape
                )
        )

        if (jogador.vez) { // exibe o tempo restante na tela (20s)
            Text(
                text = "Tempo: ${tempoRestante}s",
                color = Color.Yellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Nome
            Text(
                jogador.username,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            // Saldo + SB/BB
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
                        Text(
                            "SB",
                            color = Color.Cyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (jogador.is_bb) {
                        Text(
                            "BB",
                            color = Color.Magenta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
