package com.panopoker.ui.mesa.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.panopoker.model.PerfilResponse
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PerfilDoJogadorDialog(
    perfil: PerfilResponse,
    onDismiss: () -> Unit
) {
    var showFullImage by remember { mutableStateOf(false) }

    // 🔍 Log pra debug
    LaunchedEffect(perfil.avatarUrl) {
        println("🔍 Avatar URL clicado: ${perfil.avatarUrl}")
        showFullImage = false
    }

    // Usa fallback com base no nome
    val avatarUrl = perfil.avatarUrl
        ?: "https://ui-avatars.com/api/?name=${perfil.nome}&background=FFD700&color=000"

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val dialogWidth = (screenWidth * 0.85f).coerceAtMost(380.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(dialogWidth)
                .background(Color(0xFF1C1C1C), RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔄 Troca aqui de Column por Row: avatar + espaço medalhas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar colado na esquerda
                Image(
                    painter = rememberAsyncImagePainter(model = avatarUrl),
                    contentDescription = "Avatar do Jogador",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFFFFD700), CircleShape)
                        .clickable { showFullImage = true }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Medalhas mockadas (ícones ou textos por enquanto)
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text("🏅", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("🏆", fontSize = 20.sp)
                    Text("🏆", fontSize = 20.sp)
                    Text("🏆", fontSize = 20.sp)
                    Text("🏆", fontSize = 20.sp)
                    Text("🏆", fontSize = 20.sp)
                }
            }

            // Título centralizado
            Text(
                text = "📊 Estatísticas de ${perfil.nome}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )

            // Estatísticas mantidas iguais
            val stats = listOf(
                "Rodadas ganhas" to perfil.rodadas_ganhas,
                "Rodadas jogadas" to perfil.rodadas_jogadas,
                "Win Rate" to if (perfil.rodadas_jogadas > 0)
                    "${(perfil.rodadas_ganhas * 100 / perfil.rodadas_jogadas)}%"
                else "0%",
                "Vezes no top 1" to perfil.vezes_no_top1,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                stats.forEach { (label, value) ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = label, color = Color.LightGray, fontSize = 14.sp)
                        Text(
                            text = value.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fechar")
            }
        }
    }


    // Agora o dialog sempre abre, com avatar real ou fallback
    if (showFullImage) {
        Dialog(onDismissRequest = { showFullImage = false }) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = (-maxWidth * 0.06f), y = 0.dp) // 👈 desloca em Dp
                        .align(Alignment.Center)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = avatarUrl),
                            contentDescription = "Avatar Ampliado",
                            modifier = Modifier
                                .sizeIn(maxWidth = 400.dp, maxHeight = 400.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .border(4.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}///
