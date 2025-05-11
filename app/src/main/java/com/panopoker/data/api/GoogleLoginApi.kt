package com.panopoker.data.api

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

fun loginWithGoogleToken(idToken: String, onSuccess: (String) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.9:8000/") // EMULADOR Android → backend local
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(AuthApi::class.java)
    val body = mapOf("id_token" to idToken)

    service.loginWithGoogle(body).enqueue(object : Callback<AuthResponse> {
        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
            if (response.isSuccessful) {
                val token = response.body()?.access_token
                token?.let { onSuccess(it) }
            } else {
                Log.e("GoogleLogin", "Erro no backend: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
            Log.e("GoogleLogin", "Falha na chamada: ${t.message}")
        }
    })
}

