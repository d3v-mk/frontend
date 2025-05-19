package com.panopoker.ui.financas

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panopoker.model.PromotorDto
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import com.panopoker.ui.financas.SaqueViewModel
import kotlinx.coroutines.launch

@Composable
fun PromotoresScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var promotores by remember { mutableStateOf<List<PromotorDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var jogadorId by remember { mutableStateOf<Int?>(null) }
    val token = SessionManager.getToken(context)

    val viewModel = remember { SaqueViewModel(context) }
    var valorDigitado by remember { mutableStateOf("") }
    val saque = viewModel.saque
    val erro = viewModel.erro

    LaunchedEffect(Unit) {
        scope.launch {
            if (!token.isNullOrBlank()) {
                try {
                    val user = RetrofitInstance.usuarioService.getUsuarioLogado("Bearer $token")
                    jogadorId = user.id
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            try {
                promotores = RetrofitInstance.promotorService.getPromotores()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                loading = false
            }
        }
        viewModel.buscarSaquePendente()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp)
    ) {
        Text(
            text = "Promotores",
            fontSize = 24.sp,
            color = Color(0xFFFFD700),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (loading) {
            CircularProgressIndicator(color = Color(0xFFFFD700))
        } else {
            promotores.forEach { promotor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = promotor.nome,
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                val url = "http://192.168.0.9:8000/loja/promotor/${promotor.slug}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Loja", color = Color.Black)
                        }

                        promotor.whatsapp?.takeIf { it.isNotBlank() }?.let { numero ->
                            val numeroLimpo = numero.replace(Regex("[^\\d]"), "")
                            val mensagem = Uri.encode("Olá! Sou jogador do Pano *ID ${jogadorId ?: "?"}*\nSolicito atendimento por favor.")
                            val url = "https://wa.me/$numeroLimpo?text=$mensagem"

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("WhatsApp", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (saque != null) {
            Text("Receber Fichas: R$ ${saque.valor}", color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = valorDigitado,
                onValueChange = { valorDigitado = it },
                label = { Text("Confirme o valor") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        val sucesso = viewModel.confirmarSaque(valorDigitado)
                        Toast.makeText(
                            context,
                            if (sucesso) "Confirmado!" else "Erro ao confirmar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar")
            }
        } else {
            Text(erro ?: "Carregando...", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Voltar", color = Color.White)
        }
    }
}
