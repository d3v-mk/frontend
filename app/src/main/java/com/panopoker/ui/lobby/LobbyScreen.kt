package com.panopoker.ui.lobby

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.panopoker.data.api.MesaApi
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import com.panopoker.data.session.SessionManager
import com.panopoker.model.Mesa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LobbyScreen(navController: NavController) {
    var mesas by remember { mutableStateOf<List<Mesa>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val accessToken = session.fetchAuthToken() ?: ""

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitInstance.retrofit.create(MesaApi::class.java)
                val response = api.getMesasDisponiveis()
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (error != null) {
            Text("Erro ao carregar mesas: $error", color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(mesas) { mesa ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mesa #${mesa.id}", style = MaterialTheme.typography.titleLarge)
                        Text("Tipo: ${mesa.tipo_jogo}")
                        Text("Aposta mínima: R$${mesa.valor_minimo_aposta}")
                        Text("Jogadores: ${mesa.jogadores_atuais}/${mesa.limite_jogadores}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        val mesaService = RetrofitInstance.retrofit.create(MesaService::class.java)
                                        val response = mesaService.entrarNaMesa(mesa.id, accessToken)

                                        if (response.isSuccessful) {
                                            coroutineScope.launch(Dispatchers.Main) {
                                                Toast.makeText(context, "Entrou na mesa #${mesa.id} com sucesso!", Toast.LENGTH_SHORT).show()
                                                navController.navigate("mesa/${mesa.id}")
                                            }
                                        } else {
                                            val errorBody = response.errorBody()?.string()
                                            val mensagem = if (errorBody?.contains("Saldo insuficiente") == true) {
                                                "Saldo insuficiente para entrar na mesa"
                                            } else {
                                                "Erro: ${response.code()}"
                                            }

                                            coroutineScope.launch(Dispatchers.Main) {
                                                Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        coroutineScope.launch(Dispatchers.Main) {
                                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Entrar na mesa")
                        }
                    }
                }
            }
        }
    }
}
