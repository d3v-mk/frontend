package com.panopoker.model

import com.google.gson.annotations.SerializedName

data class PerfilResponse(
    @SerializedName("id_publico") val id_publico: String,
    @SerializedName("nome") val nome: String,
    @SerializedName("avatar_url") val avatarUrl: String?, // AQUI O SEGREDO 🔥
    @SerializedName("rodadas_ganhas") val rodadas_ganhas: Int,
    @SerializedName("rodadas_jogadas") val rodadas_jogadas: Int,
    @SerializedName("fichas_ganhas") val fichas_ganhas: Double,
    @SerializedName("fichas_perdidas") val fichas_perdidas: Double,
    @SerializedName("sequencias") val sequencias: Int,
    @SerializedName("flushes") val flushes: Int,
    @SerializedName("full_houses") val full_houses: Int,
    @SerializedName("quadras") val quadras: Int,
    @SerializedName("straight_flushes") val straight_flushes: Int,
    @SerializedName("royal_flushes") val royal_flushes: Int,
    @SerializedName("torneios_vencidos") val torneios_vencidos: Int,
    @SerializedName("maior_pote") val maior_pote: Double,
    @SerializedName("vitorias") val vitorias: Int,
    @SerializedName("mao_favorita") val mao_favorita: String?,
    @SerializedName("ranking_mensal") val ranking_mensal: Int?,
    @SerializedName("vezes_no_top1") val vezes_no_top1: Int,
    @SerializedName("data_primeira_vitoria") val data_primeira_vitoria: String?,
    @SerializedName("data_ultima_vitoria") val data_ultima_vitoria: String?,
    @SerializedName("is_promoter") val is_promoter: Boolean,
    @SerializedName("ultimo_update") val ultimo_update: String?,
    @SerializedName("beta_tester") val beta_tester: Int
)

