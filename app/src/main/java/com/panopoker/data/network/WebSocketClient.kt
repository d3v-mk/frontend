package com.panopoker.network

import android.util.Log
import okhttp3.*
import org.json.JSONObject

class WebSocketClient(
    private val mesaId: Int,
    private val onRevelarCartas: (jogadorId: Int) -> Unit = {},
    private val onMesaAtualizada: () -> Unit = {},
    private val onNovaFase: (estado: String, novasCartas: List<String>) -> Unit = { _, _ -> },
    private val onShowdown: (JSONObject) -> Unit = {}

) {
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    fun connect() {
        val request = Request.Builder()
            .url("ws://192.168.0.9:8000/ws/mesa/$mesaId") // 🧠 Ajusta IP se mudar
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)

                    if (json.optString("tipo") == "revelar_cartas") {
                        val jogadorId = json.getInt("jogador_id")
                        Log.d("WS", "🎯 Revelação recebida de $jogadorId")
                        onRevelarCartas(jogadorId)
                    }

                    when (json.optString("evento")) {
                        "mesa_atualizada" -> {
                            Log.d("WS", "🔁 Recebido evento: mesa_atualizada")
                            onMesaAtualizada()
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

    fun disconnect() {
        if (::webSocket.isInitialized) {
            webSocket.cancel()
        }
    }
}
