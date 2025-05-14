package com.panopoker.ui.financas

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.panopoker.data.session.SessionManager
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.model.SaqueDto
import com.panopoker.model.ConfirmarSaqueRequest
import com.panopoker.service.SaqueService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaqueViewModel(context: Context) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val saqueService = RetrofitInstance.retrofit.create(SaqueService::class.java)

    var saque: SaqueDto? by mutableStateOf(null)
    var erro: String? by mutableStateOf(null)

    suspend fun buscarSaquePendente() {
        withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.fetchAuthToken() ?: return@withContext
                val resposta = saqueService.getMeuSaque("Bearer $token")
                saque = resposta
            } catch (e: Exception) {
                erro = "Nenhum saque pendente encontrado."
            }
        }
    }

    suspend fun confirmarSaque(valor: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.fetchAuthToken() ?: return@withContext false
                saque?.let {
                    val request = ConfirmarSaqueRequest(valor_digitado = valor)
                    saqueService.confirmarSaque(it.id, request, "Bearer $token")
                    true
                } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }
}
