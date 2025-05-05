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


@Composable
fun AvatarJogador(jogador: Jogador) {
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
                    2.dp,
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                jogador.username,
                color = Color.White,
                fontSize = 14.sp
            )
            Box(
                modifier = Modifier
                    .background(Color(0xFF555555), RoundedCornerShape(6.dp))
                    .padding(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                }
            }
        }
    }
}
