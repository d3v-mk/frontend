package com.panopoker.ui.saque

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.panopoker.ui.financas.SaqueViewModel
import kotlinx.coroutines.launch

@Composable
fun SaqueScreen() {
    val contexto = LocalContext.current
    val viewModel = remember { SaqueViewModel(contexto) }
    val coroutineScope = rememberCoroutineScope()

    var valorDigitado by remember { mutableStateOf("") }
    val saque = viewModel.saque
    val erro = viewModel.erro

    LaunchedEffect(Unit) {
        viewModel.buscarSaquePendente()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Solicitação de Saque", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        if (saque != null) {
            Text("Valor solicitado: R$ ${saque.valor}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = valorDigitado,
                onValueChange = { valorDigitado = it },
                label = { Text("Confirme o valor") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val sucesso = viewModel.confirmarSaque(valorDigitado)
                        if (sucesso) {
                            Toast.makeText(contexto, "Saque confirmado!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(contexto, "Erro ao confirmar saque", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Saque")
            }

            Spacer(modifier = Modifier.height(16.dp))

            val numeroWhatsApp = "44991578192" // ← Substitui pelo número real
            val mensagem = "Olá, quero sacar R$ ${saque.valor}. Meu ID é ${saque.jogador_id}."

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/$numeroWhatsApp?text=${Uri.encode(mensagem)}")
                    }
                    contexto.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Chamar Promotor no WhatsApp")
            }

        } else {
            Text(erro ?: "Carregando...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
