package com.panopoker.ui.mesa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panopoker.R
import com.panopoker.model.Jogador
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.*
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.zIndex(1f)
    ) {
        Card(
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .size(64.dp)
                .border(
                    3.dp,
                    if (jogador.vez) Color.Yellow else Color.Gray,
                    RoundedCornerShape(50)
                ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar_default),
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize()
            )
        }

        if (jogador.vez) { //exibe o tempo restante na tela (15s)
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
                    Image(
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
