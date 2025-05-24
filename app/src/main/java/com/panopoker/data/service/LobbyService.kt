package com.panopoker.data.service

import com.panopoker.model.NoticiaDto
import retrofit2.Response
import retrofit2.http.*


interface LobbyService {
    @GET("lobby/noticias")
    suspend fun getNoticias(
        @Header("Authorization") auth: String,
        @Query("limit") limit: Int = 1
    ): Response<List<NoticiaDto>>

    @GET("lobby/noticias/admin")
    suspend fun getNoticiasAdmin(
        @Header("Authorization") auth: String,  // garante segurança também aqui
        @Query("limit") limit: Int = 1
    ): Response<List<NoticiaDto>>
}

