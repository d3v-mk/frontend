package com.panopoker.ui.mesa

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import com.panopoker.data.session.SessionManager
import com.panopoker.ui.mesa.components.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.panopoker.R
import createMesaWebSocketClient
import kotlinx.coroutines.CoroutineScope


@Composable
fun MesaScreen(
    mesaId: Int,
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val accessToken = session.fetchAuthToken() ?: ""
    val userIdToken = session.getUserIdFromToken(accessToken) ?: -99
    val usuarioLogadoId = userIdToken
    val coroutineScope = rememberCoroutineScope()
    val state = rememberMesaState()

    // ========== Slider ==========
    LaunchedEffect(state.mostrarSlider.value) {
        if (state.mostrarSlider.value) {
            val jogadorAtual = state.jogadores.value.find { it.user_id == userIdToken }
            val saldo = jogadorAtual?.saldo_atual ?: 0.01f
            state.raiseValue.value = (saldo / 2f).coerceIn(0.01f, saldo)
        }
    }

    // ========== Sons ==========
    fun tocarSom(resId: Int) {
        val mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }

    fun reproduzirSom(tipo: String) {
        when (tipo) {
            "check" -> tocarSom(R.raw.check)
            "call" -> tocarSom(R.raw.call_voz)
            "raise" -> tocarSom(R.raw.raise_voz)
            "fold" -> tocarSom(R.raw.fold_voz)
            "allin" -> tocarSom(R.raw.allin_voz)
            else -> Log.d("SOM", "🔇 Tipo de som desconhecido: $tipo")
        }
    }

    // ========== Timer ==========
    fun resetarTimerJogadorDaVez(state: MesaState, timestampServidor: Long, coroutineScope: CoroutineScope) {
        Log.d("TIMER_DEBUG", "🔁 resetarTimerJogadorDaVez chamado com timestamp: $timestampServidor")

        state.timerJob.value?.cancel()
        state.progressoTimer.value = 1.0f
        val agora = System.currentTimeMillis()
        state.timestampRecebidoLocalmente.value = agora

        val tempoPassadoDesdeServidor = agora - timestampServidor
        val duracaoTurno = 20_000L
        val tempoRealRestante = duracaoTurno - tempoPassadoDesdeServidor

        if (tempoRealRestante <= 0) {
            state.progressoTimer.value = 0.0f
            return
        }

        state.timerJob.value = coroutineScope.launch {
            val interval = 50L
            val fimTimer = System.currentTimeMillis() + tempoRealRestante

            while (true) {
                val tempoRestanteAtual = fimTimer - System.currentTimeMillis()
                if (tempoRestanteAtual <= 0) break
                state.progressoTimer.value = tempoRestanteAtual.toFloat() / duracaoTurno.toFloat()
                delay(interval)
            }

            state.progressoTimer.value = 0.0f
        }
    }

    // ========== Refresh ==========
    fun refreshMesa(state: MesaState, coroutineScope: CoroutineScope, mesaId: Int, accessToken: String, userIdToken: Int) {
        coroutineScope.launch {
            try {
                val service = RetrofitInstance.retrofit.create(MesaService::class.java)
                val mesaResponse = withContext(Dispatchers.IO) {
                    service.getMesa(mesaId, "Bearer $accessToken")
                }

                val mesaBody = mesaResponse.body()
                state.mostrarDialogManutencao.value = mesaBody?.status == "manutencao"

                state.mesa.value = mesaBody
                state.faseDaRodada.value = mesaBody?.estado_da_rodada
                state.jogadorDaVezId.value = mesaBody?.jogador_da_vez

                if (mesaBody?.status == "aberta") {
                    state.cartasComunitarias.value = emptyList()
                    state.cartas.value = null
                    state.showdownInfo.value = null
                    state.estadoRodada.value = ""
                    state.cartasGlowComunitarias.value = emptyList()
                    state.cartasGlowDoJogador.value = emptyMap()
                }

                val timestamp = mesaBody?.vez_timestamp
                if (mesaBody != null && mesaBody.rodada_id != state.lastRodadaId.value) {
                    resetarTimerJogadorDaVez(state, timestamp ?: System.currentTimeMillis(), coroutineScope)
                    state.lastRodadaId.value = mesaBody.rodada_id
                    state.lastJogadorDaVezId.value = mesaBody.jogador_da_vez
                } else if (state.jogadorDaVezId.value != state.lastJogadorDaVezId.value) {
                    resetarTimerJogadorDaVez(state, timestamp ?: System.currentTimeMillis(), coroutineScope)
                    state.lastJogadorDaVezId.value = state.jogadorDaVezId.value
                }

                val jogadoresResp = withContext(Dispatchers.IO) {
                    service.getJogadoresDaMesa(mesaId, "Bearer $accessToken")
                }
                state.jogadores.value = jogadoresResp.body()?.map { it.copy() } ?: emptyList()

                val minhasCartasResp = withContext(Dispatchers.IO) {
                    service.getMinhasCartas(mesaId, "Bearer $accessToken")
                }
                state.minhasCartas.value = minhasCartasResp.body()?.cartas ?: emptyList()
                state.maoFormada.value = minhasCartasResp.body()?.mao_formada ?: ""

                state.jogadores.value.find { it.user_id == userIdToken }?.let {
                    state.stackJogador.value = it.saldo_atual
                }

                val comunitariasResp = withContext(Dispatchers.IO) {
                    service.getCartasComunitarias(mesaId, "Bearer $accessToken")
                }
                state.cartas.value = comunitariasResp.body()?.cartas_comunitarias

                if (state.faseDaRodada.value == "showdown") {
                    val respShow = withContext(Dispatchers.IO) {
                        service.getShowdown(mesaId, "Bearer $accessToken")
                    }
                    if (respShow.isSuccessful) {
                        state.showdownInfo.value = respShow.body()
                    }
                } else {
                    state.showdownInfo.value = null
                }

            } catch (e: Exception) {
                Log.e("🔥 MesaDebug", "❌ Erro ao atualizar mesa: ${e.message}")
            }
        }
    }

    // ========== Perfil ==========
    fun carregarPerfilDoJogador(state: MesaState, userId: Int, coroutineScope: CoroutineScope, accessToken: String) {
        coroutineScope.launch {
            try {
                val token = "Bearer $accessToken"
                val service = RetrofitInstance.usuarioApi
                val response = service.getPerfilDeOutroUsuario(userId, token)
                if (response.isSuccessful) {
                    state.perfilSelecionado.value = response.body()
                    state.mostrarDialog.value = true
                }
            } catch (e: Exception) {
                Log.e("MesaScreen", "Exceção ao buscar perfil: ${e.message}")
            }
        }
    }

    // ========== WebSocket ==========
    val webSocketClient = remember(mesaId, accessToken) {
        createMesaWebSocketClient(
            mesaId,
            accessToken,
            state,
            coroutineScope,
            userIdToken,
            ::reproduzirSom,
            ::resetarTimerJogadorDaVez,
            ::refreshMesa
        )
    }

    LaunchedEffect(mesaId) {
        webSocketClient.connect()
    }

    DisposableEffect(Unit) {
        onDispose {
            webSocketClient.disconnect()
        }
    }

    // ========== UI Layout ==========
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Fundo
        Box(modifier = Modifier.align(Alignment.Center).zIndex(0f)) {
            MesaImagemDeFundo()
        }

        // Botão de sair
        Box(modifier = Modifier.align(Alignment.TopStart)) {
            BotaoHamburguerMesa(context, webSocketClient, coroutineScope)
        }

        // Cartas comunitárias
        Box(modifier = Modifier.align(Alignment.Center)) {
            CartasComunitarias(
                cartas = state.cartas.value,
                context = context,
                cartasGlow = if (state.faseDaRodada.value == "showdown") state.cartasGlowComunitarias.value else emptyList()
            )
        }

        // Dialogs
        MesaDialogs(
            mostrarDialogManutencao = state.mostrarDialogManutencao,
            showVencedores = state.showVencedores.value,
            showdownInfoPersistente = state.showdownInfoPersistente.value,
            jogadores = state.jogadores.value,
            perfilSelecionado = state.perfilSelecionado,
            mostrarDialog = state.mostrarDialog,
            showSemFichasDialog = state.showSemFichasDialog.value
        )

        // Pote principal
        MainPot(
            poteTotal = state.mesa.value?.pote_total?.toFloat() ?: 0f,
            faseDaRodada = state.faseDaRodada.value ?: ""
        )

        // Controles de ação
        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
            ControlesDeAcao(
                jogadores = state.jogadores.value,
                userIdToken = userIdToken,
                mostrarSlider = state.mostrarSlider.value,
                raiseValue = state.raiseValue.value,
                stackJogador = state.stackJogador.value,
                mesaId = mesaId,
                accessToken = accessToken,
                coroutineScope = coroutineScope,
                onSliderChange = { state.raiseValue.value = it },
                onMostrarSlider = { state.mostrarSlider.value = true },
                onEsconderSlider = { state.mostrarSlider.value = false },
                webSocketClient = webSocketClient,
            )
        }

        // Avatares
        state.mesa.value?.let { mesa ->
            AvataresNaMesa(
                jogadores = state.jogadores.value,
                jogadorDaVezId = state.jogadorDaVezId.value,
                usuarioLogadoId = usuarioLogadoId,
                faseDaRodada = state.faseDaRodada.value ?: "",
                poteTotal = mesa.pote_total.toFloat(),
                maoFormada = state.maoFormada.value,
                progressoTimer = state.progressoTimer.value,
                cartasGlowDoJogador = state.cartasGlowDoJogador.value,
                apostaAtualMesa = mesa.aposta_atual.toFloat(),
                onClickJogador = { jogador ->
                    carregarPerfilDoJogador(state, jogador.user_id, coroutineScope, accessToken)
                }
            )
        }

        // Cartas do jogador logado
        Box(modifier = Modifier.align(Alignment.Center).zIndex(100f)) {
            CartasDoJogador(
                cartas = state.minhasCartas.value,
                context = context,
                cartasGlow = if (state.faseDaRodada.value == "showdown")
                    state.cartasGlowDoJogador.value[userIdToken] ?: emptyList()
                else emptyList()
            )
        }
    }
}
