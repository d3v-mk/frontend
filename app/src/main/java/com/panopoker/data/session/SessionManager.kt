package com.panopoker.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.UsuarioService
import retrofit2.HttpException
import java.io.IOException

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pano_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"

        // ✅ Método estático para usar sem instanciar
        fun getToken(context: Context): String? {
            return context.getSharedPreferences("pano_prefs", Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, null)
        }
    }

    fun saveAuthToken(token: String) {
        prefs.edit {
            putString(KEY_TOKEN, token)
        }
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveUserId(userId: Int) {
        prefs.edit {
            putInt(KEY_USER_ID, userId)
        }
    }

    fun fetchUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun fetchUserName(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun saveUserName(username: String) {
        prefs.edit {
            putString(KEY_USERNAME, username)
        }
    }

    // Função para pegar o saldo do usuário
    suspend fun fetchUserBalance(): Float {
        val token = fetchAuthToken() ?: return 0.0f
        val api = RetrofitInstance.retrofit.create(UsuarioService::class.java)
        try {
            val response = api.getSaldo("Bearer $token")
            if (response.isSuccessful) {
                return response.body()?.saldo ?: 0.0f
            }
        } catch (e: HttpException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0.0f
    }




    fun clearSession() {
        prefs.edit {
            remove("access_token")
            remove("user_id")
            remove("username")
        }
    }


    fun getUserIdFromToken(token: String): Int? {
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP)
                val json = String(payload)
                val regex = """"sub":"(\d+)"""".toRegex()  // pega o sub como string de número
                regex.find(json)?.groupValues?.get(1)?.toInt()
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
