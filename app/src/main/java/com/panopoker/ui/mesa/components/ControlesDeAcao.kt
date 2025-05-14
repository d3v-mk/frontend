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
    onRefresh: () -> Unit
) {
    val jogadorAtual = jogadores.find { it.user_id == userIdToken }
    val sliderMax = jogadorAtual?.saldo_atual?.coerceAtLeast(0.01f) ?: 0.01f
    val apostaJogador = jogadorAtual?.aposta_atual ?: 0f
    val maiorAposta = jogadores.maxOfOrNull { it.aposta_atual } ?: 0f
    val textoAcao = if (maiorAposta > 0f && apostaJogador < maiorAposta) "Call" else "Check"
    val corBotao = if (textoAcao == "Check") Color.Gray else Color.Green
    val raiseLimpo = "%.2f".format(raiseValue.coerceIn(0.01f, sliderMax)).replace(",", ".").toFloat()


    val context = LocalContext.current

    fun tocarSom(resId: Int) {
        try {
            val mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer.setOnCompletionListener {
                it.release()
            }
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    if (mostrarSlider) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text("Raise: R$ %.2f".format(raiseValue), color = Color.White)
                Slider(
                    value = raiseValue,
                    onValueChange = onSliderChange,
                    valueRange = 0.01f..sliderMax,
                    steps = 50,
                    modifier = Modifier
                        .offset(x = 65.dp)
                        .height(290.dp)
                        .width(240.dp)
                        .graphicsLayer { rotationZ = -90f },
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFC107),
                        activeTrackColor = Color(0xFFFFC107),
                        inactiveTrackColor = Color.DarkGray
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onEsconderSlider,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.width(110.dp).height(36.dp)
                    ) {
                        Text("Cancelar", color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val service = RetrofitInstance.retrofit.create(MesaService::class.java)

                                    if (raiseValue >= sliderMax) {
                                        // 💥 ALL-IN!
                                        service.allInJWT(mesaId, "Bearer $accessToken")
                                    } else {
                                        // 🎯 RAISE normal
                                        service.raiseJWT(mesaId, raiseLimpo, "Bearer $accessToken")
                                    }

                                    onEsconderSlider()
                                    delay(500)
                                    onRefresh()
                                } catch (_: Exception) {}
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066CC)),
                        modifier = Modifier.width(110.dp).height(36.dp)
                    ) {
                        Text("Confirmar", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.padding(end = 16.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            RetrofitInstance.retrofit.create(MesaService::class.java)
                                .foldJWT(mesaId, "Bearer $accessToken")
                            delay(500)
                            onRefresh()
                        } catch (_: Exception) {}
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.height(36.dp).width(90.dp)
            ) {
                Text("Fold", color = Color.White, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val service = RetrofitInstance.retrofit.create(MesaService::class.java)
                            if (textoAcao == "Call") {
                                service.callJWT(mesaId, "Bearer $accessToken")
                            } else {
                                service.checkJWT(mesaId, "Bearer $accessToken")
                            }
                            delay(500)
                            onRefresh()
                        } catch (_: Exception) {}
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = corBotao),
                modifier = Modifier.height(36.dp).width(90.dp)
            ) {
                Text(textoAcao, color = Color.White, fontSize = 12.sp)
            }

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
}
