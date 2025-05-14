package com.panopoker.data.service

import com.panopoker.model.PromotorDto
import retrofit2.http.GET

interface PromotorService {
    @GET("/api/promotores-com-loja")
    suspend fun getPromotores(): List<PromotorDto>
}
