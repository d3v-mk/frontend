package com.panopoker.ui.mesa.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panopoker.data.network.WebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.fillMaxWidth

/**
 * Botão "hamburguer" na Mesa, com menu para ações extras.
 * Posicionado no canto superior direito, com menu ancorado no botão.
 */

@Composable
fun BotaoHamburguerMesa(
    context: Context,
    webSocketClient: WebSocketClient,
    coroutineScope: CoroutineScope,
    menuOptions: List<MenuOption> = listOf(
        MenuOption("Sair") {
            coroutineScope.launch {
                try {
                    webSocketClient.sairDaMesa()
                    (context as? Activity)?.finish()
                } catch (_: Exception) {}
            }
        }
    )
) {
    var expanded by remember { mutableStateOf(false) }

    // Caixa ocupa a tela inteira para posicionar no canto
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Caixa interna apenas do tamanho do botão + menu, alinhada no TopEnd
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.DarkGray)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                menuOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option.label,
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        onClick = {
                            expanded = false
                            option.onClick()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Representa uma opção no menu do BotaoHamburguerMesa.
 */
data class MenuOption(
    val label: String,
    val onClick: () -> Unit
)
