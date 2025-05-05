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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panopoker.R
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import com.panopoker.data.session.SessionManager
import com.panopoker.model.Mesa
import com.panopoker.ui.mesa.MesaActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material3.Divider


@Composable
fun LobbyScreen(navController: NavController) {
    var mesas by remember { mutableStateOf<List<Mesa>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val accessToken = session.fetchAuthToken() ?: ""

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitInstance.retrofit.create(MesaService::class.java)
                val token = session.fetchAuthToken() ?: ""
                val response = api.listarMesasAbertas("Bearer $token")
                if (response.isSuccessful) {
                    mesas = response.body() ?: emptyList()
                } else {
                    error = "Erro: ${response.code()}"
                }
            } catch (e: Exception) {
                error = "Erro: ${e.message}"
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF1E1E1E),
                drawerContentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(100),
                        color = Color.Gray,
                        modifier = Modifier.size(100.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_default),
                            contentDescription = "Avatar do jogador",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Murilo MK",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                DrawerItem("Perfil")
                DrawerItem("Saldo: R$123,45")
                DrawerItem("Configurações")
                DrawerItem("Sair")
            }
        }
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
                    text = "Escolha sua mesa",
                    fontSize = 24.sp,
                    color = Color(0xFFFFD700)
                )

                IconButton(onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Abrir menu",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text("Erro ao carregar mesas: $error", color = MaterialTheme.colorScheme.error)
            }

            LazyColumn {
                items(mesas) { mesa ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        border = BorderStroke(2.dp, Color(0xFFFFD700))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Mesa #${mesa.id}", fontSize = 20.sp, color = Color(0xFFFFD700))
                            Text("Tipo: ${mesa.tipo_jogo}", color = Color.White)
                            Text("Buy-in: R$${mesa.buy_in}", color = Color.White)
                            Text("Jogadores: ${mesa.jogadores_atuais}/6", color = Color.White)

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        try {
                                            val token = session.fetchAuthToken() ?: ""
                                            Log.d("TOKEN_DEBUG", "Bearer $token")
                                            val mesaService = RetrofitInstance.retrofit.create(MesaService::class.java)
                                            val response = mesaService.entrarNaMesa(mesa.id, "Bearer $token")

                                            if (response.isSuccessful) {
                                                coroutineScope.launch(Dispatchers.Main) {
                                                    val intent = Intent(context, MesaActivity::class.java)
                                                    intent.putExtra("mesaId", mesa.id)
                                                    context.startActivity(intent)
                                                }
                                            }
                                        } catch (e: Exception) {
                                            // erro ao entrar na mesa
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Entrar na mesa")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* ação futura */ }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}
