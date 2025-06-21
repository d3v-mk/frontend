package com.panopoker.ui.lobby

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.panopoker.R
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import com.panopoker.ui.lobby.components.CarrosselLoby
import com.panopoker.ui.lobby.components.NewsMarquee
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun LobbyScreen(navController: NavController) {
    val context = LocalContext.current
    var saldoUsuario by remember { mutableStateOf(0.0f) }
    var nomeUsuario by remember { mutableStateOf("Jogador") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }
    val session = remember { SessionManager(context) }
    var idPublico by remember { mutableStateOf("") }

    val token = SessionManager.getToken(context)

    val noticias = remember { mutableStateListOf<String>() }
    val adminNoticias = remember { mutableStateListOf<String>() }

    val adminFixed = adminNoticias.firstOrNull() ?: ""
    val latestEvent = noticias.toList()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp



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
                        Log.d("AvatarURL", "Avatar URL recebido: $avatarUrl")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        while (true) {
            try {
                val respNews = RetrofitInstance.lobbyService.getNoticias("Bearer $token")
                if (respNews.isSuccessful) {
                    val lista = respNews.body() ?: emptyList()
                    val events = lista.filter { it.tipo != "admin" }.map { it.mensagem }
                    val latest = events.firstOrNull()?.let { listOf(it) } ?: emptyList()
                    noticias.apply {
                        clear()
                        addAll(latest)
                    }
                } else {
                    Log.e("LobbyScreen", "news falhou: ${respNews.code()} ${respNews.errorBody()?.string()}")
                }

                val respAdmin = RetrofitInstance.lobbyService.getNoticiasAdmin("Bearer $token")
                if (respAdmin.isSuccessful) {
                    val adminList = respAdmin.body() ?: emptyList()
                    val fixed = adminList.firstOrNull()?.mensagem ?: ""
                    adminNoticias.apply {
                        clear()
                        add(fixed)
                    }
                } else {
                    Log.e("LobbyScreen", "adminNews falhou: ${respAdmin.code()} ${respAdmin.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("LobbyScreen", "erro ao buscar notícias", e)
            }
            delay(25_000)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fundo
        Image(
            painter = painterResource(id = R.drawable.lobby_landscape),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        // Notícias rolando
        NewsMarquee(
            adminMsg = adminFixed,
            latestEvent = latestEvent.lastOrNull().orEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp)
                .align(Alignment.TopCenter)
        )

        // Card jogador
        Box(
            modifier = Modifier
                .offset(x = screenWidth * 0.01f, y = screenHeight * -0.015f)
                .size(width = 250.dp, height = 140.dp)
                .align(Alignment.TopStart)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_player),
                contentDescription = "Card do Jogador",
                modifier = Modifier.fillMaxSize().align(Alignment.Center)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .size(67.dp)
                    .offset(x = screenHeight * -0.05f, y = screenWidth * 0.025f)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(avatarUrl ?: "https://api.dicebear.com/7.x/initials/png?seed=${nomeUsuario}"),
                        contentDescription = "Foto do Jogador",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                    Text(
                        text = nomeUsuario,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .offset(x = screenHeight * 0.25f, y = screenWidth * 0.01f)
                            .widthIn(max = 160.dp)
                    )
                }
            }
        }

        // Casual Coin
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .offset(x = screenWidth * -0.16f, y = screenHeight * -0.45f)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.casual_coin),
                contentDescription = "CasualCoin Saldo",
                modifier = Modifier
                    .width(110.dp)
                    .height(45.dp)
            )
            Text(
                text = "0,00",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA000),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }

        // Pano Coin
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .offset(x = screenWidth * -0.05f, y = screenHeight * -0.45f)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pano_coin),
                contentDescription = "PanoCoin Saldo",
                modifier = Modifier
                    .width(110.dp)
                    .height(45.dp)
            )

            Text(
                text = String.format(Locale.getDefault(), "%.2f", saldoUsuario),
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA000),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }

        // Cards centrais
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .offset(x = screenWidth * 0.14f)
        ) {
            val tamanho = 750.dp // usa fixo

            Box(
                modifier = Modifier
                    .width(tamanho)
                    .height(tamanho)
                    .align(Alignment.Center)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .graphicsLayer { translationX = 150f }, // valor fixo
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CarrosselLoby(
                        imagens = listOf(
                            R.drawable.ic_card_betawins,
                            R.drawable.ic_sejapromoter,
                        ),
                        modifier = Modifier
                            .width(180.dp)
                            .aspectRatio(0.75f)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        LazyRow(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(2) { index ->
                                val (imagem, rota, textoSobreposto) = when (index) {
                                    0 -> Triple(R.drawable.ic_cardjogar, "mesas_poker", "JOGAR")
                                    1 -> Triple(R.drawable.ic_cardtorneio, "perfil", "TORNEIOS")
                                    else -> Triple(R.drawable.ic_cardjogar, "mesas_poker", null)
                                }

                                Box(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .aspectRatio(0.75f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            navController.navigate(rota)
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = imagem),
                                        contentDescription = "Carta ${index + 1}",
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    textoSobreposto?.let {
                                        Text(
                                            text = it,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            //fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(top = 8.dp)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                                .offset(x = screenWidth * 0f, y = screenHeight * -0.2f)
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.width(160.dp + 8.dp))
                            }
                        }

                    }
                }
            }
        }

        // Rodapé
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .offset(x = (-222).dp, y = (-20).dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.nav_bottom_menu),
                contentDescription = "Barra de Navegação",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .offset(x = 222.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(id = R.drawable.ic_market), contentDescription = "Market", modifier = Modifier.size(38.dp))
                Box {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mail),
                        contentDescription = "Mail",
                        modifier = Modifier.size(38.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 1.dp, y = (-4).dp)
                            .background(Color.Red, shape = CircleShape)
                    )
                }

                Icon(painterResource(id = R.drawable.ic_perfil), contentDescription = "Perfil", modifier = Modifier.size(38.dp))
                Icon(painterResource(id = R.drawable.ic_bag), contentDescription = "Bag", modifier = Modifier.size(38.dp))
            }
        }
    }
}
