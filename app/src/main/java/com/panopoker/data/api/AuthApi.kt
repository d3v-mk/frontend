package com.panopoker.data.api

import com.panopoker.model.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

// === Requisições ===
data class RegisterRequest(val nome: String, val email: String, val password: String)

// === Respostas ===
data class RegisterResponse(val msg: String)
data class AuthResponse(
    val access_token: String,
    val token_type: String,
    val nome: String,
    val user_id: Int
)

interface AuthApi {
    @POST("/auth/login")
    fun loginUnificado(@Body body: Map<String, String>): Call<AuthResponse>

    @POST("/auth/login")
    suspend fun loginUnificadoSuspend(@Body body: Map<String, String>): AuthResponse

    @POST("/usuario/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("/usuario/{id}")
    suspend fun getUsuario(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): UserInfoResponse
}
