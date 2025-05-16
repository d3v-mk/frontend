package com.panopoker.network

import android.util.Log
import okhttp3.*
import org.json.JSONObject

class WebSocketClient(
    private val mesaId: Int,
    private val onRevelarCartas: (jogadorId: Int) -> Unit
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
                    if (json.getString("tipo") == "revelar_cartas") {
                        val jogadorId = json.getInt("jogador_id")
                        Log.d("WS", "🎯 Revelação recebida de $jogadorId")
                        onRevelarCartas(jogadorId)
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
