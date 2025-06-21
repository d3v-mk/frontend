package com.panopoker.ui.perfil

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.panopoker.BuildConfig
import com.panopoker.R
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import com.panopoker.model.PerfilResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


data class Conquista(
    val nome: String,
    val emoji: String,
    val descricao: String,
    val desbloqueada: Boolean
)

@Composable
fun PerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val coroutineScope = rememberCoroutineScope()
    val avatarUrlCacheBuster = remember { mutableStateOf(System.currentTimeMillis()) }
    val session = remember { SessionManager(context) }
    var perfil by remember { mutableStateOf<PerfilResponse?>(null) }
    var showDialog by remember { mutableStateOf(false) }

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
                perfil = response.body()
                perfil?.avatarUrl?.let {
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
        val baseUrl = BuildConfig.API_BASE_URL
        val avatarRaw = user.avatarUrl ?: ""
        val finalAvatarUrl = if (avatarRaw.startsWith("http")) {
            "$avatarRaw?t=${avatarUrlCacheBuster.value}"
        } else {
            "$baseUrl$avatarRaw?t=${avatarUrlCacheBuster.value}"
        }

        val conquistas = listOf(
            Conquista("Royal Flush", "👑", "Faça um Royal Flush!", user.royal_flushes > 0),
            Conquista("Straight Flush", "👺", "Faça um Straight Flush!", user.straight_flushes > 0),
            Conquista("Top 1", "🏆", "Fique em 1º no ranking!", user.vezes_no_top1 > 0),
            Conquista("Campeão", "🥇", "Fique em 1º no torneio!", user.vezes_no_top1 > 999999),
            Conquista("Peixe", "🐟", "Total de 150 fichas ganhas", user.fichas_ganhas > 150),
            Conquista("Tubarão", "🦈", "Total de 500 fichas ganhas", user.fichas_ganhas > 500),
            Conquista("Baleia", "🐋", "Total de 1000 fichas ganhas", user.fichas_ganhas > 1000),
            Conquista("Honey Pot", "🍯", "Ganhe um pote de 25 fichas", user.maior_pote > 25),
            Conquista("Honey Honey Pot", "🐝", "Ganhe um pote de 50 fichas", user.maior_pote > 50),
            Conquista("Promotor", "🤵🏻‍♂️", "Parabéns, você é promotor do Pano!", user.is_promoter),
            Conquista("Beta Tester", "️🎉", "Participou da versão Beta do Pano!", user.beta_tester > 0),
            Conquista("1 ano de serviço", "️1️⃣", "Joga no Pano ja faz 1 ano!", user.vezes_no_top1 > 999999)
        )

        val conquistasDesbloqueadas = conquistas.filter { it.desbloqueada }
        val temConquista = conquistasDesbloqueadas.isNotEmpty()

        if (isLandscape) {
            PerfilLandscape(user, finalAvatarUrl, conquistas, conquistasDesbloqueadas, temConquista, launcher, showDialog) { showDialog = it }
        } else {
            PerfilPortrait(user, finalAvatarUrl, conquistas, conquistasDesbloqueadas, temConquista, launcher, showDialog) { showDialog = it }
        }
    }
}

@Composable
fun PerfilLandscape(
    user: PerfilResponse,
    finalAvatarUrl: String,
    conquistas: List<Conquista>,
    conquistasDesbloqueadas: List<Conquista>,
    temConquista: Boolean,
    launcher: ManagedActivityResultLauncher<String, Uri?>,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Coluna da esquerda: avatar, botões e conquistas rápidas
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = finalAvatarUrl,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.avatar_default),
                error = painterResource(R.drawable.avatar_default),
                fallback = painterResource(R.drawable.avatar_default),
                modifier = Modifier
                    .size(130.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .border(2.dp, Color.Green, CircleShape)
            )

            TextButton(onClick = { launcher.launch("image/*") }) {
                Text("Alterar Avatar", color = Color.Yellow, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "\uD83C\uDF1F Conquistas",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 10.dp)
            )

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
                            onClick = { /* Talvez abrir tooltip no futuro */ },
                            label = { Text("${c.emoji} ${c.nome}") },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF2C2C2C))
                        )
                    }
                } else {
                    Text(
                        "Sem conquistas ainda 😕",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            Button(
                onClick = { setShowDialog(true) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232323)),
            ) {
                Text("Ver todas conquistas", color = Color(0xFFFFD700))
            }
        }

        // Coluna da direita: estatísticas
        Column(
            modifier = Modifier
                .weight(2f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "\uD83D\uDCCA Estatísticas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
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
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp),
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
        }
    }
}

@Composable
fun PerfilPortrait(
    user: PerfilResponse,
    finalAvatarUrl: String,
    conquistas: List<Conquista>,
    conquistasDesbloqueadas: List<Conquista>,
    temConquista: Boolean,
    launcher: ManagedActivityResultLauncher<String, Uri?>,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit
) {
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
            placeholder = painterResource(R.drawable.avatar_default),
            error = painterResource(R.drawable.avatar_default),
            fallback = painterResource(R.drawable.avatar_default),
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
            onClick = { setShowDialog(true) },
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
                        onClick = {},
                        label = { Text("${c.emoji} ${c.nome}") },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF2C2C2C))
                    )
                }
            } else {
                Text(
                    "Sem conquistas ainda 😕",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
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
    }
}
