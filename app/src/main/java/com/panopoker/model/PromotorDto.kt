package com.panopoker.model

data class PromotorDto(
    val id: Int,
    val nome: String,
    val slug: String,
    val avatarUrl: String? = null,
    val whatsapp: String? = null
)
