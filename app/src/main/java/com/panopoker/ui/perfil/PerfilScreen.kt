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
        val baseUrl = "http://192.168.0.9:8000"
        val avatarRaw = user.avatarUrl ?: ""
        val finalAvatarUrl = if (avatarRaw.startsWith("http")) {
            "$avatarRaw?t=${avatarUrlCacheBuster.value}"
        } else {
            "$baseUrl$avatarRaw?t=${avatarUrlCacheBuster.value}"
        }

        Log.d("PANO_DEBUG", "Avatar recebido do backend: ${user.avatarUrl}")
        Log.d("PANO_DEBUG", "URL FINAL usada no AsyncImage: $finalAvatarUrl")

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
                    .border(2.dp, Color.Yellow, CircleShape)
            )

            TextButton(onClick = { launcher.launch("image/*") }) {
                Text("Alterar Avatar", color = Color.Yellow, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = user.nome, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFFD700))
            Text(text = "ID: ${user.id_publico}", fontSize = 14.sp, color = Color.LightGray, fontFamily = FontFamily.Monospace)

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (user.royal_flushes > 0) {
                    AssistChip(onClick = {}, label = { Text("\uD83D\uDC51 Royal Flush") }, colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF3A3A3A)))
                }
                if (user.vezes_no_top1 > 0) {
                    AssistChip(onClick = {}, label = { Text("\uD83C\uDFC6 Top 1 Ranking") }, colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF2E2E2E)))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("\uD83D\uDCCA Estatísticas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))

            val stats = listOf(
                "Rodadas ganhas" to user.rodadas_ganhas,
                "Rodadas jogadas" to user.rodadas_jogadas,
                "Win Rate" to if (user.rodadas_jogadas > 0) "${(user.rodadas_ganhas * 100 / user.rodadas_jogadas)}%" else "0%",
                "Sequências" to user.sequencias,
                "Flushes" to user.flushes,
                "Full Houses" to user.full_houses,
                "Quadras" to user.quadras,
                "Straight Flushes" to user.straight_flushes,
                "Royal Flushes" to user.royal_flushes,
                "Torneios vencidos" to user.torneios_vencidos,
                "Maior pote ganho" to "R$ %.2f".format(user.maior_pote),
                "Fichas ganhas" to "R$ %.2f".format(user.fichas_ganhas),
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("ranking_mensal") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Ranking Mensal", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Yellow)
        }
    }
}
