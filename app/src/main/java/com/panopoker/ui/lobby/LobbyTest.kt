package com.panopoker.ui.lobby

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.panopoker.R
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import com.panopoker.model.Jogador
import com.panopoker.ui.lobby.components.CarrosselLoby
import com.panopoker.ui.lobby.components.NewsMarquee
import kotlinx.coroutines.delay


@Composable
fun LobbyScreenTest(navController: NavController) {
    val context = LocalContext.current
    var saldoUsuario by remember { mutableStateOf(0.0f) }
    var nomeUsuario by remember { mutableStateOf("Jogador") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }
    val session = remember { SessionManager(context) }
    var idPublico by remember { mutableStateOf("") }

    val token = SessionManager.getToken(context)

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
        //polling dos ends noticias
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
            delay(25_000)
        }
    }

    // Valores pra passar pro marquee
    val adminFixed = adminNoticias.firstOrNull() ?: ""
    val latestEvent = noticias.toList()

    Box(modifier = Modifier.fillMaxSize()) {
        // FUNDOZÃO
        Image(
            painter = painterResource(id = R.drawable.lobby_landscape), // troca pelo seu fundo real
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // CONTEÚDO SOBRE O FUNDO
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
        ) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight

            NewsMarquee(
                adminMsg    = adminFixed,
                latestEvent = latestEvent.lastOrNull().orEmpty(),  // ⬅️ aqui: String, não List<String>
            )

            // Card do jogador
            Box(
                modifier = Modifier
                    .offset(x = 10.dp, y = (-10).dp)
                    .size(width = 250.dp, height = 140.dp) // aumenta o card se quiser
            ) {
                // Fundo SVG (grande e centralizado)
                Image(
                    painter = painterResource(id = R.drawable.ic_player),
                    contentDescription = "Card do Jogador",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )

                // Avatar + nome sobrepostos no canto superior esquerdo
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(67.dp) // tamanho da imagem
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(avatarUrl ?: "https://i.pravatar.cc/150?img=3"),
                            contentDescription = "Foto do Jogador",
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(x = (-16).dp, y = 20.dp)
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
                                .absoluteOffset(x = 80.dp, y = 30.dp) // 👈 ajusta aqui como quiser!
                                .widthIn(max = 160.dp) // pra não quebrar layout
                        )
                    }
                }
            }

            // casual coin png
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .offset(x = -maxWidth * 0.15f, y = (-155).dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.casual_coin),
                    contentDescription = "CasualCoin Saldo",
                    modifier = Modifier
                        .width(maxWidth * 0.125f)
                        .height(45.dp)
                )

                Text(
                    text = "0,00",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA000),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(x = 0.dp, y = 0.dp)
                )
            }

            // pano coin
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .offset(x = -maxWidth * 0.018f, y = (-155).dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pano_coin),
                    contentDescription = "PanoCoin Saldo",
                    modifier = Modifier
                        .width(maxWidth * 0.125f)
                        .height(45.dp)
                )

                Text(
                    text = String.format("%.2f", saldoUsuario),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA000),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(x = 0.dp, y = 0.dp)
                )
            }

            // cards centrais
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                val tamanho = if (maxWidth < 650.dp) maxWidth else 650.dp

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
                            .graphicsLayer {
                                translationX = size.width * 0.15f
                            },
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Carrossel rotativo (primeira imagem)
                        CarrosselLoby(
                            imagens = listOf(
                                R.drawable.ic_card_betawins,
                                R.drawable.ic_sejapromoter,
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Imagens fixas e clicáveis
                        Image(
                            painter = painterResource(id = R.drawable.ic_cardjogar),
                            contentDescription = "Carta 2",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { /* ação carta 2 */ }
                        )

                        Image(
                            painter = painterResource(id = R.drawable.ic_cardsgames),
                            contentDescription = "Carta 3",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { /* ação carta 3 */ }
                        )

                        Image(
                            painter = painterResource(id = R.drawable.ic_cardsgames),
                            contentDescription = "Carta 4",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { /* ação carta 4 */ }
                        )
                    }
                }
            }


            // Rodapé
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .offset(x = (-222).dp, y = (-20).dp) // 👈 Move tudo pra esquerda e pra cima
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nav_bottom_menu),
                    contentDescription = "Barra de Navegação",
                    modifier = Modifier
                        .width(maxWidth)
                        .height(45.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .offset(x = 222.dp, y = 0.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_market),
                        contentDescription = "Market",
                        modifier = Modifier.size(38.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mail),
                        contentDescription = "Mail",
                        modifier = Modifier.size(38.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_perfil),
                        contentDescription = "Perfil",
                        modifier = Modifier.size(38.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bag),
                        contentDescription = "Bag",
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
        }
    }
}







//@Preview(showBackground = true, widthDp = 891, heightDp = 411)
//@Composable
//fun PreviewLobbyScreenTest() {
//    // cria um Jogador fake com dados mockados
//    val jogadorFake = Jogador(
//        id = 1,
//        user_id = 123,
//        username = "Katari Da Silva",
//        email = "katari@example.com",
//        is_admin = false,
//        saldo_inicial = 100f,
//        saldo_atual = 423.45f,
//        aposta_atual = 0f,
//        foldado = false,
//        rodada_ja_agiu = false,
//        cartas = listOf("Ah", "Kd"),
//        vez = false,
//        posicao_cadeira = 1,
//        participando_da_rodada = true,
//        is_sb = false,
//        avatarUrl = "https://i.pravatar.cc/150?img=3",
//        is_bb = false,
//        is_dealer = false
//    )
//
//    LobbyScreenTest(navController = rememberNavController())
//}
//
//
//
