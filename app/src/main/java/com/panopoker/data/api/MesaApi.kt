package com.panopoker.data.api

import com.panopoker.model.Mesa
import retrofit2.Response
import retrofit2.http.GET

interface MesaApi {
    @GET("/lobby/disponiveis")
    suspend fun getMesasDisponiveis(): Response<List<Mesa>>
}
