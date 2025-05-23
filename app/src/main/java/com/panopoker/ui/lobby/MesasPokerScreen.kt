package com.panopoker.ui.lobby

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panopoker.data.session.SessionManager
import com.panopoker.ui.components.BotaoHamburguer
import com.panopoker.ui.components.MenuLateralCompleto
import com.panopoker.R
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

@Composable
fun MesasPokerScreen(
    navController: NavController,
    ) {
    val context = LocalContext.current
    val token = SessionManager.getToken(context)
    val session = remember { SessionManager(context) }
    var nomeUsuario by remember { mutableStateOf("Jogador") }
    var idPublico by remember { mutableStateOf("") }
    var erroMatch by remember { mutableStateOf<String?>(null) }
    var avatarUrl by remember { mutableStateOf<String?>(null) }


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var saldoUsuario by remember { mutableStateOf(0.0f) }

    LaunchedEffect(Unit) {
        saldoUsuario = session.fetchUserBalance()

        if (!token.isNullOrBlank()) {
            try {
                val response = com.panopoker.data.network.RetrofitInstance.usuarioApi.getPerfil("Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let { perfil ->
                        nomeUsuario = perfil.nome
                        idPublico = perfil.id_publico
                        avatarUrl = perfil.avatarUrl
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    MenuLateralCompleto(
        drawerState = drawerState,
        scope = scope,
        nomeUsuario = nomeUsuario,
        idPublico = idPublico,
        saldoUsuario = saldoUsuario,
        avatarUrl = avatarUrl,
        navController = navController
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mesas de Poker",
                    fontSize = 24.sp,
                    color = Color(0xFFFFC300)
                )

                BotaoHamburguer(drawerState, scope)
            }

            listOf("bronze", "prata", "ouro").forEach { tipo ->
                val titulo = when (tipo) {
                    "bronze" -> "Mesas Bronze"
                    "prata" -> "Mesas Prata"
                    else -> "Mesas Ouro"
                }

                val imagemResId = when (tipo) {
                    "bronze" -> R.drawable.mesasbronze_card
                    "prata" -> R.drawable.mesasprata_card
                    else -> R.drawable.mesasouro_card
                }

                val matchFunction = when (tipo) {
                    "bronze" -> com.panopoker.data.network.RetrofitInstance.mesaService::buscarMatchBronze
                    "prata" -> com.panopoker.data.network.RetrofitInstance.mesaService::buscarMatchPrata
                    else -> com.panopoker.data.network.RetrofitInstance.mesaService::buscarMatchOuro
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            scope.launch {
                                try {
                                    val response = matchFunction("Bearer $token")
                                    if (response.isSuccessful) {
                                        val mesaId = response.body()?.id
                                        if (mesaId != null) {
                                            val entrar = com.panopoker.data.network.RetrofitInstance.mesaService.entrarNaMesa(mesaId, "Bearer $token")
                                            if (entrar.isSuccessful) {
                                                val intent = Intent(context, com.panopoker.ui.mesa.MesaActivity::class.java)
                                                intent.putExtra("mesa_id", mesaId)
                                                context.startActivity(intent)
                                            } else {
                                                erroMatch = "Erro ao entrar na mesa."
                                            }
                                        } else {
                                            erroMatch = "Nenhuma mesa disponível."
                                        }
                                    } else {
                                        erroMatch = "Nenhuma mesa disponível."
                                    }
                                } catch (e: Exception) {
                                    erroMatch = "Erro ao buscar mesa."
                                }
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    border = BorderStroke(2.dp, Color(0xFFFFC300))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = imagemResId),
                            contentDescription = titulo,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = titulo,
                            fontSize = 20.sp,
                            color = Color(0xFFFFC300),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (erroMatch != null) {
                Text(
                    text = erroMatch!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                border = BorderStroke(2.dp, Color.Gray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Torneios 🏆 (em breve)", fontSize = 20.sp, color = Color.LightGray)
                }
            }
        }
    }
}
