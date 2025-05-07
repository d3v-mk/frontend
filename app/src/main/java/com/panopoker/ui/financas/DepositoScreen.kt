package com.panopoker.ui.financas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DepositoScreen(navController: NavController) {
    var valorDeposito by remember { mutableStateOf("") }
    var codigoPix by remember { mutableStateOf<String?>(null) }
    var erroDeposito by remember { mutableStateOf<String?>(null) } // ⬅️ NOVO
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Depósito via Pix",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = valorDeposito,
            onValueChange = {
                valorDeposito = it
                erroDeposito = null // limpa o erro ao digitar
            },
            label = { Text("Valor do depósito (min: 3.00)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFD700),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White,
                focusedLabelColor = Color(0xFFFFD700)
            )
        )

        // Exibe a mensagem de erro, se houver
        erroDeposito?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = it,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val valor = valorDeposito.toFloatOrNull()
                if (valor != null && valor >= 3.0f) {
                    erroDeposito = null
                    codigoPix = "0AJIDSJAISDJCODIGOPIXXXXX"
                } else {
                    codigoPix = null
                    erroDeposito = "Insira um valor válido acima de R$ 3.00"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text("Gerar Pix", color = Color.Black, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        codigoPix?.let { codigo ->
            Text(
                text = "Código Pix:",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = codigo,
                color = Color.LightGray,
                modifier = Modifier
                    .background(Color(0xFF1F1F1F), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .fillMaxWidth(0.95f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(codigo))
                    erroDeposito = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Copiar código", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Voltar", color = Color.White)
        }
    }
}
