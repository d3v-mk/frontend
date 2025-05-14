package com.panopoker.ui.lobby

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panopoker.R
import com.panopoker.ui.components.BotaoHamburguer
import com.panopoker.ui.components.MenuLateralCompleto
import com.panopoker.data.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun LobbyScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val token = SessionManager.getToken(context)
    var nomeUsuario by remember { mutableStateOf("Jogador") }
    var idPublico by remember { mutableStateOf("") }



    // Use remember para manter o estado mutável do saldo do usuário
    var saldoUsuario by remember { mutableStateOf(0.0f) }

    // Lançar a chamada assíncrona para pegar o saldo quando o Composable for exibido
    LaunchedEffect(Unit) {
        saldoUsuario = session.fetchUserBalance() // Atualiza o saldo com a função suspend
    }

    LaunchedEffect(Unit) {
        if (!token.isNullOrBlank()) {
            try {
                val usuario = com.panopoker.data.network.RetrofitInstance.usuarioService.getUsuarioLogado("Bearer $token")
                nomeUsuario = usuario.nome
                idPublico = usuario.id_publico // ← pega o ID público
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
                // Exibindo a imagem de ficha ao lado do saldo
                Image(
                    painter = painterResource(id = R.drawable.ficha_poker), // Substitua pelo nome correto da sua imagem de ficha
                    contentDescription = "Ficha PanoPoker",
                    modifier = Modifier.size(30.dp), // Ajuste o tamanho da imagem conforme necessário
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp)) // Espaçamento entre a imagem e o texto

                Text(
                    text = "PanoFichas: ${"%.2f".format(saldoUsuario)}",
                    fontSize = 24.sp,
                    color = Color(0xFFFFC300)
                )

                BotaoHamburguer(drawerState, scope)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card: Poker
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("mesas_poker")
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                border = BorderStroke(2.dp, Color(0xFFFFC300)) //amarelo alaranjado
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    // Imagem de fundo
                    Image(
                        painter = painterResource(id = R.drawable.poker_card),
                        contentDescription = "Imagem Poker",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }


            // Card: Blackjack (em breve)
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
                    // Imagem de fundo
                    Image(
                        painter = painterResource(id = R.drawable.blackjack_card),
                        contentDescription = "Imagem Blackjack",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    // Texto sobre a imagem
                    Text(
                        text = "(EM BREVE)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }


            // Card: Slots (em breve)
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
                    // Imagem de fundo
                    Image(
                        painter = painterResource(id = R.drawable.slots_card),
                        contentDescription = "Imagem Slots",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    // Texto sobre a imagem
                    Text(
                        text = "(EM BREVE)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }


            // Card: Banner
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
}
