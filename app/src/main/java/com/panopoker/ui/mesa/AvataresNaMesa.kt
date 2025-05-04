package com.panopoker.ui.mesa

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.panopoker.R
import com.panopoker.model.Jogador
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import kotlinx.coroutines.delay

@Composable
fun CartaComAnimacaoFlip(
    frenteResId: Int,
    delayMs: Int,
    startTrigger: Boolean
) {
    val rotation = remember { Animatable(180f) }
    val cameraDistance = 12 * LocalContext.current.resources.displayMetrics.density

    val largura by remember { derivedStateOf { if (rotation.value <= 90f) 33.dp else 20.dp } }
    val altura by remember { derivedStateOf { if (rotation.value <= 90f) 48.dp else 30.dp } }

    LaunchedEffect(startTrigger) {
        if (startTrigger) {
            delay(delayMs.toLong())
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1500)
            )
        }
    }

    Box(
        modifier = Modifier
            .width(largura)
            .height(altura)
            .graphicsLayer {
                rotationY = rotation.value
                this.cameraDistance = cameraDistance
            }
    ) {
        if (rotation.value <= 90f) {
            Card(
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = frenteResId),
                    contentDescription = "Carta Frente",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Card(
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.carta_back),
                    contentDescription = "Carta Verso",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}




@Composable
fun FichaAposta(
    valor: Float,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .background(Color(0xFF1B1B1B), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ficha_poker),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "%.2f".format(valor),
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun AvataresNaMesa(
    jogadores: List<Jogador>,
    jogadorDaVezId: Int?,
    usuarioLogadoId: Int,
    faseDaRodada: String?
) {
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
        0.dp to (-120).dp,
        (0).dp to (0).dp,
        (-150).dp to (-140).dp,
        0.dp to (-140).dp,
        150.dp to (-140).dp,
        300.dp to (-100).dp
    )
    val chipOffsets = listOf(
        0.dp to (-75).dp,
        70.dp to (-20).dp,
        0.dp to 0.dp,
        0.dp to 0.dp,
        0.dp to 0.dp,
        0.dp to 0.dp
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(99f)
    ) {
        Log.d("AvataresNaMesa", "Cadeiras: $seats | UserSeat: $userSeat | Fase: $faseDaRodada")

        seats.forEachIndexed { seatIndex, jogador ->
            if (jogador == null) return@forEachIndexed
            val visualIndex = (seatIndex - userSeat + totalSeats) % totalSeats
            val (dx, dy) = posicoes[visualIndex]
            val (cardOffsetX, cardOffsetY) = holeCardOffsets[visualIndex]
            val (chipOffsetX, chipOffsetY) = chipOffsets[visualIndex]
            jogador.vez = jogadorDaVezId == jogador.user_id

            Box(
                modifier = Modifier
                    .offset(x = dx, y = dy)
                    .align(Alignment.Center)
            ) {
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
                            contentScale = ContentScale.Crop,
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
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

                if (jogador.aposta_atual > 0f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = chipOffsetX, y = chipOffsetY)
                            .zIndex(1f)
                    ) {
                        FichaAposta(valor = jogador.aposta_atual)
                    }
                }

                if (!mostrarShowdown) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = cardOffsetX, y = cardOffsetY)
                            .zIndex(2f)
                    ) {
                        repeat(2) {
                            Card(
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .width(33.dp)
                                    .height(48.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.carta_back),
                                    contentDescription = "Carta virada",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                if (mostrarShowdown) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = cardOffsetX, y = cardOffsetY)
                            .zIndex(2f)
                    ) {
                        jogador.cartas.forEachIndexed { cartaIndex, carta ->
                            val rankCode = carta.dropLast(1)
                            val suitCode = carta.last()
                            val rankName = when (rankCode) {
                                "2" -> "dois"; "3" -> "tres"; "4" -> "quatro"; "5" -> "cinco"
                                "6" -> "seis"; "7" -> "sete"; "8" -> "oito"; "9" -> "nove"; "10" -> "dez"
                                "J" -> "valete"; "Q" -> "dama"; "K" -> "rei"; "A" -> "as"
                                else -> rankCode.lowercase()
                            }
                            val suitName = when (suitCode) {
                                'P' -> "paus"; 'C' -> "copas"; 'E' -> "espadas"; 'O' -> "ouros"
                                else -> ""
                            }
                            val resId = context.resources.getIdentifier(
                                "${rankName}_de_${suitName}",
                                "drawable",
                                context.packageName
                            )
                            if (resId != 0) {
                                CartaComAnimacaoFlip(
                                    frenteResId = resId,
                                    delayMs = jogador.posicao_cadeira * 600 + cartaIndex * 400,
                                    startTrigger = true
                                )
                            }
                        }
                    }
                }

                if (jogador.is_sb || jogador.is_bb) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 20.dp)
                            .zIndex(3f)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (jogador.is_sb) {
                                Text(
                                    "SB",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (jogador.is_bb) {
                                Text(
                                    "BB",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
