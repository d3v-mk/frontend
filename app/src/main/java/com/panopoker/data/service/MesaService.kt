package com.panopoker.data.service

import com.panopoker.model.JogadorDaVezDto
import com.panopoker.model.CartasComunitariasResponse
import com.panopoker.model.CartasResponse
import com.panopoker.model.Jogador
import com.panopoker.model.MesaLobbyDto
import com.panopoker.model.MesaDto
import com.panopoker.model.ShowdownDto
import retrofit2.Response
import retrofit2.http.*

interface MesaService {

    @GET("/matchmaking/bronze")
    suspend fun buscarMatchBronze(
        @Header("Authorization") token: String
    ): Response<MesaDto> // ou o modelo de Mesa que você estiver usando

    @GET("/matchmaking/prata")
    suspend fun buscarMatchPrata(
        @Header("Authorization") token: String
    ): Response<MesaDto> // ou o modelo de Mesa que você estiver usando

    @GET("/matchmaking/ouro")
    suspend fun buscarMatchOuro(
        @Header("Authorization") token: String
    ): Response<MesaDto> // ou o modelo de Mesa que você estiver usando

    @GET("/mesa/{mesaId}")
    suspend fun getMesa(
        @Path("mesaId") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<MesaDto>

    @GET("/mesa/{mesa_id}/cartas_comunitarias")
    suspend fun getCartasComunitarias(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<CartasComunitariasResponse>


    @GET("/mesa/{mesa_id}/vez")
    suspend fun getJogadorDaVez(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<JogadorDaVezDto>

    @POST("/mesa/{mesa_id}/entrar")
    suspend fun entrarNaMesa(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("/mesa/{mesa_id}/jogadores")
    suspend fun getJogadoresDaMesa(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<List<Jogador>>


    @GET("/mesa/{mesa_id}/minhas_cartas")
    suspend fun getMinhasCartas(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<CartasResponse>


    @POST("/mesa/{mesa_id}/call")
    suspend fun callJWT(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesa/{mesa_id}/check")
    suspend fun checkJWT(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesa/{mesa_id}/raise")
    suspend fun raiseJWT(
        @Path("mesa_id") mesaId: Int,
        @Query("valor") valor: Float,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesa/{mesa_id}/allin")
    suspend fun allInJWT(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesa/{mesa_id}/fold")
    suspend fun foldJWT(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/mesa/{mesa_id}/sair")
    suspend fun sairDaMesa(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("/mesa/abertas")
    suspend fun listarMesasAbertas(
        @Header("Authorization") token: String
    ): Response<List<MesaLobbyDto>>

    @GET("/mesa/{mesa_id}/showdown")
    suspend fun getShowdown(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<ShowdownDto>



}
