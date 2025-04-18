package com.panopoker.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val msg: String, val access_token: String, val token_type: String)

data class RegisterRequest(val username: String, val email: String, val password: String)
data class RegisterResponse(val msg: String)

interface AuthApi {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}
