package com.panopoker.ui.utils

import android.util.Log
import org.json.JSONObject
import com.panopoker.model.ShowdownDto
import com.panopoker.model.JogadorShowdownDto
import com.panopoker.model.CartaUtilizadaDto

fun processarShowdown(json: JSONObject): ShowdownDto {
    val mesaId = json.optInt("mesa_id")
    val vencedores = json.optJSONArray("vencedores")?.let { arr ->
        List(arr.length()) { arr.getInt(it) }
    } ?: emptyList()
    val pote = json.optDouble("pote", 0.0).toFloat()
    val showdownList = json.optJSONArray("showdown")?.let { arr ->
        List(arr.length()) { i ->
            val jObj = arr.getJSONObject(i)
            JogadorShowdownDto(
                jogador_id = jObj.getInt("jogador_id"),
                cartas = jObj.optJSONArray("cartas")?.let { cArr ->
                    List(cArr.length()) { cArr.getString(it) }
                } ?: emptyList(),
                tipo_mao = jObj.getInt("tipo_mao"),
                descricao_mao = jObj.optString("descricao_mao"),
                valores_mao = jObj.optJSONArray("valores_mao")?.let { vArr ->
                    List(vArr.length()) { vArr.getInt(it) }
                } ?: emptyList(),
                foldado = jObj.optBoolean("foldado", false),
                cartas_utilizadas = jObj.optJSONArray("cartas_utilizadas")?.let { uArr ->
                    List(uArr.length()) { idx ->
                        val uObj = uArr.getJSONObject(idx)
                        CartaUtilizadaDto(
                            carta = uObj.getString("carta"),
                            origem = uObj.getString("origem"),
                            indice = uObj.getInt("indice"),
                            tipo = uObj.getString("tipo")
                        )
                    }
                } ?: emptyList()
            )
        }
    } ?: emptyList()

    return ShowdownDto(
        mesa_id = mesaId,
        showdown = showdownList,
        vencedores = vencedores,
        pote = pote
    )
}
