package com.panopoker.ui.mesa

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.panopoker.R
import com.panopoker.model.Jogador
import com.panopoker.ui.mesa.components.*

@Composable
fun AvataresNaMesa(
    jogadores: List<Jogador>,
    jogadorDaVezId: Int?,
    usuarioLogadoId: Int,
    faseDaRodada: String?,
    poteTotal: Float,
    apostaAtualMesa: Float
) {
    val mostrarFlop = faseDaRodada.equals("flop", ignoreCase = true)
    val mostrarTurn = faseDaRodada.equals("turn", ignoreCase = true)
    val mostrarRiver = faseDaRodada.equals("river", ignoreCase = true)
    val mostrarShowdown = faseDaRodada.equals("showdown", ignoreCase = true)

    val context = LocalContext.current
    val totalSeats = 6
    val seats: List<Jogador?> = (0 until totalSeats).map { seatIdx ->
        jogadores.find { it.posicao_cadeira == seatIdx }
    }
    val userSeat = jogadores.find { it.user_id == usuarioLogadoId }?.posicao_cadeira ?: 0

    val posicoes = listOf(
        0.dp to 140.dp,
        (-325).dp to 0.dp,
        (-200).dp to (-140).dp,
        0.dp to (-140).dp,
        200.dp to (-140).dp,
        325.dp to 0.dp
    )
    val holeCardOffsets = listOf(
        (-30).dp to 0.dp,
        (-30).dp to 0.dp,
        (-30).dp to 0.dp,
        (-30).dp to 0.dp,
        (-30).dp to 0.dp,
        30.dp to 0.dp
    )
    val chipOffsets = listOf(
        0.dp to (-75).dp,
        70.dp to (-15).dp,
        0.dp to 65.dp,
        0.dp to 65.dp,
        0.dp to 65.dp,
        (-70).dp to (-15).dp
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(99f)
    ) {
        Log.d("AvataresNaMesa", "Seats: $seats | UserSeat: $userSeat | Phase: $faseDaRodada")

        // Pote total
        if ((mostrarFlop || mostrarTurn || mostrarRiver || mostrarShowdown) && poteTotal > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = (-90).dp, y = 125.dp)
                    .zIndex(4f)
            ) {
                FichaAposta(valor = poteTotal)
            }
        }

        seats.forEachIndexed { seatIndex, jogadorOriginal ->
            if (jogadorOriginal == null) return@forEachIndexed
            key(jogadorOriginal.user_id) {
                // calcula visualIndex e offsets
                val visualIndex = (seatIndex - userSeat + totalSeats) % totalSeats
                val (dx, dy) = posicoes[visualIndex]
                val (cardOffsetX, cardOffsetY) = holeCardOffsets[visualIndex]
                val (chipOffsetX, chipOffsetY) = chipOffsets[visualIndex]

                // define se é vez sem mutar o objeto original
                val isVez = jogadorDaVezId == jogadorOriginal.user_id
                val jogador = jogadorOriginal.copy(vez = isVez)

                Box(
                    modifier = Modifier
                        .offset(x = dx, y = dy)
                        .align(Alignment.Center)
                ) {
                    AvatarJogador(jogador)

                    // fichas de aposta
                    if (
                        jogador.aposta_atual > 0f &&
                        !mostrarShowdown
                    )
                    {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = chipOffsetX, y = chipOffsetY)
                                .zIndex(1f)
                        ) {
                            FichaAposta(valor = jogador.aposta_atual)
                        }
                    }

                    // cartas de hole (back)
                    if (!mostrarShowdown) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-6).dp),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = cardOffsetX, y = cardOffsetY)
                                .zIndex(2f)
                        ) {
                            repeat(2) {
                                CartaComAnimacaoFlip(
                                    frenteResId = R.drawable.carta_back,
                                    delayMs = 0,
                                    startTrigger = false
                                )
                            }
                        }
                    }

                    // showdown: mostra as cartas
                    if (mostrarShowdown) {
                        HoleCards(
                            cartas = jogador.cartas,
                            delayBaseMs = jogador.posicao_cadeira * 600,
                            offsetX = cardOffsetX.value.toInt(),
                            offsetY = cardOffsetY.value.toInt(),
                            cadeira = jogador.posicao_cadeira
                        )
                    }
                }
            }
        }
    }
}
