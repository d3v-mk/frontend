package com.panopoker.model

data class ShowdownResponse(
    val vencedores: List<String>,  // <-- AQUI muda de Int pra String (nomes!)
    val mao_formada: String,
    val pote: Float
)

