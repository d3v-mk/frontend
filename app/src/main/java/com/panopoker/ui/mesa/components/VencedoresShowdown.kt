package com.panopoker.ui.mesa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import com.panopoker.model.Jogador
import com.panopoker.model.JogadorShowdownDto

@Composable
fun VencedoresShowdown(
    vencedores: List<Int>,
    showdown: List<JogadorShowdownDto>,
    jogadores: List<Jogador>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 12.dp, top = 8.dp, end = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xCC222222))
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "🏆 Vencedor(es):",
                color = Color.Yellow,
                fontSize = 20.sp
            )
            vencedores.forEach { vencedorId ->
                val jogadorShowdown = showdown.find { it.jogador_id == vencedorId }
                jogadorShowdown?.let { jShow ->
                    val jogadorMesa = jogadores.find { it.user_id == jShow.jogador_id }
                    val nome = jogadorMesa?.username ?: "Jogador ${jShow.jogador_id}"
                    Text(
                        text = "$nome: ${jShow.descricao_mao}",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
