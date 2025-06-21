package com.panopoker.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.panopoker.R





import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.withStyle


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
) {
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fundo
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Conteúdo responsivo
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleLoginScreen(
                onLoginSuccess = { onLoginSuccess() },
                modifier = Modifier
                    .width(screenWidth * 0.3f) // Responsivo na largura
                    .height(screenHeight * 0.15f) // Responsivo na altura
                    .offset(x = screenWidth * 0f, y = screenHeight * -0.4f)
                    .align(Alignment.Center)
            )

            val annotatedText = buildAnnotatedString {
                append("Ao continuar, você concorda com os ")

                pushStringAnnotation(tag = "TERMS", annotation = "https://seusite.com/termos")
                withStyle(style = SpanStyle(color = Color.Cyan, textDecoration = TextDecoration.Underline)) {
                    append("Termos de Uso")
                }
                pop()

                append(" e com a ")

                pushStringAnnotation(tag = "PRIVACY", annotation = "https://seusite.com/privacidade")
                withStyle(style = SpanStyle(color = Color.Cyan, textDecoration = TextDecoration.Underline)) {
                    append("Política de Privacidade")
                }
                pop()

                append(".")
            }

            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 12.sp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = screenWidth * 0f, y = screenHeight * -0.25f)
                    .padding(horizontal = 24.dp),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            if (annotation.tag == "TERMS" || annotation.tag == "PRIVACY") {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                context.startActivity(intent)
                            }
                        }
                }
            )
        }
    }
}



// TELA DE LOGIN PRA DEBUGGGGGGGG
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
