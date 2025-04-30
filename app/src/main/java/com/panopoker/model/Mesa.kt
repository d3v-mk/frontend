package com.panopoker.model

data class Mesa(
    val id: Int,
    val nome: String,
    val buy_in: Double,
    val status: String,
    val jogadores: Int,
    val jogadores_atuais: Int,
    val tipo_jogo: String,
    val valor_minimo_aposta: Double,
    val estado_da_rodada: String? = null
)
