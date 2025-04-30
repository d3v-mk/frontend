package com.panopoker.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pano_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
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

    fun clearSession() {
        prefs.edit {
            clear()
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
