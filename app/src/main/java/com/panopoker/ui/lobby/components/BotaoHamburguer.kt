package com.panopoker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panopoker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter


@Composable
fun BotaoHamburguer(drawerState: DrawerState, scope: CoroutineScope) {
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

@Composable
fun MenuLateralCompleto(
    drawerState: DrawerState,
    scope: CoroutineScope,
    nomeUsuario: String,
    idPublico: String?,
    avatarUrl: String?,
    saldoUsuario: Float,
    navController: NavController,
    conteudo: @Composable () -> Unit
) {
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
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .border(width = 3.dp, color = Color.Green, shape = CircleShape) // borda verde fina
                            .background(Color(0xFF323232), shape = CircleShape), // fundo escuro interno
                        contentAlignment = Alignment.Center
                    ) {
                        val avatarPainter = if (!avatarUrl.isNullOrBlank()) {
                            rememberAsyncImagePainter(avatarUrl + "?nocache=${System.currentTimeMillis()}")
                        } else {
                            painterResource(id = R.drawable.avatar_default)
                        }
                        Image(
                            painter = avatarPainter,
                            contentDescription = "Avatar do jogador",
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(Color.Gray, shape = CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nome + status badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = nomeUsuario,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Green, shape = RoundedCornerShape(50))
                        )

                    }

                    Text(
                        text = "Status: (em breve)",
                        fontSize = 15.sp,
                        color = Color(0xFFFFE082),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                    )

                    Text(
                        text = "ID: ${idPublico ?: "Carregando..."}",
                        fontSize = 13.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF232323),
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ficha_poker),
                                contentDescription = "Ficha PanoPoker",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = " ${"%.2f".format(saldoUsuario)}",
                                fontSize = 18.sp,
                                color = Color(0xFFFFC300),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                DrawerItem("Perfil \uD83D\uDC64") {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("perfil")
                    }
                }

                DrawerItem("Amigos 👥") { /* implementar depois */ }

                DrawerItem("Equipe 🔥") { /* implementar depois */ }

                DrawerItem("Rank 🏆") { /* implementar depois */ }
                DrawerItem("Meu VIP 💎") { /* implementar depois */ }

                DrawerItem("Promotores \uD83E\uDD1D") {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("promotores")
                    }
                }
            }
        },
        content = { conteudo() }
    )
}


@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 18.sp,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

