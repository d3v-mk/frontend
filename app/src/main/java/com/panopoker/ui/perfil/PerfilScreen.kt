package com.panopoker.ui.perfil

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import com.panopoker.model.PerfilResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.alpha
import com.google.accompanist.flowlayout.FlowRow
import com.panopoker.BuildConfig



data class Conquista(
    val nome: String,
    val emoji: String,
    val descricao: String,
    val desbloqueada: Boolean
)

@Composable
fun PerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val avatarUrlCacheBuster = remember { mutableStateOf(System.currentTimeMillis()) }
    val session = remember { SessionManager(context) }
    var perfil by remember { mutableStateOf<PerfilResponse?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                try {
                    val stream = context.contentResolver.openInputStream(uri)
                    val bytes = stream?.readBytes()
                    if (bytes != null && bytes.isNotEmpty()) {
                        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("file", "avatar.jpg", requestBody)
                        val token = SessionManager.getToken(context)
                        val response = RetrofitInstance.usuarioApi.uploadAvatar("Bearer $token", part)
                        if (response.isSuccessful) {
                            avatarUrlCacheBuster.value = System.currentTimeMillis()
                            response.body()?.let { body ->
                                Log.d("PANO_DEBUG", "Upload feito com sucesso!")
                                Log.d("PANO_DEBUG", "URL do novo avatar: ${body.avatar_url}")
                                session.saveAvatarUrl(body.avatar_url ?: "")
                            }
                        } else {
                            Log.e("PANO_DEBUG", "Falha no upload: ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PANO_DEBUG", "Erro no upload: ${e.message}")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val token = SessionManager.getToken(context)
        try {
            val response = RetrofitInstance.usuarioApi.getPerfil("Bearer $token")
            if (response.isSuccessful) {
                Log.d("PANO_DEBUG", "Perfil carregado com sucesso!")
                perfil = response.body()
                Log.d("PANO_DEBUG", "Perfil completo: $perfil")
                perfil?.avatarUrl?.let {
                    Log.d("PANO_DEBUG", "Avatar direto do perfil: $it")
                    session.saveAvatarUrl(it)
                }
            } else {
                Log.e("PANO_DEBUG", "Erro ao carregar perfil: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("PANO_DEBUG", "Erro ao buscar perfil: ${e.message}")
        }
    }

    perfil?.let { user ->
        val baseUrl = BuildConfig.API_BASE_URL // IPZADA
        val avatarRaw = user.avatarUrl ?: ""
        val finalAvatarUrl = if (avatarRaw.startsWith("http")) {
            "$avatarRaw?t=${avatarUrlCacheBuster.value}"
        } else {
            "$baseUrl$avatarRaw?t=${avatarUrlCacheBuster.value}"
        }

        Log.d("PANO_DEBUG", "Avatar recebido do backend: ${user.avatarUrl}")
        Log.d("PANO_DEBUG", "URL FINAL usada no AsyncImage: $finalAvatarUrl")


        val conquistas = listOf(
            Conquista("Royal Flush", "👑", "Faça um Royal Flush!", user.royal_flushes > 0),
            Conquista("Straight Flush", "👺", "Faça um Straight Flush!", user.straight_flushes > 0),
            Conquista("Top 1", "🏆", "Fique em 1º no ranking!", user.vezes_no_top1 > 0),
            Conquista("Campeão", "🥇", "Fique em 1º no torneio!", user.vezes_no_top1 > 999999), //arrumar(qnd tiver torneio)
            Conquista("Peixe", "🐟", "Total de 150 fichas ganhas", user.fichas_ganhas > 150),
            Conquista("Tubarão", "🦈", "Total de 500 fichas ganhas", user.fichas_ganhas > 500),
            Conquista("Baleia", "🐋", "Total de 1000 fichas ganhas", user.fichas_ganhas > 1000),
            Conquista("Honey Pot", "🍯", "Ganhe um pote de 100", user.maior_pote > 100),
            Conquista("Honey Honey Pot", "🐝", "Ganhe um pote de 500", user.maior_pote > 500),
            Conquista("Promotor", "🤵🏻‍♂️", "Parabéns, você é promotor do Pano!", user.is_promoter), //
            Conquista("Beta Tester", "️🎉", "Participou da versão Beta do Pano!", user.beta_tester > 0), //
            //Conquista("1 ano de serviço", "️1️⃣", "Joga desde {data}", user.is_promoter) //


        )

        val conquistasDesbloqueadas = conquistas.filter { it.desbloqueada }
        val temConquista = conquistasDesbloqueadas.isNotEmpty()

        var showDialog by remember { mutableStateOf(false) }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = finalAvatarUrl,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(130.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .border(2.dp, Color.Green, CircleShape)
            )

            TextButton(onClick = { launcher.launch("image/*") }) {
                Text("Alterar Avatar", color = Color.Yellow, fontSize = 14.sp)
            }

            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232323)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Ver todas conquistas", color = Color(0xFFFFD700))
            }

            Divider(
                color = Color(0xFF363636),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 14.dp)
            )

            Text(
                text = "\uD83C\uDF1F Conquistas",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Chips das conquistas, tudo em uma Row horizontal e scrollável caso queira mais conquistas futuramente
            FlowRow(
                mainAxisSpacing = 10.dp,
                crossAxisSpacing = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                if (temConquista) {
                    conquistasDesbloqueadas.forEach { c ->
                        AssistChip(
                            onClick = {}, // Pode abrir tooltip ou não fazer nada
                            label = { Text("${c.emoji} ${c.nome}") },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF2C2C2C))
                        )
                    }
                } else {
                    Text(
                        "Sem conquistas ainda 😕",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            Text("\uD83D\uDCCA Estatísticas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))

            val stats = listOf(
                "Rodadas ganhas" to user.rodadas_ganhas,
                "Rodadas jogadas" to user.rodadas_jogadas,
                "Win Rate" to if (user.rodadas_jogadas > 0) "${(user.rodadas_ganhas * 100 / user.rodadas_jogadas)}%" else "0%",
                "Fichas ganhas" to "%.2f".format(user.fichas_ganhas),
                "Maior pote ganho" to "%.2f".format(user.maior_pote),
                "Torneios vencidos" to user.torneios_vencidos,
                "Sequências" to user.sequencias,
                "Flushes" to user.flushes,
                "Full Houses" to user.full_houses,
                "Quadras" to user.quadras,
                "Straight Flushes" to user.straight_flushes,
                "Royal Flushes" to user.royal_flushes,
                //"Fichas perdidas" to "R$ %.2f".format(user.fichas_perdidas)
                //"Mão favorita" to (user.mao_favorita ?: "—")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stats) { (label, value) ->
                    Card(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        border = BorderStroke(1.dp, Color.DarkGray)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = label, fontSize = 12.sp, color = Color.Gray)
                            Text(text = value.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }


            /// dialoag para abrir todas as conquistas
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                shape = RoundedCornerShape(30)
                            ) {
                                Text("Fechar", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    title = {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(Color(0xFFFFD700), shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            )
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "\uD83C\uDF1F ",
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(
                                    "Conquistas",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }
                    },
                    text = {
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .background(Color(0xFF181818), shape = RoundedCornerShape(12.dp))
                                .padding(4.dp)
                        ) {
                            conquistas.forEachIndexed { idx, c ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
                                        .alpha(if (c.desbloqueada) 1f else 0.4f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                if (c.desbloqueada) Color(0xFF232323) else Color(0xFF262626),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = c.emoji,
                                            fontSize = 22.sp
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = c.nome,
                                            fontWeight = if (c.desbloqueada) FontWeight.Bold else FontWeight.Normal,
                                            color = if (c.desbloqueada) Color(0xFFFFD700) else Color.Gray,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = c.descricao,
                                            fontSize = 12.sp,
                                            color = if (c.desbloqueada) Color.White else Color.LightGray,
                                            maxLines = 3
                                        )
                                    }
                                }
                                if (idx < conquistas.lastIndex) {
                                    Divider(
                                        color = Color(0xFF222222),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp)
                                    )
                                }
                            }
                        }
                    },
                    containerColor = Color(0xFF232323),
                    shape = RoundedCornerShape(18.dp)
                )
            }


        }

    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Yellow)
        }
    }
}
