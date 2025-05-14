package com.panopoker.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panopoker.data.api.AuthApi
import com.panopoker.data.api.RegisterRequest
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import androidx.compose.ui.text.input.PasswordVisualTransformation
import org.json.JSONObject

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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
            onValueChange = {
                username = it
                usernameError = null
            },
            label = { Text("Usuário") },
            modifier = Modifier.fillMaxWidth(),
            isError = usernameError != null
        )
        if (usernameError != null) {
            Text(usernameError!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null
        )
        if (emailError != null) {
            Text(emailError!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null
        )
        if (passwordError != null) {
            Text(passwordError!!, color = Color.Red, fontSize = 12.sp)
        }

        RequisitosSenha(password)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val api = RetrofitInstance.retrofit.create(AuthApi::class.java)

                    try {
                        val registerResponse = api.register(RegisterRequest(username, email, password))
                        if (!registerResponse.isSuccessful) {
                            val errorBody = registerResponse.errorBody()?.string()
                            val mensagem = try {
                                val json = JSONObject(errorBody ?: "")
                                json.getString("detail")
                            } catch (e: Exception) {
                                "Erro ao registrar"
                            }

                            withContext(Dispatchers.Main) {
                                when {
                                    mensagem.contains("email", ignoreCase = true) && mensagem.contains("domínio", ignoreCase = true) -> emailError = "Domínio de email não permitido."
                                    mensagem.contains("email", ignoreCase = true) -> emailError = mensagem
                                    mensagem.contains("nome", ignoreCase = true) || mensagem.contains("usuário", ignoreCase = true) -> usernameError = mensagem
                                    mensagem.contains("senha", ignoreCase = true) -> passwordError = mensagem
                                    else -> Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show()
                                }
                            }
                            return@launch
                        }

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

@Composable
fun RequisitosSenha(senha: String) {
    val temMaiuscula = senha.any { it.isUpperCase() }
    val temMinuscula = senha.any { it.isLowerCase() }
    val temNumero = senha.any { it.isDigit() }
    val temSimbolo = senha.any { !it.isLetterOrDigit() }
    val tamanhoOk = senha.length >= 8

    Column(modifier = Modifier.padding(top = 12.dp)) {
        RequisitoItem("Mínimo 8 caracteres", tamanhoOk)
        RequisitoItem("Letra maiúscula", temMaiuscula)
        RequisitoItem("Letra minúscula", temMinuscula)
        RequisitoItem("Número", temNumero)
        RequisitoItem("Símbolo (!@#\$%)", temSimbolo)
    }
}

@Composable
fun RequisitoItem(texto: String, ok: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (ok) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (ok) Color.Green else Color.Red,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = texto,
            color = if (ok) Color.Green else Color.Red,
            fontSize = 14.sp
        )
    }
}
