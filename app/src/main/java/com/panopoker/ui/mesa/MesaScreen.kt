package com.panopoker.ui.mesa

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import com.panopoker.data.session.SessionManager
import com.panopoker.model.CartasComunitarias
import com.panopoker.model.Jogador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MesaScreen(mesaId: Int, navController: NavController) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val accessToken = session.fetchAuthToken() ?: ""
    val coroutineScope = rememberCoroutineScope()

    var jogadores by remember { mutableStateOf<List<Jogador>>(emptyList()) }
    var cartas by remember { mutableStateOf<CartasComunitarias?>(null) }
    var minhasCartas by remember { mutableStateOf<List<String>>(emptyList()) }
    var jogadorDaVezId by remember { mutableStateOf<Int?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    fun refreshMesa() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val mesaService = RetrofitInstance.retrofit.create(MesaService::class.java)
                val jogadoresResp = mesaService.getJogadoresDaMesa(mesaId, accessToken)
                if (jogadoresResp.isSuccessful) jogadores = jogadoresResp.body() ?: emptyList()

                val cartasResp = mesaService.getCartasComunitarias(mesaId, accessToken)
                if (cartasResp.isSuccessful) cartas = cartasResp.body()

                val minhasResp = mesaService.getMinhasCartas(mesaId, accessToken)
                if (minhasResp.isSuccessful) minhasCartas = minhasResp.body() ?: emptyList()

                val vezResp = mesaService.getJogadorDaVez(mesaId, accessToken)
                if (vezResp.isSuccessful) jogadorDaVezId = vezResp.body()?.get("jogador_da_vez")

            } catch (e: Exception) {
                error = "Erro: ${e.message}"
            }
        }
    }

    LaunchedEffect(true) {
        while (true) {
            refreshMesa()
            delay(2000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1B1B))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mesa #$mesaId", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)

        // 🎯 Indicação do jogador da vez
        val nomeJogadorDaVez = jogadores.find { it.id == jogadorDaVezId }?.username
        if (nomeJogadorDaVez != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("🎯 Vez de: $nomeJogadorDaVez", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("🤖 Dealer (NPC)", modifier = Modifier.padding(12.dp), color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            cartas?.let {
                val cartasParaMostrar = buildList {
                    addAll(it.flop)
                    if ((jogadores.count { j -> !j.foldado } <= 1) || jogadores.all { j -> j.aposta_atual == jogadores.first().aposta_atual }) {
                        if (!it.turn.isNullOrEmpty()) add(it.turn)
                        if (!it.river.isNullOrEmpty()) add(it.river)
                    }
                }

                cartasParaMostrar.forEach { carta ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF353535)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(carta, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 20.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (minhasCartas.isNotEmpty()) {
            Text("Suas cartas:", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                minhasCartas.forEach { carta ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4E4E4E)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(carta, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 20.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (error != null) {
            Text(error ?: "", color = Color.Red)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (jogadores.isEmpty()) {
                Text("Aguardando jogadores...", color = Color.White)
            } else {
                jogadores.forEach { jogador ->
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("🧍 ${jogador.username}", color = Color.White)
                            Text("💰 Stack: R$ %.2f".format(jogador.stack), color = Color.LightGray)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val service = RetrofitInstance.retrofit.create(MesaService::class.java)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                coroutineScope.launch {
                    service.foldJWT(mesaId, accessToken)
                    delay(500)
                    refreshMesa()
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Fold", color = Color.White)
            }

            Button(onClick = {
                coroutineScope.launch {
                    service.callJWT(mesaId, accessToken)
                    delay(500)
                    refreshMesa()
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                Text("Call", color = Color.White)
            }

            Button(onClick = {
                coroutineScope.launch {
                    service.checkJWT(mesaId, accessToken)
                    delay(500)
                    refreshMesa()
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("Check", color = Color.White)
            }

            Button(onClick = {
                coroutineScope.launch {
                    service.raiseJWT(mesaId, 1.0f, accessToken)
                    delay(500)
                    refreshMesa()
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066CC))) {
                Text("Raise", color = Color.White)
            }

            Button(onClick = {
                coroutineScope.launch {
                    service.allInJWT(mesaId, accessToken)
                    delay(500)
                    refreshMesa()
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) {
                Text("All-in", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val response = service.sairDaMesa(mesaId, accessToken)
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Saiu da mesa com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Erro ao sair da mesa!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Sair da Mesa", color = Color.White)
        }
    }
}
