package com.panopoker.ui.utils

import android.content.Context
import com.panopoker.R

fun getCartaDrawable(context: Context, carta: String): Int {
    if (carta.length < 2) return R.drawable.carta_back

    val valor = carta.dropLast(1)
    val naipe = carta.last()

    val valorTexto = when (valor) {
        "2" -> "dois"
        "3" -> "tres"
        "4" -> "quatro"
        "5" -> "cinco"
        "6" -> "seis"
        "7" -> "sete"
        "8" -> "oito"
        "9" -> "nove"
        "10" -> "dez"
        "J" -> "valete"
        "Q" -> "dama"
        "K" -> "rei"
        "A" -> "as"
        else -> "desconhecido"
    }

    val naipeTexto = when (naipe) {
        'P' -> "paus"
        'C' -> "copas"
        'E' -> "espadas"
        'O' -> "ouros"
        else -> "desconhecido"
    }

    val nome = "${valorTexto}_de_${naipeTexto}"
    return context.resources.getIdentifier(nome, "drawable", context.packageName)
}
