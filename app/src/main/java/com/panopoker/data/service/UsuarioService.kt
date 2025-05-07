package com.panopoker.data.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

data class SaldoResponse(val saldo: Float)

interface UsuarioService {
    @GET("usuario/saldo")
    suspend fun getSaldo(@Header("Authorization") token: String): Response<SaldoResponse>
}
