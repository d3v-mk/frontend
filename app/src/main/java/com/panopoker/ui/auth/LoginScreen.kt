package com.panopoker.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panopoker.data.api.AuthApi
import com.panopoker.data.api.AuthResponse
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.panopoker.R





@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Fundo
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Conteúdo responsivo
        BoxWithConstraints {
            val screenHeight = maxHeight
            val bottomPadding = screenHeight * 0.2f // ou ajusta pra 0.1f, 0.15f, etc

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = bottomPadding),
                verticalArrangement = Arrangement.Top
            ) {
                // Campos do topo
                Spacer(modifier = Modifier.height(1.dp)) // espaço reservado pra logo ou algo assim

                // Bloco central (formulários, etc.)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Ex: TextField de email/senha
                }

                // Agora sim o botão no final
                GoogleLoginScreen {
                    onLoginSuccess()
                }
            }
        }
    }
}



//@Composable
//fun LoginScreen(
//    onLoginSuccess: () -> Unit,
//    onRegisterClick: () -> Unit
//) {
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val sessionManager = remember { SessionManager(context) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("Login", fontSize = 24.sp)
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text("Usuário") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Senha") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth(),
//            visualTransformation = PasswordVisualTransformation()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                CoroutineScope(Dispatchers.IO).launch {
//                    val api = RetrofitInstance.retrofit.create(AuthApi::class.java)
//                    val body = mapOf("nome" to username, "password" to password)
//                    val call: Call<AuthResponse> = api.loginUnificado(body)
//
//                    call.enqueue(object : Callback<AuthResponse> {
//                        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
//                            if (response.isSuccessful) {
//                                val resp = response.body()!!
//                                sessionManager.saveAuthToken(resp.access_token)
//                                sessionManager.saveUserName(resp.nome)
//                                sessionManager.saveUserId(resp.user_id)
//
//                                CoroutineScope(Dispatchers.Main).launch {
//                                    onLoginSuccess()
//                                }
//                            } else if (response.code() == 401) {
//                                // 🔥 Sessão inválida, limpa tudo
//                                sessionManager.clearSession()
//                                CoroutineScope(Dispatchers.Main).launch {
//                                    Toast.makeText(context, "Sessão inválida. Faça login novamente.", Toast.LENGTH_SHORT).show()
//                                }
//                            } else {
//                                CoroutineScope(Dispatchers.Main).launch {
//                                    Toast.makeText(context, "Erro ao fazer login: ${response.code()}", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        }
//
//                        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                            CoroutineScope(Dispatchers.Main).launch {
//                                Toast.makeText(context, "Erro de conexão", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    })
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Entrar")
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        TextButton(
//            onClick = onRegisterClick,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Criar conta")
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text("ou", fontSize = 14.sp)
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        GoogleLoginScreen {
//            onLoginSuccess()
//        }
//    }
//}
