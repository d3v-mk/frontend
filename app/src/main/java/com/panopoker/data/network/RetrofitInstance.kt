package com.panopoker.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.panopoker.BuildConfig
import com.panopoker.data.service.LobbyService
import com.panopoker.data.service.MesaService
import com.panopoker.data.service.PromotorService
import com.panopoker.data.service.UsuarioService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Interceptor custom que mascara token Authorization nos logs
    private val safeLoggingInterceptor = HttpLoggingInterceptor { message ->
        if (message.startsWith("Authorization:")) {
            Log.i("OkHttp", "Authorization: Bearer *** (token ocultado no log)")
        } else {
            Log.i("OkHttp", message)
        }
    }.apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(safeLoggingInterceptor)
        .build()

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

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
