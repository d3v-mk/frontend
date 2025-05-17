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
import androidx.compose.ui.zIndex
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

    // Estados principais
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

    // Controle de slider
    LaunchedEffect(mostrarSlider) {
        if (mostrarSlider) {
            val jogadorAtual = jogadores.find { it.user_id == userIdToken }
            val saldo = jogadorAtual?.saldo_atual ?: 0.01f
            raiseValue = (saldo / 2f).coerceIn(0.01f, saldo)
        }
    }

    // Função de refresh da mesa
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

                if (faseDaRodada == "showdown") {
                    val respShow = withContext(Dispatchers.IO) {
                        service.getShowdown(mesaId, "Bearer $accessToken")
                    }
                    if (respShow.isSuccessful) showdownInfo = respShow.body()
                } else {
                    showdownInfo = null
                }
            } catch (e: Exception) {
                Log.e("\uD83D\uDD25 MesaDebug", "❌ Erro ao atualizar mesa: ${e.message}")
            }
        }
    }

    // WebSocket
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

    LaunchedEffect(Unit) { websocketClient.connect() }
    DisposableEffect(Unit) { onDispose { websocketClient.disconnect() } }

    // Loop de atualização
    LaunchedEffect(Unit) {
        refreshMesa()
        delay(500)
        while (true) {
            refreshMesa()
            delay(2000)
        }
    }

    // Layout principal da mesa
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Imagem de fundo
        Box(modifier = Modifier.align(Alignment.Center).zIndex(0f)) {
            MesaImagemDeFundo()
        }

        // Botão de sair
        Box(modifier = Modifier.align(Alignment.TopStart)) {
            BotaoSair(context, mesaId, accessToken, coroutineScope)
        }

        // Cartas comunitárias
        Box(modifier = Modifier.align(Alignment.Center)) {
            CartasComunitarias(cartas = cartas, context = context)
        }

        // Vencedores do showdown
        if (faseDaRodada == "showdown") {
            showdownInfo?.let { info ->
                VencedoresShowdown(
                    vencedores = info.vencedores,
                    maoFormada = info.mao_formada
                )
            }
        }

        // Fichas do pote
        MainPot(
            poteTotal = mesa?.pote_total?.toFloat() ?: 0f,
            faseDaRodada = faseDaRodada
        )

        // Controles de ação
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

        // Avatares na mesa
        mesa?.let {
            AvataresNaMesa(
                jogadores = jogadores,
                jogadorDaVezId = jogadorDaVezId,
                usuarioLogadoId = usuarioLogadoId,
                faseDaRodada = faseDaRodada,
                poteTotal = it.pote_total.toFloat(),
                maoFormada = maoFormada,
                apostaAtualMesa = it.aposta_atual.toFloat()
            )
        }
    }
}
