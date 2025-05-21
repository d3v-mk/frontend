package com.panopoker.model

data class ShowdownDto(
    val vencedores: List<String>,
    val mao_formada: String,
    val pote: Float,
    val cartas_vencedoras_comunitarias: List<String> = emptyList(),
    val cartas_vencedoras_jogador: Map<String, List<String>> = emptyMap()
)


