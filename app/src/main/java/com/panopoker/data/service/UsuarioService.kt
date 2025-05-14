package com.panopoker.data.service

import com.panopoker.model.UsuarioLogadoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

data class SaldoResponse(val saldo: Float)

interface UsuarioService {

    @GET("usuario/saldo")
    suspend fun getSaldo(@Header("Authorization") token: String): Response<SaldoResponse>

    @GET("usuario/me")
    suspend fun getUsuarioLogado(@Header("Authorization") token: String): UsuarioLogadoDto
}

