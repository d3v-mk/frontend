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
import com.panopoker.model.ShowdownDto
import com.panopoker.model.Jogador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panopoker.model.MesaDto

// Componentes
import com.panopoker.ui.mesa.components.BotaoSair
import com.panopoker.ui.mesa.components.CartasComunitarias as CartasComunitariasComponent
import com.panopoker.ui.mesa.components.CartasDoJogador
import com.panopoker.ui.mesa.components.ControlesDeAcao
import com.panopoker.ui.mesa.components.MesaImagemDeFundo

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
    var maoFormada by remember { mutableStateOf<String>("") }
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

                // Busca dados da mesa em IO
                val mesaResponse = withContext(Dispatchers.IO) {
                    service.getMesa(mesaId, "Bearer $accessToken")
                }
                val mesaBody = mesaResponse.body()

                // Atualiza estados na Main
                mesa = mesaBody
                faseDaRodada = mesaBody?.estado_da_rodada
                Log.d("🔥 MesaDebug", "🧭 Estado da rodada: $faseDaRodada")
                jogadorDaVezId = mesaBody?.jogador_da_vez

                // Busca jogadores
                val respJogadores = withContext(Dispatchers.IO) {
                    service.getJogadoresDaMesa(mesaId, "Bearer $accessToken")
                }
                jogadores = respJogadores.body() ?: emptyList()

                // Busca minhas cartas
                val respMinhas = withContext(Dispatchers.IO) {
                    service.getMinhasCartas(mesaId, "Bearer $accessToken")
                }
                minhasCartas = respMinhas.body()?.cartas ?: emptyList()
                maoFormada = respMinhas.body()?.mao_formada ?: ""

                // Atualiza stack do jogador logado
                jogadores.find { it.user_id == userIdToken }?.let {
                    stackJogador = it.saldo_atual
                }

                // Busca cartas comunitárias
                val respComunitarias = withContext(Dispatchers.IO) {
                    service.getCartasComunitarias(mesaId, "Bearer $accessToken")
                }
                cartas = respComunitarias.body()?.cartas_comunitarias
                Log.d("🔥 MesaDebug", "🃏 Cartas comunitárias: $cartas")

                // Se for showdown, busca info extra
                if (faseDaRodada == "showdown") {
                    val respShow = withContext(Dispatchers.IO) {
                        service.getShowdown(mesaId, "Bearer $accessToken")
                    }
                    Log.d("🔥 ShowdownDebug", "✅ Status: ${respShow.code()} - Body: ${respShow.body()}")
                    if (respShow.isSuccessful) showdownInfo = respShow.body()
                } else {
                    showdownInfo = null
                }

                Log.d("🔥 MesaDebug", "🃏 Minhas cartas: $minhasCartas | Mão formada: $maoFormada")

            } catch (e: Exception) {
                Log.e("🔥 MesaDebug", "❌ Erro ao atualizar mesa: ${e.message}")
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Fundo e botões fixos
        Box(modifier = Modifier.align(Alignment.Center)) { MesaImagemDeFundo() }
        Box(modifier = Modifier.align(Alignment.TopStart)) { BotaoSair(context, mesaId, accessToken, coroutineScope) }
        Box(modifier = Modifier.align(Alignment.Center)) { CartasComunitariasComponent(cartas = cartas, context) }

        // Resultado showdown
        if (faseDaRodada == "showdown" && showdownInfo != null) {
            Text(
                text = "🏆 Vencedor(es): ${showdownInfo!!.vencedores.joinToString()} | ${showdownInfo!!.mao_formada}",
                color = Color.Yellow,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = 300.dp, y = 236.dp)
            )
        }

        // Cartas do jogador
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CartasDoJogador(minhasCartas, context)
                if (maoFormada.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Mão formada: $maoFormada", color = Color.White)
                }
            }
        }

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

        // Avatares
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
}//*
