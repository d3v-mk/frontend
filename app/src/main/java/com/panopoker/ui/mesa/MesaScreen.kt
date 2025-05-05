package com.panopoker.ui.mesa

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import com.panopoker.data.session.SessionManager
import com.panopoker.model.CartasComunitarias
import com.panopoker.model.Jogador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

// Componentes
import com.panopoker.ui.mesa.components.BotaoSair
import com.panopoker.ui.mesa.components.CartasComunitarias as CartasComunitariasComponent
import com.panopoker.ui.mesa.components.CartasDoJogador
import com.panopoker.ui.mesa.components.ControlesDeAcao
import com.panopoker.ui.mesa.components.MesaImagemDeFundo
import com.panopoker.model.MesaResponse

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
    var jogadorDaVezId by remember { mutableStateOf<Int?>(null) }
    var stackJogador by remember { mutableFloatStateOf(1f) }
    var raiseValue by remember { mutableFloatStateOf(0f) }
    var mostrarSlider by remember { mutableStateOf(false) }

    var mesa by remember { mutableStateOf<MesaResponse?>(null) }
    val usuarioLogadoId = session.fetchUserId()

    LaunchedEffect(mostrarSlider) {
        if (mostrarSlider) {
            val jogadorAtual = jogadores.find { it.user_id == userIdToken }
            val saldo = jogadorAtual?.saldo_atual ?: 0.01f
            raiseValue = (saldo / 2f).coerceIn(0.01f, saldo)
        }
    }

    fun refreshMesa() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val service = RetrofitInstance.retrofit.create(MesaService::class.java)

                val mesaResponse = service.getMesa(mesaId, "Bearer $accessToken")
                val mesaBody = mesaResponse.body()
                mesa = mesaBody
                faseDaRodada = mesaBody?.estado_da_rodada
                jogadorDaVezId = mesaBody?.jogador_da_vez

                val responseJogadores = service.getJogadoresDaMesa(mesaId, "Bearer $accessToken")
                val responseMinhasCartas = service.getMinhasCartas(mesaId, "Bearer $accessToken")
                val responseCartasComunitarias = service.getCartasComunitarias(mesaId, "Bearer $accessToken")

                jogadores = responseJogadores.body() ?: emptyList()
                cartas = responseCartasComunitarias.body()?.cartas_comunitarias
                minhasCartas = responseMinhasCartas.body() ?: emptyList()

                jogadores.find { it.user_id == userIdToken }?.let {
                    stackJogador = it.saldo_atual
                }

                Log.d("\uD83D\uDD25 MesaDebug", "\uD83D\uDCCC Minhas cartas: $minhasCartas")

            } catch (e: Exception) {
                Log.e("\uD83D\uDD25 MesaDebug", "\u274C Erro ao atualizar mesa: ${e.message}")
            }
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

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.align(Alignment.Center)) {
            MesaImagemDeFundo()
        }
        Box(modifier = Modifier.align(Alignment.TopStart)) {
            BotaoSair(context, mesaId, accessToken, coroutineScope)
        }
        Box(modifier = Modifier.align(Alignment.Center)) {
            CartasComunitariasComponent(cartas = cartas, context)
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            CartasDoJogador(minhasCartas, context)
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
            val apostaAtualMesa = it.aposta_atual.toFloat()

            AvataresNaMesa(
                jogadores = jogadores,
                jogadorDaVezId = jogadorDaVezId,
                usuarioLogadoId = usuarioLogadoId,
                faseDaRodada = faseDaRodada,
                poteTotal = it.pote_total.toFloat(),
                apostaAtualMesa = apostaAtualMesa
            )
        }
    }
}
