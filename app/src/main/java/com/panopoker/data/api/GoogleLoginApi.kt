package com.panopoker.data.api

import android.content.Context
import android.util.Log
import com.panopoker.data.session.SessionManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun loginWithGoogleToken(
    idToken: String,
    context: Context,
    onSuccess: () -> Unit
) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.panopoker.com/") // IPZADA
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(AuthApi::class.java)
    val body = mapOf("id_token" to idToken)

    service.loginUnificado(body).enqueue(object : Callback<AuthResponse> {
        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val sessionManager = SessionManager(context)
                    sessionManager.saveAuthToken(responseBody.access_token)
                    sessionManager.saveUserName(responseBody.nome)
                    sessionManager.saveUserId(responseBody.user_id)

                    Log.d("LOGIN_DEBUG", "Login com Google OK: ${responseBody.nome}")
                    onSuccess()
                }
            } else {
                Log.e("GoogleLogin", "Erro no backend: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
            Log.e("GoogleLogin", "Falha na chamada: ${t.message}")
        }
    })
}
