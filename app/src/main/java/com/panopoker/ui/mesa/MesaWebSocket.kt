// MesaWebSocket.kt


import android.util.Log
import com.panopoker.model.CartaGlowInfo
import com.panopoker.data.network.WebSocketClient
import com.panopoker.ui.mesa.MesaState
import com.panopoker.ui.utils.processarShowdown
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun createMesaWebSocketClient(
    mesaId: Int,
    accessToken: String,
    state: MesaState,
    coroutineScope: CoroutineScope,
    userIdToken: Int,
    reproduzirSom: (String) -> Unit,
    resetarTimerJogadorDaVez: (MesaState, Long, CoroutineScope) -> Unit,
    refreshMesa: (MesaState, CoroutineScope, Int, String, Int) -> Unit
): WebSocketClient {
    return WebSocketClient(
        mesaId = mesaId,
        token = accessToken,

        onRevelarCartas = { jogadorId ->
            state.jogadores.value = state.jogadores.value.map { jogador ->
                if (jogador.user_id == jogadorId) jogador.copy(participando_da_rodada = true)
                else jogador
            }
        },

        onVezAtualizada = { jogadorId, timestamp ->
            state.timestampRecebidoLocalmente.value = System.currentTimeMillis()

            Log.d("TIMER_DEBUG", "Vez atualizada para jogadorId: $jogadorId, timestamp: $timestamp")

            resetarTimerJogadorDaVez(state, timestamp, coroutineScope)

            if (jogadorId != userIdToken) {
                state.progressoTimer.value = 0f
                state.timerJob.value?.cancel()
            }
        },

        onRemovidoSemSaldo = {
            //state.showSemFichasDialog.value = true
        },

        onSomJogada = { tipo ->
            Log.d("SOM_DEBUG", "Recebido som_jogada: $tipo")
            reproduzirSom(tipo)
        },

        onMesaAtualizada = {
            Log.d("WS", "🌀 Atualizando mesa via WebSocket")
            refreshMesa(state, coroutineScope, mesaId, accessToken, userIdToken)
        },

        onNovaFase = { estado, novasCartas ->
            Log.d("WS", "🌊 Nova fase: $estado")

            if (estado == "pre-flop") {
                state.cartasGlowComunitarias.value = emptyList()
                state.cartasGlowDoJogador.value = emptyMap()
            }

            state.estadoRodada.value = estado
            state.cartasComunitarias.value = state.cartasComunitarias.value + novasCartas
        },

        onShowdown = { json ->
            val showdown = processarShowdown(json)
            state.showdownInfo.value = showdown

            state.showdownInfoPersistente.value = showdown
            state.showVencedores.value = true

            coroutineScope.launch {
                delay(8000)
                state.showVencedores.value = false
                state.showdownInfoPersistente.value = null
            }

            state.cartasGlowDoJogador.value = showdown.showdown
                .filter { showdown.vencedores.contains(it.jogador_id) }
                .associate { it.jogador_id to it.cartas_utilizadas.map { c -> CartaGlowInfo(c.carta, c.indice) } }

            state.cartasGlowComunitarias.value = showdown.showdown
                .filter { showdown.vencedores.contains(it.jogador_id) }
                .flatMap { it.cartas_utilizadas }
                .filter { it.origem == "mesa" }
                .map { CartaGlowInfo(it.carta, it.indice) }
                .distinct()
        }
    )
}
