package com.panopoker.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panopoker.data.api.AuthApi
import com.panopoker.data.api.LoginRequest
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuário") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val api = RetrofitInstance.retrofit.create(AuthApi::class.java)
                    try {
                        val response = api.login(LoginRequest(username, password))
                        if (response.isSuccessful && response.body() != null) {
                            val body = response.body()!!
                            val token = body.access_token

                            sessionManager.saveAuthToken(token)

                            val payload = JSONObject(
                                String(android.util.Base64.decode(token.split(".")[1], android.util.Base64.DEFAULT))
                            )
                            val userId = payload.getInt("sub")

                            sessionManager.saveUserId(userId)

                            // Agora vamos salvar o nome do usuário
                            sessionManager.saveUserName(username)

                            launch(Dispatchers.Main) {
                                onLoginSuccess()
                            }
                        } else {
                            // erro ao logar (sem Toast)
                        }
                    } catch (e: HttpException) {
                        // erro http (sem Toast)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar conta")
        }
    }
}
