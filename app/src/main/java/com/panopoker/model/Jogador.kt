package com.panopoker.model

data class Jogador(
    val id: Int,
    val user_id: Int,
    val username: String,
    val email: String,
    val is_admin: Boolean,
    val saldo_inicial: Float,
    val saldo_atual: Float,
    val aposta_atual: Float,
    val foldado: Boolean,
    val rodada_ja_agiu: Boolean,
    val cartas: List<String>,
    var vez: Boolean,
    val posicao_cadeira: Int = 0,
    val participando_da_rodada: Boolean,
    val is_sb: Boolean,
    val is_bb: Boolean,
    )


