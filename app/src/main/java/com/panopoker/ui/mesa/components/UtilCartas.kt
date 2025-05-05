package com.panopoker.ui.mesa.components

fun nomeDaCarta(carta: String): String {
    val rankCode = carta.dropLast(1)
    val suitCode = carta.last()

    val rankName = when (rankCode) {
        "2" -> "dois"; "3" -> "tres"; "4" -> "quatro"; "5" -> "cinco"
        "6" -> "seis"; "7" -> "sete"; "8" -> "oito"; "9" -> "nove"; "10" -> "dez"
        "J" -> "valete"; "Q" -> "dama"; "K" -> "rei"; "A" -> "as"
        else -> rankCode.lowercase()
    }

    val suitName = when (suitCode) {
        'P' -> "paus"; 'C' -> "copas"; 'E' -> "espadas"; 'O' -> "ouros"
        else -> "desconhecido"
    }

    return "${rankName}_de_${suitName}"
}
