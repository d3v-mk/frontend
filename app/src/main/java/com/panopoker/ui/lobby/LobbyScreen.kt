package com.panopoker.ui.lobby

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.panopoker.R
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.LobbyService
import com.panopoker.data.session.SessionManager
import com.panopoker.ui.components.MenuLateralCompleto
import com.panopoker.ui.lobby.components.NewsMarquee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun BotaoHamburguer(drawerState: DrawerState, scope: CoroutineScope, modifier: Modifier = Modifier) {
    IconButton(
        onClick = { scope.launch { drawerState.open() } },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Abrir menu",
            tint = Color.White
        )
    }
}

@Composable
fun LobbyScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val token = SessionManager.getToken(context)
    var nomeUsuario by remember { mutableStateOf("Jogador") }
    var idPublico by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }
    var saldoUsuario by remember { mutableStateOf(0.0f) }

    // Listas de mensagens
    val noticias = remember { mutableStateListOf<String>() }
    val adminNoticias = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        saldoUsuario = session.fetchUserBalance()

        if (!token.isNullOrBlank()) {
            try {
                val response = RetrofitInstance.usuarioApi.getPerfil("Bearer $token")
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

        while (true) {
            try {
                // Notícias normais: pega só a mais recente (primeira da lista)
                val respNews = RetrofitInstance.lobbyService.getNoticias("Bearer $token")
                if (respNews.isSuccessful) {
                    val lista = respNews.body() ?: emptyList()
                    val events = lista.filter { it.tipo != "admin" }
                        .map { it.mensagem }
                    val latest = events.firstOrNull()?.let { listOf(it) } ?: emptyList()
                    noticias.apply {
                        clear()
                        addAll(latest)
                    }
                } else {
                    Log.e("LobbyScreen", "news falhou: ${'$'}{respNews.code()} ${'$'}{respNews.errorBody()?.string()}")
                }

                // Notícias admin: sempre pega a última admin como fixa
                val respAdmin = RetrofitInstance.lobbyService.getNoticiasAdmin("Bearer $token")
                if (respAdmin.isSuccessful) {
                    val adminList = respAdmin.body() ?: emptyList()
                    val fixed = adminList.firstOrNull()?.mensagem ?: ""
                    adminNoticias.apply {
                        clear()
                        add(fixed)
                    }
                } else {
                    Log.e("LobbyScreen", "adminNews falhou: ${'$'}{respAdmin.code()} ${'$'}{respAdmin.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("LobbyScreen", "erro ao buscar notícias", e)
            }
            delay(10_000)
        }
    }

    // Valores pra passar pro marquee
    val adminFixed = adminNoticias.firstOrNull() ?: ""
    val latestEvent = noticias.toList()

    MenuLateralCompleto(
        drawerState = drawerState,
        scope = scope,
        nomeUsuario = nomeUsuario,
        idPublico = idPublico,
        avatarUrl = avatarUrl,
        saldoUsuario = saldoUsuario,
        navController = navController
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NewsMarquee(
                    adminMsg    = adminFixed,
                    latestEvent = latestEvent.lastOrNull().orEmpty(),  // ⬅️ aqui: String, não List<String>
                    modifier    = Modifier.weight(1f)
                )


                Spacer(modifier = Modifier.width(10.dp))
                BotaoHamburguer(
                    drawerState = drawerState,
                    scope = scope,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card: Poker
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { navController.navigate("mesas_poker") },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                border = BorderStroke(2.dp, Color(0xFFFFC300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.poker_card),
                        contentDescription = "Imagem Poker",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

            // Blackjack
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
                        .height(100.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.blackjack_card),
                        contentDescription = "Imagem Blackjack",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Text(
                        text = "(EM BREVE)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Slots em breve
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
                        .height(100.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.slots_card),
                        contentDescription = "Imagem Slots",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Text(
                        text = "(EM BREVE)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Banner promocional
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(2.dp, Color(0xFFFFC300))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner_placeholder),
                    contentDescription = "Banner promocional",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}///
