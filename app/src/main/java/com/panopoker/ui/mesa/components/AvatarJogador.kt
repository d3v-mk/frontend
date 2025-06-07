package com.panopoker.ui.mesa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.panopoker.R
import com.panopoker.model.Jogador
import kotlinx.coroutines.delay
import androidx.compose.foundation.clickable

@Composable
fun AvatarJogador(
    jogador: Jogador,
    usuarioLogadoId: Int,
    jogadorDaVezId: Int,
    progressoTimer: Float,
    onClickJogador: (Jogador) -> Unit
) {
    val tempoTotal = 20
    var tempoRestante by remember { mutableStateOf(tempoTotal) }

    // contador regressivo (timer visual) no avatar do jogador que está na vez
    LaunchedEffect(jogador.vez) {
        if (jogador.vez) {
            tempoRestante = tempoTotal
            while (tempoRestante > 0) {
                delay(1000)
                tempoRestante--
            }
        }
    }

    val progresso = tempoRestante / tempoTotal.toFloat()

    /// atualiza o cache do coil pra mostrar o avatar novo + fallback
    val avatarTimestamp = remember(jogador.avatarUrl) { System.currentTimeMillis() }
    val fallback = "https://i.imgur.com/1MfqtXH.png"
    val baseUrl = jogador.avatarUrl?.takeIf { it.isNotBlank() } ?: fallback
    val finalAvatarUrl = "$baseUrl?v=$avatarTimestamp"
    ///

    BoxWithConstraints(
        modifier = Modifier.zIndex(1f)
    ) {
        val avatarBoxSize = maxWidth * 0.093f
        val imageSize = avatarBoxSize * 0.88f
        val iconSize = maxWidth * 0.017f
        val fontSizeNome = maxWidth.value * 0.014f
        val fontSizeSaldo = maxWidth.value * 0.014f
        val fontSizeBlind = maxWidth.value * 0.014f
        val paddingHorizontal = maxWidth * 0.005f
        val paddingVertical = maxWidth * 0.003f
        val dealerSize = maxWidth * 0.016f // 👈 ajusta conforme tela

        val deslocamento = maxHeight * 0.01f //

        val fontSizeCla = (maxWidth.value * 0.012f).sp

        Column(
            modifier = Modifier
                .offset(y = if (jogador.user_id != usuarioLogadoId) (-15).dp else 0.dp), // <<
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(avatarBoxSize)) {
                if (jogador.user_id == jogadorDaVezId) {
                    TimerCircular(progresso = progressoTimer, tamanho = avatarBoxSize)
                }


                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(finalAvatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar jogador",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(imageSize)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .shadow(6.dp, CircleShape)
                        .clickable { onClickJogador(jogador) }
                )

                // 👇 Overlay escuro se foldado
                if (jogador.foldado == true) {
                    Box(
                        modifier = Modifier
                            .size(imageSize)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)), // Escurece
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "FOLD",
                            color = Color.Red,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (imageSize.value * 0.12f).sp
                        )
                    }
                }
            }

            // ✅ CLÃ + NOME + SALDO + FICHA + BLINDS
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.offset(y = -deslocamento)
            ) {
//                Text(
//                    text = "🔥 Team BRAVO 🔥",
//                    color = Color(0xFFFFD700),
//                    fontSize = fontSizeCla,
//                    fontWeight = FontWeight.SemiBold
//                )

                // ✅ Fundo agora só aqui!
                Box(
                    modifier = Modifier
                        .background(Color(0xFF555555), RoundedCornerShape(6.dp))
                        .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            jogador.username,
                            color = Color.White,
                            fontSize = fontSizeNome.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Image(
                            painter = painterResource(id = R.drawable.ficha_poker),
                            contentDescription = null,
                            modifier = Modifier.size(iconSize)
                        )

                        Text(
                            "%.2f".format(jogador.saldo_atual),
                            color = Color(0xFFFFD700),
                            fontSize = fontSizeSaldo.sp
                        )

                        if (jogador.is_sb) {
                            Text(
                                "SB",
                                color = Color.Cyan,
                                fontSize = fontSizeBlind.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (jogador.is_bb) {
                            Text(
                                "BB",
                                color = Color.Magenta,
                                fontSize = fontSizeBlind.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (jogador.is_dealer) {
                            Box(
                                modifier = Modifier
                                    .size(dealerSize)
                                    .background(Color.White, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "D",
                                    color = Color.Black,
                                    fontSize = (dealerSize.value * 0.6f).sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}