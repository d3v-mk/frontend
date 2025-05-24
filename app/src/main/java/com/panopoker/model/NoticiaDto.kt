package com.panopoker.model

data class NoticiaDto(
    val id: Int,
    val mensagem: String,
    val tipo: String,
    val criada_em: String,
    val mesa_id: Int?,
    val usuario_id: Int?
)
