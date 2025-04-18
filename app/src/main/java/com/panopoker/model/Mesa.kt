package com.panopoker.model

data class Mesa(
    val id: Int,
    val status: String,
    val limite_jogadores: Int,
    val jogadores_atuais: Int,
    val tipo_jogo: String,
    val valor_minimo_aposta: Double
)
