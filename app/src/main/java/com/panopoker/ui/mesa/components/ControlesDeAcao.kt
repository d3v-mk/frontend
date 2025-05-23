package com.panopoker.ui.mesa.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import com.panopoker.model.Jogador
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment

import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext
import com.panopoker.R

import androidx.compose.ui.graphics.TransformOrigin
import com.panopoker.network.WebSocketClient


@Composable
fun ControlesDeAcao(
    jogadores: List<Jogador>,
    userIdToken: Int,
    mostrarSlider: Boolean,
    raiseValue: Float,
    stackJogador: Float,
    mesaId: Int,
    accessToken: String,
    coroutineScope: CoroutineScope,
    onSliderChange: (Float) -> Unit,
    onMostrarSlider: () -> Unit,
    onEsconderSlider: () -> Unit,
    webSocketClient: WebSocketClient,
) {
    val jogadorAtual = jogadores.find { it.user_id == userIdToken }
    val sliderMax = jogadorAtual?.saldo_atual?.coerceAtLeast(0.01f) ?: 0.01f
    val apostaJogador = jogadorAtual?.aposta_atual ?: 0f
    val maiorAposta = jogadores.maxOfOrNull { it.aposta_atual } ?: 0f
    val textoAcao = if (maiorAposta > 0f && apostaJogador < maiorAposta) "Call" else "Check"
    val corBotao = if (textoAcao == "Check") Color.Gray else Color.Green
    val raiseLimpo = "%.2f".format(raiseValue.coerceIn(0.01f, sliderMax)).replace(",", ".").toFloat()



    if (mostrarSlider) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // WRAPPER para posicionar tudo fixo no canto inferior direito
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 35.dp, bottom = 300.dp) // Distância dos botões
            ) {
                // SLIDER ROTACIONADO
                Box(
                    modifier = Modifier
                        .width(235.dp) // Altura visual do slider
                        .height(40.dp) // Espessura
                        .graphicsLayer {
                            rotationZ = -90f
                            transformOrigin = TransformOrigin(1f, 1f) // Gira do canto inferior direito
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Slider(
                        value = raiseValue,
                        onValueChange = onSliderChange,
                        valueRange = 0.01f..sliderMax,
                        steps = 50,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFC107),
                            activeTrackColor = Color(0xFFFFC107),
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }

                // TEXTO ACIMA DA BARRA
                Text(
                    text = "%.2f".format(raiseValue),
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Alinha o texto no canto superior direito
                        .padding(top = 20.dp, end = 8.dp) // Ajusta a distância do texto em relação ao topo e à borda direita
                )
            }

            // BOTÕES CONFIRMAR / CANCELAR
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            ) {
                Button(
                    onClick = onEsconderSlider,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .height(36.dp)
                        .width(110.dp)
                ) {
                    Text("Cancelar", color = Color.White, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                if (raiseValue >= sliderMax) {
                                    webSocketClient.enviarAllin()
                                } else {
                                    webSocketClient.enviarRaise(raiseLimpo)
                                }
                                onEsconderSlider()
                                delay(100)
                            } catch (_: Exception) {}
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066CC)),
                    modifier = Modifier
                        .height(36.dp)
                        .width(110.dp)
                ) {
                    Text("Confirmar", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }///






    else {
        Row(
            modifier = Modifier.padding(end = 16.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Botão REVELAR (só aparece se o jogador foldou e está no showdown)
            if (jogadorAtual?.participando_da_rodada == false) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val service = RetrofitInstance.retrofit.create(MesaService::class.java)
                                val resp = service.revelarCartas(mesaId, "Bearer $accessToken")
                                if (resp.isSuccessful) {
                                    delay(200)
                                }
                            } catch (_: Exception) {}
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDAA520),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .width(65.dp)
                ) {
                    Text("👁", fontSize = 12.sp)
                }
            }

            // Botão FOLD
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            RetrofitInstance.retrofit.create(MesaService::class.java)
                                webSocketClient.enviarFold()
                            delay(500)
                        } catch (_: Exception) {}
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.height(36.dp).width(90.dp)
            ) {
                Text("Fold", color = Color.White, fontSize = 12.sp)
            }

            // Botão CALL / CHECK
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val service = RetrofitInstance.retrofit.create(MesaService::class.java)
                            if (textoAcao == "Call") {
                                webSocketClient.enviarCall()
                            } else {
                                webSocketClient.enviarCheck()
                            }
                            delay(500)
                        } catch (_: Exception) {}
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = corBotao),
                modifier = Modifier.height(36.dp).width(90.dp)
            ) {
                Text(textoAcao, color = Color.White, fontSize = 12.sp)
            }

            // Botão RAISE
            Button(
                onClick = {
                    val saldo = jogadorAtual?.saldo_atual ?: 0.01f
                    onSliderChange((saldo / 2f).coerceIn(0.01f, saldo))
                    onMostrarSlider()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066CC)),
                modifier = Modifier.height(36.dp).width(90.dp)
            ) {
                Text("Raise", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}////
