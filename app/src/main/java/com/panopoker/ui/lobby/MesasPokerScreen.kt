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
import com.panopoker.ui.lobby.components.BotaoHamburguer
import com.panopoker.ui.lobby.components.MenuLateralCompleto
import com.panopoker.R
import androidx.compose.ui.text.font.FontWeight
import com.panopoker.data.network.WebSocketClient

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

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            val valorMinimo = when (tipo) {
                                "bronze" -> 0.30f
                                "prata" -> 2.00f
                                else -> 10.00f
                            }

                            if (saldoUsuario < valorMinimo) {
                                erroMatch = "Saldo insuficiente para entrar em mesas $tipo"
                                return@clickable
                            }

                            erroMatch = null
                            var matchWs: WebSocketClient? = null
                            matchWs = WebSocketClient(
                                //context = context,
                                mesaId = 0,
                                token = token ?: "",
                                tipoMatch = tipo, // bronze, prata, ouro
                                onMatchEncontrado = { mesaId ->
                                    matchWs?.disconnect()
                                    val intent = Intent(context, com.panopoker.ui.mesa.MesaActivity::class.java)
                                    intent.putExtra("mesa_id", mesaId)
                                    context.startActivity(intent)
                                },
                                onMesaAtualizada = {},
                                onRemovidoSemSaldo = {
                                    erroMatch = "Você foi removido por saldo insuficiente"
                                }
                            )
                            matchWs.connect()
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
