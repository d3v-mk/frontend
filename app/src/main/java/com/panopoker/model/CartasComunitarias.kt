package com.panopoker.model

data class CartasComunitarias(
    val flop: List<String> = emptyList(),
    val turn: String? = null,
    val river: String? = null
)
