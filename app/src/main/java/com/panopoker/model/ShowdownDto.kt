package com.panopoker.model

data class CartaUtilizadaDto(
    val carta: String,
    val origem: String,
    val indice: Int,
    val tipo: String
)

data class JogadorShowdownDto(
    val jogador_id: Int,
    val cartas: List<String>,
    val tipo_mao: Int,
    val descricao_mao: String,
    val valores_mao: List<Int>,
    val foldado: Boolean,
    val cartas_utilizadas: List<CartaUtilizadaDto>
)

data class ShowdownDto(
    val mesa_id: Int,
    val showdown: List<JogadorShowdownDto>,
    val vencedores: List<Int>,
    val pote: Float  // 👈 Só manter se backend mandar!
)



