package com.panopoker.ui.financas

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    var idPublico by remember { mutableStateOf<String?>(null) }
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
                    idPublico = user.id_publico
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

        // --- RECEBER FICHAS PRIMEIRO ---
        if (saque != null) {
            Text("Receber Fichas: ${saque.valor}", color = Color.White)
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

        Spacer(modifier = Modifier.height(28.dp)) // Espaço antes da lista de promotores

        // --- LISTA DE PROMOTORES AGORA VEM DEPOIS ---
// ... outros componentes acima (receber fichas, etc)

        if (loading) {
            CircularProgressIndicator(color = Color(0xFFFFD700))
        } else {
            promotores.forEach { promotor ->
                val avatarCacheBuster = if (!promotor.avatarUrl.isNullOrBlank()) {
                    promotor.avatarUrl + "?t=" + (promotor.id ?: 0)
                } else {
                    ""
                }
                val avatarPromotor = if (!promotor.avatarUrl.isNullOrBlank()) {
                    coil.compose.rememberAsyncImagePainter(avatarCacheBuster)
                } else {
                    painterResource(id = com.panopoker.R.drawable.avatar_default)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Avatar + Status Online
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Image(
                                painter = avatarPromotor,
                                contentDescription = "Foto promotor",
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF232323), shape = CircleShape)
                            )
                            // Status Online/Offline
                            Box(
                                Modifier
                                    .size(14.dp)
                                    .offset(x = 4.dp, y = 4.dp)
                                    .background(
                                        if (promotor.whatsapp?.endsWith("2") == true) Color(
                                            0xFF43EA85
                                        ) else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .border(1.dp, Color.Black, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))

                        // Informações do promotor
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = promotor.nome,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(4) { Text("⭐", fontSize = 13.sp) }
                            }
                            Text(
                                text = "Atendimento VIP!",
                                color = Color(0xFFFFE082),
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1
                            )
                            Text(
                                text = "Curitiba/PR · No Pano desde 2024",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }

                        // Botões Loja + WhatsApp
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            promotor.whatsapp?.takeIf { it.isNotBlank() }?.let { numero ->
                                val numeroLimpo = numero.replace(Regex("[^\\d]"), "")
                                val mensagem =
                                    Uri.encode("Olá! Sou jogador do Pano *ID ${idPublico ?: "?"}*\nSolicito atendimento por favor.")
                                val url = "https://wa.me/$numeroLimpo?text=$mensagem"

                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF25D366
                                        )
                                    ),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .width(110.dp)
                                        .padding(bottom = 4.dp)
                                ) {
                                    Text(
                                        "WhatsApp",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    val url =
                                        "http://192.168.0.9:8000/loja/promotor/${promotor.slug}"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFFFD700
                                    )
                                ),
                                shape = RoundedCornerShape(50),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .width(110.dp)
                            ) {
                                Text(
                                    "Loja",
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Voltar", color = Color.White)
        }
    }
}///
