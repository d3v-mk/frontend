package com.panopoker.ui.lobby

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.panopoker.R
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex

@Composable
fun LobbyScreenTest(navController: NavController) {
    val playerPhotoUrl = "https://i.pravatar.cc/150?img=3"

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
                        modifier = Modifier
                            .size(67.dp) // tamanho da imagem
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter("https://i.pravatar.cc/150?img=3"),
                            contentDescription = "Foto do Jogador",
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(x = (-16).dp, y = 20.dp)
                                .clip(CircleShape)
                        )

                        Text(
                            text = "Katarina Da silva",
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

                // Texto mockado por cima da imagem
                Text(
                    text = "123,45",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA000),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .offset(x = 0.dp, y = (0).dp)
                )
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .offset(x = -maxWidth * 0.018f, y = (-155).dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                // Fundo com a imagem
                Image(
                    painter = painterResource(id = R.drawable.pano_coin),
                    contentDescription = "PanoCoin Saldo",
                    modifier = Modifier
                        .width(maxWidth * 0.125f)
                        .height(45.dp)
                )

                // Texto mockado por cima da imagem
                Text(
                    text = "123,45",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA000),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .offset(x = 0.dp, y = (0).dp)
                )
            }


            //cards centrais
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
                                translationX = size.width * 0.15f // muda 0.2f pra o quanto quiser andar
                            },
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_cardsgames),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_cardsgames),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_cardsgames),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_cardsgames),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
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
                // Fundo da barra (imagem com gradiente, etc)
                Image(
                    painter = painterResource(id = R.drawable.nav_bottom_menu),
                    contentDescription = "Barra de Navegação",
                    modifier = Modifier
                        .width(maxWidth)
                        .height(45.dp)
                )

                // Ícones sobrepostos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .offset(x = 222.dp, y = (0).dp)
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

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun PreviewLobbyScreenTest() {
    LobbyScreenTest(navController = rememberNavController())
}
