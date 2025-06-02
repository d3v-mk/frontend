package com.panopoker.ui.splash

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panopoker.R
import com.panopoker.data.api.AuthApi
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.session.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: (String) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val fullText = "joga a bet no pano!"
    var typedText by remember { mutableStateOf("") }

    val syncopateFont = FontFamily(Font(R.font.syncopate_bold))
    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    LaunchedEffect(Unit) {
        visible = true

        for (i in 1..fullText.length) {
            typedText = fullText.take(i)
            delay(50)
        }

        delay(1300)

        val token = session.fetchAuthToken()
        val userId = session.fetchUserId()

        if (token != null && userId != -1) {
            try {
                val api = RetrofitInstance.retrofit.create(AuthApi::class.java)
                val usuario = api.getUsuario(userId, "Bearer $token")
                Log.d("SplashScreen", "Usuário válido: ${usuario.nome}")
                onSplashFinished("lobby")
            } catch (e: Exception) {
                Log.e("SplashScreen", "Erro ao validar token", e)
                session.clearSession()
                onSplashFinished("login")
            }
        } else {
            onSplashFinished("login")
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val offsetY = with(density) {
            -constraints.maxHeight.toDp() * 0.05f
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = typedText,
                    fontSize = 13.sp,
                    fontFamily = syncopateFont,
                    color = Color(0xFFFFD700),
                    modifier = Modifier.offset(y = offsetY)
                )
            }
        }
    }
}