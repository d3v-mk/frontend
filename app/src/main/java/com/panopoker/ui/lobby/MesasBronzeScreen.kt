package com.panopoker.ui.lobby

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import com.panopoker.data.session.SessionManager
import com.panopoker.model.MesaLobbyDto
import com.panopoker.ui.mesa.MesaActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MesasBronzeScreen(navController: NavController) {
    var mesas by remember { mutableStateOf<List<MesaLobbyDto>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitInstance.retrofit.create(MesaService::class.java)
                val token = session.fetchAuthToken() ?: ""
                val response = api.listarMesasAbertas("Bearer $token")
                if (response.isSuccessful) {
                    mesas = response.body()?.filter { it.buy_in == 0.30 } ?: emptyList()
                } else {
                    error = "Erro: ${response.code()}"
                }
            } catch (e: Exception) {
                error = "Erro: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Mesas Bronze",
            fontSize = 24.sp,
            color = Color(0xFFFFD700),
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
                                        val mesaService = RetrofitInstance.retrofit.create(MesaService::class.java)
                                        val response = mesaService.entrarNaMesa(mesa.id, "Bearer $token")

                                        if (response.isSuccessful) {
                                            coroutineScope.launch(Dispatchers.Main) {
                                                val intent = Intent(context, MesaActivity::class.java)
                                                intent.putExtra("mesaId", mesa.id)
                                                context.startActivity(intent)
                                            }
                                        }
                                    } catch (_: Exception) {}
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