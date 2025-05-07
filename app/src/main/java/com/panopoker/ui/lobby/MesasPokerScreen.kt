package com.panopoker.ui.lobby

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.compose.rememberNavController
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import com.panopoker.data.session.SessionManager
import com.panopoker.model.Mesa
import com.panopoker.ui.components.BotaoHamburguer
import com.panopoker.ui.components.MenuLateralCompleto
import com.panopoker.ui.mesa.MesaActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.panopoker.R
import androidx.compose.ui.text.font.FontWeight



@Composable
fun MesasPokerScreen(navController: NavController) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val nomeUsuario = session.fetchUserName() ?: "Jogador"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MenuLateralCompleto(
        drawerState = drawerState,
        scope = scope,
        nomeUsuario = nomeUsuario,
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

            // Cards de seleção de categoria com imagem
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
                        .clickable { navController.navigate("mesas_$tipo") },
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
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Texto sobre a imagem
                        Text(
                            text = titulo,
                            fontSize = 20.sp,
                            color = Color(0xFFFFC300),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }



            // Card: Torneios (em breve)
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



