package com.panopoker.ui.mesa

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.panopoker.R
import com.panopoker.model.Jogador

@Composable
fun FichaAposta(jogador: Jogador) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(Color(0xFF1B1B1B), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ficha_poker),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "%.2f".format(jogador.aposta_atual),
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun AvataresNaMesa(
    jogadores: List<Jogador>,
    jogadorDaVezId: Int?,
    usuarioLogadoId: Int
) {
    Box(modifier = Modifier.fillMaxSize().zIndex(99f)) {

        Log.d("AvataresNaMesa", "Renderizando ${jogadores.size} jogadores | Jogador da vez: $jogadorDaVezId")

        jogadores.forEach { jogador ->
            jogador.vez = (jogadorDaVezId != null && jogador.user_id == jogadorDaVezId)
            Log.d("AvataresNaMesa", "Jogador ${jogador.username} (user_id: ${jogador.user_id}) -> vez = ${jogador.vez}")
        }

        val posicoesFixas = listOf(
            0.dp to 140.dp,
            -320.dp to 0.dp,
            -200.dp to -140.dp,
            0.dp to -140.dp,
            200.dp to -140.dp,
            320.dp to 0.dp
        )

        jogadores.forEach { jogador ->
            val (offsetX, offsetY) = posicoesFixas.getOrElse(jogador.posicao_cadeira % posicoesFixas.size) { 0.dp to 0.dp }

            // Avatar
            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .align(Alignment.Center)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .size(64.dp)
                            .border(
                                2.dp,
                                if (jogador.vez) Color.Yellow else Color.Gray,
                                RoundedCornerShape(50)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_default),
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Nome e saldo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = jogador.username,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF555555), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ficha_poker),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "%.2f".format(jogador.saldo_atual),
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            val fichaOffset = when (jogador.posicao_cadeira) {
                0 -> Modifier.offset(x = 0.dp, y = (60).dp)
                1 -> Modifier.offset(x = (-250).dp, y = (-20).dp)
                2 -> Modifier.offset(x = (-200).dp, y = (-70).dp)
                3 -> Modifier.offset(x = (0).dp, y = (-70).dp)
                4 -> Modifier.offset(x = (0).dp, y = (40).dp) // falta esse
                5 -> Modifier.offset(x = (-40).dp, y = (0).dp) // falta esse
                else -> Modifier.offset(x = (0).dp, y = (0).dp)
            }

            if (jogador.aposta_atual > 0f) {
                Box(
                    modifier = fichaOffset
                        .align(Alignment.Center)
                        .zIndex(999f)
                ) {
                    FichaAposta(jogador)
                }
            }
        }
    }
}
