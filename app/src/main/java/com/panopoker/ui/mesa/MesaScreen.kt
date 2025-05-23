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
import com.panopoker.model.CartaGlowInfo
import com.panopoker.model.CartasComunitarias
import com.panopoker.model.Jogador
import com.panopoker.model.MesaDto
import com.panopoker.model.PerfilResponse
import com.panopoker.model.ShowdownDto
import com.panopoker.network.WebSocketClient
import com.panopoker.ui.mesa.components.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.panopoker.ui.mesa.components.PerfilDoJogadorDialog
import com.panopoker.ui.utils.processarShowdown

@Composable
fun MesaScreen(
    mesaId: Int,
    navController: NavController? = null,
) {
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

    //
    var cartasComunitarias by remember { mutableStateOf(listOf<String>()) }
    var estadoRodada by remember { mutableStateOf("") }

    // jogador dialog
    val mostrarDialog = remember { mutableStateOf(false) }
    val perfilSelecionado = remember { mutableStateOf<PerfilResponse?>(null) }


    var cartasGlowComunitarias by remember { mutableStateOf<List<CartaGlowInfo>>(emptyList()) }
    var cartasGlowDoJogador by remember { mutableStateOf<Map<Int, List<CartaGlowInfo>>>(emptyMap()) }



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

    fun carregarPerfilDoJogador(userId: Int) {
        Log.d("DialogDebug", "🔍 Buscando perfil do jogador $userId")
        coroutineScope.launch {
            try {
                val token = "Bearer $accessToken"
                val service = RetrofitInstance.usuarioApi
                val response = service.getPerfilDeOutroUsuario(userId, token)

                if (response.isSuccessful) {
                    perfilSelecionado.value = response.body()
                    mostrarDialog.value = true
                } else {
                    Log.e("MesaScreen", "Erro ao buscar perfil: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MesaScreen", "Exceção ao buscar perfil: ${e.message}")
            }
        }
    }



    // WebSocket
    val webSocketClient = remember(mesaId, accessToken) {
        WebSocketClient(
            mesaId = mesaId,
            token = accessToken,
            onRevelarCartas = { jogadorId ->
                jogadores = jogadores.map { jogador ->
                    if (jogador.user_id == jogadorId) jogador.copy(participando_da_rodada = true)
                    else jogador
                }
            },

            onMesaAtualizada = {
                Log.d("WS", "🌀 Atualizando mesa via WebSocket")
                refreshMesa()
            },

            onNovaFase = { estado, novasCartas ->
                Log.d("WS", "🌊 Nova fase: $estado")

                if (estado == "pre-flop") {
                    // 🎉 Nova rodada: limpar brilhos antigos!
                    cartasGlowComunitarias = emptyList()
                    cartasGlowDoJogador = emptyMap()
                }

                estadoRodada = estado
                cartasComunitarias = cartasComunitarias + novasCartas
            },

            onShowdown = { json ->
                val showdown = processarShowdown(json)
                showdownInfo = showdown

                // Glow para TODOS jogadores vencedores
                cartasGlowDoJogador = showdown.showdown
                    .filter { showdown.vencedores.contains(it.jogador_id) }
                    .associate { it.jogador_id to it.cartas_utilizadas.map { c -> CartaGlowInfo(c.carta, c.indice) } }

                cartasGlowComunitarias = showdown.showdown
                    .filter { showdown.vencedores.contains(it.jogador_id) }
                    .flatMap { it.cartas_utilizadas }
                    .filter { it.origem == "mesa" }
                    .map { CartaGlowInfo(it.carta, it.indice) }
                    .distinct()
            }
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
            BotaoSair(context, webSocketClient, coroutineScope)
        }

        // Cartas comunitárias com animação de brilho nas vencedoras
        Box(modifier = Modifier.align(Alignment.Center)) {
            CartasComunitarias(
                cartas = cartas,
                context = context,
                cartasGlow = if (faseDaRodada == "showdown") cartasGlowComunitarias else emptyList()
            )
        }


        // Vencedores do showdown
        if (faseDaRodada == "showdown") {
            showdownInfo?.let { info ->
                VencedoresShowdown(
                    vencedores = info.vencedores,
                    jogadores = jogadores,
                    showdown = info.showdown // <-- lista de jogadores com descrições!
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
                webSocketClient = webSocketClient,
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
                cartasGlowDoJogador = cartasGlowDoJogador,
                apostaAtualMesa = it.aposta_atual.toFloat(),
                onClickJogador = { jogador ->
                    Log.d("MK_DEBUG", "👆 Avatar clicado: ${jogador.username} (${jogador.user_id})") // 👈 AQUI
                    carregarPerfilDoJogador(jogador.user_id)
                }
            )
        }

        // Cartas do jogador logado
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(100f) // Bem acima de todo o resto
        ) {
            CartasDoJogador(
                cartas = minhasCartas,
                context = context,
                cartasGlow = if (faseDaRodada == "showdown") cartasGlowDoJogador[userIdToken] ?: emptyList() else emptyList()
            )
        }

        // mostrar dialog
        if (mostrarDialog.value && perfilSelecionado.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(9999f)
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                PerfilDoJogadorDialog(
                    perfil = perfilSelecionado.value!!,
                    onDismiss = {
                        mostrarDialog.value = false
                        perfilSelecionado.value = null
                    }
                )
            }
        }
    }
}///


