package com.panopoker.ui.mesa

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import com.panopoker.data.session.SessionManager
import com.panopoker.model.CartasComunitarias
import com.panopoker.model.Jogador
import com.panopoker.model.MesaDto
import com.panopoker.model.ShowdownDto
import com.panopoker.network.WebSocketClient
import com.panopoker.ui.mesa.components.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MesaScreen(mesaId: Int, navController: NavController? = null) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val accessToken = session.fetchAuthToken() ?: ""
    val userIdToken = session.getUserIdFromToken(accessToken) ?: -99
    val coroutineScope = rememberCoroutineScope()

    var faseDaRodada by remember { mutableStateOf<String?>(null) }
    var jogadores by remember { mutableStateOf<List<Jogador>>(emptyList()) }
    var cartas by remember { mutableStateOf<CartasComunitarias?>(null) }
    var minhasCartas by remember { mutableStateOf<List<String>>(emptyList()) }
    var maoFormada by remember { mutableStateOf("") }
    var jogadorDaVezId by remember { mutableStateOf<Int?>(null) }
    var stackJogador by remember { mutableFloatStateOf(1f) }
    var raiseValue by remember { mutableFloatStateOf(0f) }
    var mostrarSlider by remember { mutableStateOf(false) }
    var showdownInfo by remember { mutableStateOf<ShowdownDto?>(null) }
    var mesa by remember { mutableStateOf<MesaDto?>(null) }
    val usuarioLogadoId = session.fetchUserId()

    LaunchedEffect(mostrarSlider) {
        if (mostrarSlider) {
            val jogadorAtual = jogadores.find { it.user_id == userIdToken }
            val saldo = jogadorAtual?.saldo_atual ?: 0.01f
            raiseValue = (saldo / 2f).coerceIn(0.01f, saldo)
        }
    }

    fun refreshMesa() {
        coroutineScope.launch {
            try {
                val service = RetrofitInstance.retrofit.create(MesaService::class.java)

                val mesaResponse = withContext(Dispatchers.IO) {
                    service.getMesa(mesaId, "Bearer $accessToken")
                }
                val mesaBody = mesaResponse.body()

                mesa = mesaBody
                faseDaRodada = mesaBody?.estado_da_rodada
                Log.d("\uD83D\uDD25 MesaDebug", "\uD83D\uDCD1 Estado da rodada: $faseDaRodada")
                jogadorDaVezId = mesaBody?.jogador_da_vez

                val respJogadores = withContext(Dispatchers.IO) {
                    service.getJogadoresDaMesa(mesaId, "Bearer $accessToken")
                }
                jogadores = (respJogadores.body() ?: emptyList()).map { it.copy() }

                val respMinhas = withContext(Dispatchers.IO) {
                    service.getMinhasCartas(mesaId, "Bearer $accessToken")
                }
                minhasCartas = respMinhas.body()?.cartas ?: emptyList()
                maoFormada = respMinhas.body()?.mao_formada ?: ""

                jogadores.find { it.user_id == userIdToken }?.let {
                    stackJogador = it.saldo_atual
                }

                val respComunitarias = withContext(Dispatchers.IO) {
                    service.getCartasComunitarias(mesaId, "Bearer $accessToken")
                }
                cartas = respComunitarias.body()?.cartas_comunitarias
                Log.d("\uD83D\uDD25 MesaDebug", "\uD83C\uDCCF Cartas comunitárias: $cartas")

                if (faseDaRodada == "showdown") {
                    val respShow = withContext(Dispatchers.IO) {
                        service.getShowdown(mesaId, "Bearer $accessToken")
                    }
                    Log.d("\uD83D\uDD25 ShowdownDebug", "✅ Status: ${respShow.code()} - Body: ${respShow.body()}")
                    if (respShow.isSuccessful) showdownInfo = respShow.body()
                } else {
                    showdownInfo = null
                }

                Log.d("\uD83D\uDD25 MesaDebug", "\uD83C\uDCCF Minhas cartas: $minhasCartas | Mão formada: $maoFormada")

            } catch (e: Exception) {
                Log.e("\uD83D\uDD25 MesaDebug", "❌ Erro ao atualizar mesa: ${e.message}")
            }
        }
    }

    val websocketClient = remember(mesaId) {
        WebSocketClient(
            mesaId = mesaId,
            onRevelarCartas = { jogadorId ->
                jogadores = jogadores.map { jogador ->
                    if (jogador.user_id == jogadorId) jogador.copy(participando_da_rodada = true)
                    else jogador
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        websocketClient.connect()
    }

    DisposableEffect(Unit) {
        onDispose {
            websocketClient.disconnect()
        }
    }

    LaunchedEffect(Unit) {
        refreshMesa()
        delay(500)
        while (true) {
            refreshMesa()
            delay(2000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(modifier = Modifier.align(Alignment.Center)) { MesaImagemDeFundo() }
        Box(modifier = Modifier.align(Alignment.TopStart)) {
            BotaoSair(context, mesaId, accessToken, coroutineScope)
        }
        Box(modifier = Modifier.align(Alignment.Center)) {
            CartasComunitarias(cartas = cartas, context)
        }

        if (faseDaRodada == "showdown" && showdownInfo != null) {
            Text(
                text = "\uD83C\uDFC6 Vencedor(es): ${showdownInfo!!.vencedores.joinToString()} | ${showdownInfo!!.mao_formada}",
                color = Color.Yellow,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = 300.dp, y = 236.dp)
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CartasDoJogador(minhasCartas, context)
                if (maoFormada.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Mão formada: $maoFormada", color = Color.White)
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
            ControlesDeAcao(
                jogadores = jogadores,
                userIdToken = userIdToken,
                mostrarSlider = mostrarSlider,
                raiseValue = raiseValue,
                stackJogador = stackJogador,
                mesaId = mesaId,
                accessToken = accessToken,
                coroutineScope = coroutineScope,
                onSliderChange = { raiseValue = it },
                onMostrarSlider = { mostrarSlider = true },
                onEsconderSlider = { mostrarSlider = false },
                onRefresh = { refreshMesa() }
            )
        }

        mesa?.let {
            AvataresNaMesa(
                jogadores = jogadores,
                jogadorDaVezId = jogadorDaVezId,
                usuarioLogadoId = usuarioLogadoId,
                faseDaRodada = faseDaRodada,
                poteTotal = it.pote_total.toFloat(),
                apostaAtualMesa = it.aposta_atual.toFloat()
            )
        }
    }
} // *
