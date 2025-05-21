package com.panopoker.ui.utils

import android.util.Log
import org.json.JSONObject
import com.panopoker.model.ShowdownDto

fun processarShowdown(json: JSONObject): ShowdownDto {
    Log.d("WS", "👑 Showdown recebido: $json")

    val vencedores = json.optJSONArray("vencedores")?.let { arr ->
        List(arr.length()) { arr.getString(it) }
    } ?: emptyList()

    val maoFormada = json.optString("mao_formada") ?: ""
    val pote = json.optDouble("pote", 0.0).toFloat()

    val cartasMesa = json.optJSONArray("cartas_vencedoras_comunitarias")?.let { arr ->
        List(arr.length()) { arr.getString(it) }
    } ?: emptyList()

    val cartasPorJogador = mutableMapOf<String, List<String>>()
    val mapCartas = json.optJSONObject("cartas_vencedoras_jogador")
    if (mapCartas != null) {
        mapCartas.keys().forEach { userId ->
            val cartas = mapCartas.getJSONArray(userId)
            cartasPorJogador[userId] = List(cartas.length()) { cartas.getString(it) }
        }
    }

    return ShowdownDto(
        vencedores = vencedores,
        mao_formada = maoFormada,
        pote = pote,
        cartas_vencedoras_comunitarias = cartasMesa,
        cartas_vencedoras_jogador = cartasPorJogador
    )
}
