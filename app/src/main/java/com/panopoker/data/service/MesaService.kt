package com.panopoker.data.service

import com.panopoker.model.CartasComunitarias
import com.panopoker.model.Jogador
import retrofit2.Response
import retrofit2.http.*

interface MesaService {

    @GET("/mesas/{mesa_id}/vez")
    suspend fun getJogadorDaVez(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Map<String, Int>>

    @POST("/mesas/{mesa_id}/entrar")
    suspend fun entrarNaMesa(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("/mesas/{mesa_id}/jogadores")
    suspend fun getJogadoresDaMesa(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<List<Jogador>>

    @GET("/mesas/{mesa_id}/cartas_comunitarias")
    suspend fun getCartasComunitarias(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<CartasComunitarias>

    @GET("/mesas/{mesa_id}/minhas_cartas")
    suspend fun getMinhasCartas(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<List<String>>

    @POST("/mesas/{mesa_id}/call")
    suspend fun callJWT(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesas/{mesa_id}/check")
    suspend fun checkJWT(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesas/{mesa_id}/raise")
    suspend fun raiseJWT(
        @Path("mesa_id") mesaId: Int,
        @Query("valor") valor: Float,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesas/{mesa_id}/allin")
    suspend fun allInJWT(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesas/{mesa_id}/fold")
    suspend fun foldJWT(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesas/{mesa_id}/sair")
    suspend fun sairDaMesa(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>
}
