package com.panopoker.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.panopoker.data.service.LobbyService
import com.panopoker.data.service.MesaService
import com.panopoker.data.service.PromotorService
import com.panopoker.data.service.UsuarioService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.0.9:8000" // IPZADA

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()


    private val gson: Gson = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // ✅ ADICIONA ISSO AQUI EMBAIXO
    val promotorService: PromotorService by lazy {
        retrofit.create(PromotorService::class.java)
    }

    val usuarioService: UsuarioService by lazy {
        retrofit.create(UsuarioService::class.java)
    }

    val mesaService: MesaService by lazy {
        retrofit.create(MesaService::class.java)
    }

    val usuarioApi: UsuarioService by lazy {
        retrofit.create(UsuarioService::class.java)
    }

    val lobbyService: LobbyService by lazy {
        retrofit.create(LobbyService::class.java)
    }
}
