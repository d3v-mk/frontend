package com.panopoker.ui.mesa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.panopoker.ui.mesa.components.PerfilDoJogadorDialog
import com.panopoker.ui.mesa.components.VencedoresShowdown

import com.panopoker.model.Jogador
import com.panopoker.model.PerfilResponse
import com.panopoker.model.ShowdownDto


@Composable
fun MesaDialogs(
    mostrarDialogManutencao: MutableState<Boolean>,
    showVencedores: Boolean,
    showdownInfoPersistente: ShowdownDto?,
    jogadores: List<Jogador>,
    perfilSelecionado: MutableState<PerfilResponse?>,
    mostrarDialog: MutableState<Boolean>,
    showSemFichasDialog: Boolean
) {
    // DIALOG DE MANUTENCAO NAO TA FUNCIONANDO ARRUMAR DEPOIS
    if (mostrarDialogManutencao.value) {
        AlertDialog(
            onDismissRequest = { mostrarDialogManutencao.value = false },
            title = { Text(text = "Manutenção Programada") },
            text = { Text("Pots distribuidos normalmente. Por favor, aguarde enquanto realizamos melhorias.") },
            confirmButton = {
                TextButton(onClick = { mostrarDialogManutencao.value = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showVencedores && showdownInfoPersistente != null) {
        val listaShowdown = showdownInfoPersistente?.showdown
        if (!listaShowdown.isNullOrEmpty()) {
            VencedoresShowdown(
                vencedores = showdownInfoPersistente!!.vencedores,
                jogadores = jogadores,
                showdown = listaShowdown
            )
        }
    }

    if (mostrarDialog.value && perfilSelecionado.value != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(9999f)
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            PerfilDoJogadorDialog(
                perfil = perfilSelecionado.value!!,
                onDismiss = {
                    mostrarDialog.value = false
                    perfilSelecionado.value = null
                }
            )
        }
    }

    if (showSemFichasDialog) {
        AlertDialog(
            onDismissRequest = { /*...*/ },
            title = { Text("Saldo Insuficiente") },
            text = { Text("Você não tem fichas suficientes para continuar jogando.") },
            confirmButton = {
                Button(onClick = { /* fechar dialog */ }) {
                    Text("OK")
                }
            }
        )
    }
}
