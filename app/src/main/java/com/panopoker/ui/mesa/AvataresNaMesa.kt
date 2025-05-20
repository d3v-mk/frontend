package com.panopoker.ui.mesa

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.panopoker.model.Jogador
import com.panopoker.ui.mesa.components.*
import com.panopoker.R
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AvataresNaMesa(
    jogadores: List<Jogador>,
    jogadorDaVezId: Int?,
    usuarioLogadoId: Int,
    faseDaRodada: String?,
    poteTotal: Float,
    apostaAtualMesa: Float,
    onClickJogador: (Jogador) -> Unit,
    maoFormada: String
) {
    val mostrarFlop = faseDaRodada.equals("flop", ignoreCase = true)
    val mostrarTurn = faseDaRodada.equals("turn", ignoreCase = true)
    val mostrarRiver = faseDaRodada.equals("river", ignoreCase = true)
    val mostrarShowdown = faseDaRodada.equals("showdown", ignoreCase = true)

    val totalSeats = 6
    val seats: List<Jogador?> = (0 until totalSeats).map { seatIdx ->
        jogadores.find { it.posicao_cadeira == seatIdx }
    }
    val userSeat = jogadores.find { it.user_id == usuarioLogadoId }?.posicao_cadeira ?: 0

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(99f)
    ) {
        val largura = maxWidth
        val altura = maxHeight
        val context = LocalContext.current

        val avatarPositions = listOf(
            0.5f to 0.82f,
            0.21f to 0.74f,
            0.21f to 0.20f,
            0.5f to 0.19f,
            0.77f to 0.20f,
            0.77f to 0.74f
        )

        val fichaPositions = listOf(
            0.5f to 0.62f,
            0.21f to 0.50f,
            0.21f to 0.355f,
            0.5f to 0.34f,
            0.77f to 0.355f,
            0.77f to 0.50f
        )

        val cartaPositions = listOf(
            0.5f to 0.90f,
            0.465f to 0.48f,
            0.465f to 0.48f,
            0.465f to 0.48f,
            0.465f to 0.48f,
            0.465f to 0.48f
        )

        val tamanhoCarta = if (mostrarShowdown) largura * 0.05f else largura * 0.022f


        seats.forEachIndexed { seatIndex, jogadorOriginal ->
            if (jogadorOriginal == null) return@forEachIndexed
            key(jogadorOriginal.user_id) {
                val visualIndex = (seatIndex - userSeat + totalSeats) % totalSeats
                val (ax, ay) = avatarPositions[visualIndex]
                val (fx, fy) = fichaPositions[visualIndex]
                val (cx, cy) = cartaPositions[visualIndex]

                val avatarOffset = largura * (ax - 0.5f) to altura * (ay - 0.5f)
                val fichaOffset = largura * (fx - 0.5f) to altura * (fy - 0.5f)
                val cartaOffset = largura * (cx - 0.5f) to altura * (cy - 0.5f)

                val isVez = jogadorDaVezId == jogadorOriginal.user_id
                val jogador = jogadorOriginal.copy(vez = isVez)

                Box(
                    modifier = Modifier
                        .offset(x = avatarOffset.first, y = avatarOffset.second)
                        .align(Alignment.Center)
                ) {
                    AvatarJogador(
                        jogador = jogador,
                        usuarioLogadoId = usuarioLogadoId,
                        onClickJogador = onClickJogador
                    )

                    // 👉 MÃO FORMADA EXIBIDA DO LADO DO AVATAR DO JOGADOR LOGADO
                    if (jogador.user_id == usuarioLogadoId && maoFormada.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .offset(x = 82.dp, y = (-10).dp) // Ajusta posição do texto ao lado do avatar
                                .align(Alignment.Center)
                                .zIndex(10f)
                        ) {
                            Text(
                                text = maoFormada,
                                color = Color.Yellow, // dourado bonito
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (jogador.aposta_atual > 0f && !mostrarShowdown) {
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = fichaOffset.first - avatarOffset.first,
                                    y = fichaOffset.second - avatarOffset.second
                                )
                                .align(Alignment.Center)
                                .zIndex(1f)
                        ) {
                            FichaAposta(valor = jogador.aposta_atual)
                        }
                    }

                    if (jogador.user_id != usuarioLogadoId) {
                        val espacamento = tamanhoCarta * -0.3f
                        val larguraCartas = tamanhoCarta * 2 + espacamento

                        val deveRevelar = mostrarShowdown && jogador.cartas.size == 2 && jogador.participando_da_rodada == true
                        val cartas = if (deveRevelar) jogador.cartas else listOf("placeholder1", "placeholder2")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(espacamento),
                            modifier = Modifier
                                .offset(x = cartaOffset.first, y = cartaOffset.second)
                                .align(Alignment.Center)
                                .zIndex(2f)
                        ) {
                            cartas.forEachIndexed { index, carta ->
                                val resId = if (deveRevelar)
                                    context.resources.getIdentifier(
                                        nomeDaCarta(carta),
                                        "drawable",
                                        context.packageName
                                    )
                                else R.drawable.carta_back

                                CartaComAnimacaoFlip(
                                    frenteResId = resId,
                                    delayMs = jogador.posicao_cadeira * 800 + index * 600,
                                    startTrigger = deveRevelar,
                                    tamanho = tamanhoCarta
                                )
                            }
                        }
                    }
                }
            }
        }

        CartasDoJogador(
            minhasCartas = jogadores.find { it.user_id == usuarioLogadoId }?.cartas ?: emptyList(),
            context = context
        )
    }
}
