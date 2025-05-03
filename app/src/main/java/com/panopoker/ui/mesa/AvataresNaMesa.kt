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

        val indexDoLogado = jogadores.indexOfFirst { it.user_id == usuarioLogadoId }
        val jogadoresRotacionados = if (indexDoLogado >= 0)
            jogadores.drop(indexDoLogado) + jogadores.take(indexDoLogado)
        else
            jogadores

        val posicoesFixas = listOf(
            0.dp to 140.dp,
            -325.dp to 0.dp,
            -200.dp to -140.dp,
            0.dp to -140.dp,
            200.dp to -140.dp,
            325.dp to 0.dp
        )

        jogadoresRotacionados.forEachIndexed { visualIndex, jogador ->
            jogador.vez = (jogadorDaVezId != null && jogador.user_id == jogadorDaVezId)
            Log.d("AvataresNaMesa", "Jogador ${jogador.username} (user_id: ${jogador.user_id}) -> vez = ${jogador.vez}")

            val (offsetX, offsetY) = posicoesFixas.getOrElse(visualIndex % posicoesFixas.size) { 0.dp to 0.dp }

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

                // SB / BB fora da coluna pra não empurrar layout
                if (jogador.is_sb || jogador.is_bb) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (15).dp)
                            .zIndex(999f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (jogador.is_sb) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF00BCD4), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("SB", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (jogador.is_bb) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFF4081), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("BB", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Ficha de aposta
                val fichaOffset = when (visualIndex) {
                    0 -> Modifier.offset(x = 0.dp, y = -70.dp) //ok
                    1 -> Modifier.offset(x = 70.dp, y = -15.dp) //ok
                    2 -> Modifier.offset(x = 0.dp, y = 70.dp) //ok
                    3 -> Modifier.offset(x = 0.dp, y = 70.dp) //ok
                    4 -> Modifier.offset(x = 0.dp, y = 70.dp) //ok
                    5 -> Modifier.offset(x = -70.dp, y = -15.dp) //ok
                    else -> Modifier.offset(0.dp, 0.dp)
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
}
