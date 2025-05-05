package com.panopoker.ui.mesa.components

import android.app.Activity
import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import androidx.compose.foundation.layout.*


@Composable
fun BotaoSair(
    context: Context,
    mesaId: Int,
    accessToken: String,
    coroutineScope: CoroutineScope
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val service = RetrofitInstance.retrofit.create(MesaService::class.java)
                        val response = service.sairDaMesa(mesaId, "Bearer $accessToken")
                        if (response.isSuccessful) (context as? Activity)?.finish()
                    } catch (_: Exception) {}
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .height(36.dp)
        ) {
            Text("Sair", color = Color.White, fontSize = 12.sp)
        }
    }
}

