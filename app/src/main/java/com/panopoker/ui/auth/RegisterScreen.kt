package com.panopoker.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panopoker.data.api.AuthApi
import com.panopoker.data.api.AuthResponse
import com.panopoker.data.api.RegisterRequest
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Criar Conta", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val api = RetrofitInstance.retrofit.create(AuthApi::class.java)

                    try {
                        // 1. REGISTRA
                        val registerResponse = api.register(RegisterRequest(username, email, password))
                        if (!registerResponse.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Erro ao registrar: ${registerResponse.code()}", Toast.LENGTH_SHORT).show()
                            }
                            return@launch
                        }

                        // 2. LOGA automaticamente
                        val loginBody = mapOf("nome" to username, "password" to password)
                        val loginResponse = api.loginUnificadoSuspend(loginBody)

                        sessionManager.saveAuthToken(loginResponse.access_token)
                        sessionManager.saveUserId(loginResponse.user_id)
                        sessionManager.saveUserName(loginResponse.nome)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                        }

                    } catch (e: HttpException) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Erro HTTP: ${e.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Erro de conexão", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }
    }
}
