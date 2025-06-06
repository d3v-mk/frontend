package com.panopoker.model

data class MesaDto(
    val id: Int,
    val rodada_id: Int,
    val nome: String,
    val buy_in: Double,
    val status: String,
    val limite_jogadores: Int,
    val jogador_da_vez: Int?,
    val estado_da_rodada: String,
    val dealer_pos: Int?,
    val small_blind: Double,
    val big_blind: Double,
    val pote_total: Double,
    val aposta_atual: Double,
    val cartas_comunitarias: CartasComunitarias,
    val vez_timestamp: Long? = null
)

