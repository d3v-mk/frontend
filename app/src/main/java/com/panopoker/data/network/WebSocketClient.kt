package com.panopoker.network

import android.util.Log
import okhttp3.*
import org.json.JSONObject

class WebSocketClient(
    private val mesaId: Int,
    private val onRevelarCartas: (jogadorId: Int) -> Unit = {},
    private val onMesaAtualizada: () -> Unit = {},
    private val token: String,
    private val onNovaFase: (estado: String, novasCartas: List<String>) -> Unit = { _, _ -> },
    private val onShowdown: (JSONObject) -> Unit = {},
    private val onSomJogada: (String) -> Unit = {}, // <-- novo callback
    private val onMatchEncontrado: (mesaId: Int) -> Unit = {},
    private val onRemovidoSemSaldo: () -> Unit = {},


    private val onVezAtualizada: (jogadorId: Int, timestamp: Long) -> Unit = { _, _ -> },

    private val tipoMatch: String = ""

) {
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    fun connect() {
        val request = Request.Builder()
            .url("ws://192.168.0.9:8000/ws/mesa/$mesaId") // IPZADA
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                // 1) manda o auth
                val auth = JSONObject().apply {
                    put("type","auth"); put("token",token)
                }
                ws.send(auth.toString())
                Log.d("WS","🔒 auth enviado")

                // 2) se for matchmaking já manda o match, senão, se for mesa real, entra nela:
                if (tipoMatch.isNotEmpty()) {
                    val m = JSONObject().apply {
                        put("action","matchmaking"); put("tipo",tipoMatch)
                    }
                    ws.send(m.toString())
                    Log.d("WS","🎯 matchmaking enviado")
                } else if (mesaId != 0) {
                    val entrar = JSONObject().put("action","entrar")
                    ws.send(entrar.toString())
                    Log.d("WS","✅ entrar na mesa enviado")
                }
            }


            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WS_RECEBIDO", text) // <-- Adiciona isso!
                try {
                    val json = JSONObject(text)

                    // Aqui detecta o "ping" vindo do backend (que você manda como {"evento": "ping"})
                    if (json.optString("evento") == "ping") {
                        // Responde com "pong"
                        val pong = JSONObject()
                        pong.put("evento", "pong")
                        webSocket.send(pong.toString())
                        Log.d("WS", "🏓 Pong enviado em resposta ao ping")
                        return // Não precisa processar mais nada
                    }

                    if (json.optString("tipo") == "revelar_cartas") {
                        val jogadorId = json.getInt("jogador_id")
                        Log.d("WS", "🎯 Revelação recebida de $jogadorId")
                        onRevelarCartas(jogadorId)
                    }

                    if (json.optString("type") == "removido_sem_saldo") {
                        Log.d("WS", "🚪 Jogador foi removido por saldo zerado")
                        onRemovidoSemSaldo()
                        return // 👈 evita processar mais coisa desnecessária
                    }

                    when (json.optString("evento")) {
                        "mesa_atualizada" -> {
                            Log.d("WS", "🔁 Recebido evento: mesa_atualizada")
                            onMesaAtualizada()
                        }

                        "som_jogada" -> {
                            val tipo = json.optString("tipo", "")
                            Log.d("WS", "🔊 Som recebido: $tipo")
                            onSomJogada(tipo)
                        }


                        "match_encontrado" -> {
                            val mesaId = json.getInt("mesa_id")
                            Log.d("WS", "🎯 Matchmaking encontrou mesa: $mesaId")
                            // Chama callback ou navega direto
                            onMatchEncontrado(mesaId)
                        }

                        "fase_avancada" -> {
                            val estado = json.optString("estado_rodada")
                            val novasCartasJson = json.optJSONArray("novas_cartas")
                            val cartas = mutableListOf<String>()
                            if (novasCartasJson != null) {
                                for (i in 0 until novasCartasJson.length()) {
                                    cartas.add(novasCartasJson.getString(i))
                                }
                            }
                            Log.d("WS", "📥 Nova fase: $estado com cartas $cartas")
                            onNovaFase(estado, cartas)
                        }

                        "vez_atualizada" -> {
                            val jogadorId = json.optInt("jogador_id", -1)
                            val timestampInicio = json.optLong("vez_timestamp", System.currentTimeMillis())

                            Log.d("WS", "🎯 vez_atualizada | jogadorId=$jogadorId | ts=$timestampInicio")

                            if (jogadorId != -1) {
                                onVezAtualizada(jogadorId, timestampInicio)
                            }
                        }

                        "showdown" -> {
                            val dados = json.optJSONObject("dados")
                            if (dados != null) {
                                Log.d("WS", "🔥 Showdown recebido: $dados")
                                onShowdown(dados)
                            } else {
                                Log.w("WS", "⚠️ Showdown recebido mas 'dados' é null")
                            }
                        }
                        "nova_rodada" -> {
                            Log.d("WS", "🆕 Nova rodada começou")
                            onMesaAtualizada()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WS", "Erro ao processar mensagem: ${e.message}")
                }
            }
        })
    }

    // ---- Envio universal de ações WS! ----
    fun enviarAcao(acao: String, valor: Any? = null) {
        val msg = JSONObject()
        msg.put("action", acao)
        if (valor != null) msg.put("valor", valor)
        Log.d("WS", "Enviando $acao pelo WS")
        webSocket.send(msg.toString())
    }

    // Atalhos de ação poker (pra usar fácil no UI)
    fun enviarCall()  = enviarAcao("call")
    fun enviarCheck() = enviarAcao("check")
    fun enviarFold()  = enviarAcao("fold")
    fun enviarAllin() = enviarAcao("allin")
    fun enviarRaise(valor: Number) = enviarAcao("raise", valor)
    fun sairDaMesa() = enviarAcao("sair")

    fun disconnect() {
        if (::webSocket.isInitialized) {
            webSocket.cancel()
        }
    }
}
