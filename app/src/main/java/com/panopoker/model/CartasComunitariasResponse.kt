package com.panopoker.model

import com.google.gson.annotations.SerializedName

data class CartasComunitariasResponse(
    @SerializedName("cartas_comunitarias")
    val cartas_comunitarias: CartasComunitarias
)
