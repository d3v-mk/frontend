package com.panopoker.model

data class SaqueDto(
    val id: Int,
    val jogador_id: Int,
    val promotor_id: Int,
    val valor: Double,
    val status: String
)

data class ConfirmarSaqueRequest(
    val valor_digitado: String
)