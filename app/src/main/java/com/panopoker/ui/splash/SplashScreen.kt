package com.panopoker.ui.splash

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panopoker.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val fullText = "joga a bet no pano!"
    var typedText by remember { mutableStateOf("") }

    val syncopateFont = FontFamily(Font(R.font.syncopate_bold))

    LaunchedEffect(Unit) {
        visible = true

        for (i in 1..fullText.length) {
            typedText = fullText.take(i)
            delay(50)
        }

        delay(1300)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_pano),
                        contentDescription = "Logo PanoPoker",
                        modifier = Modifier
                            .size(500.dp)
                            .shadow(10.dp, RoundedCornerShape(20.dp))
                    )
                }

                Text(
                    text = typedText,
                    fontSize = 13.sp,
                    fontFamily = syncopateFont,
                    color = Color(0xFFFFD700),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 230.dp)
                )
            }
        }
    }
}
