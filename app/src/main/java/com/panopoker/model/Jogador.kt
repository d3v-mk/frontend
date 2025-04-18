package com.panopoker.model

data class Jogador(
    val id: Int,
    val user_id: Int,
    val username: String,
    val stack: Float,
    val saldo_restante: Float,
    val aposta_atual: Float,
    val foldado: Boolean
)
