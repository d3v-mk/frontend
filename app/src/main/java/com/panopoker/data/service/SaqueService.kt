package com.panopoker.service

import com.panopoker.model.SaqueDto
import com.panopoker.model.ConfirmarSaqueRequest
import retrofit2.http.*

interface SaqueService {
    @GET("/saques/me")
    suspend fun getMeuSaque(@Header("Authorization") token: String): SaqueDto

    @POST("/saques/{id}/confirmar")
    suspend fun confirmarSaque(
        @Path("id") id: Int,
        @Body request: ConfirmarSaqueRequest,
        @Header("Authorization") token: String
    )
}
