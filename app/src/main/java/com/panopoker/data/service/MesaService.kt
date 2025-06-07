package com.panopoker.data.service

import com.panopoker.model.CartasComunitariasResponse
import com.panopoker.model.CartasResponse
import com.panopoker.model.Jogador
import com.panopoker.model.MesaDto
import com.panopoker.model.ShowdownDto
import retrofit2.Response
import retrofit2.http.*

interface MesaService {

    @POST("/mesa/{mesaId}/revelar_cartas")
    suspend fun revelarCartas(
        @Path("mesaId") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

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


    @GET("/mesa/{mesa_id}/showdown")
    suspend fun getShowdown(
        @Path("mesa_id") mesaId: Int,
        @Header("Authorization") token: String
    ): Response<ShowdownDto>

}
