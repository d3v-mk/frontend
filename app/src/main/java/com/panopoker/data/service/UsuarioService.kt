package com.panopoker.data.service


import com.panopoker.model.AvatarUploadResponse
import com.panopoker.model.PerfilResponse
import com.panopoker.model.UsuarioLogadoDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


data class SaldoResponse(val saldo: Float)

interface UsuarioService {

    @Multipart
    @POST("/usuario/upload_avatar")
    suspend fun uploadAvatar(
        @Header("Authorization") token: String,
        @Part avatar: MultipartBody.Part
    ): Response<AvatarUploadResponse> // ⬅️ estava Response<Unit>, tem que retornar esse DTO

    @GET("/usuario/perfil/{user_id}")
    suspend fun getPerfilDeOutroUsuario(
        @Path("user_id") userId: Int,
        @Header("Authorization") token: String
    ): Response<PerfilResponse>

    @GET("usuario/saldo")
    suspend fun getSaldo(@Header("Authorization") token: String): Response<SaldoResponse>

    @GET("usuario/me")
    suspend fun getUsuarioLogado(@Header("Authorization") token: String): UsuarioLogadoDto

    @GET("/usuario/perfil")
    suspend fun getPerfil(
        @Header("Authorization") token: String
    ): Response<PerfilResponse>

}

