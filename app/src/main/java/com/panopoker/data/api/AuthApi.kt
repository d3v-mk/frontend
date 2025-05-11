package com.panopoker.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Call

// === Requisições ===
data class LoginRequest(val nome: String, val password: String)
data class RegisterRequest(val username: String, val email: String, val password: String)
data class GoogleLoginRequest(val id_token: String)

// === Respostas ===
data class LoginResponse(val msg: String? = null, val access_token: String, val token_type: String)
data class RegisterResponse(val msg: String)
data class AuthResponse(val access_token: String, val token_type: String) // usado pro login com Google

interface AuthApi {
    @POST("/usuario/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/usuario/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/callback-mobile")
    fun loginWithGoogle(@Body body: Map<String, String>): Call<AuthResponse>
}
