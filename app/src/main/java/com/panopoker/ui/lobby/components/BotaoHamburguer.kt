package com.panopoker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(100),
                        color = Color.Gray,
                        modifier = Modifier.size(100.dp)
                    ) {
                        val avatarPainter = if (!avatarUrl.isNullOrBlank()) {
                            rememberAsyncImagePainter(avatarUrl + "?nocache=${System.currentTimeMillis()}")
                        } else {
                            painterResource(id = R.drawable.avatar_default)
                        }

                        Image(
                            painter = avatarPainter,
                            contentDescription = "Avatar do jogador",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = nomeUsuario,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )

                    Text(
                        text = "ID: ${idPublico ?: "Carregando..."}",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                DrawerItem("Perfil") {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("perfil")
                    }
                }

                DrawerItem("Depositar") {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("deposito")
                    }
                }

                DrawerItem("Saque") {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("saque")
                    }
                }
                DrawerItem("Configurações") { /* implementar depois */ }
                DrawerItem("Sair") { /* implementar depois */ }
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

