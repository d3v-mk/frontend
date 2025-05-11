package com.panopoker.ui.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.panopoker.data.api.loginWithGoogleToken

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import com.panopoker.R


@Composable
fun GoogleLoginScreen(onLoginSuccess: (String) -> Unit) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleLogin", "Account recebido: $account") // nova linha
            val idToken = account.idToken
            Log.d("GoogleLogin", "idToken recebido: $idToken") // nova linha
            if (idToken != null) {
                Log.d("GoogleLogin", "ID TOKEN: $idToken")
                loginWithGoogleToken(idToken, onLoginSuccess)
            } else {
                Log.e("GoogleLogin", "idToken é nulo")
            }
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "Erro ao fazer login", e)
        }
    }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("477222874066-0rb8c3o8hsms1kjbdlcitplnvkbvtbkp.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    // BOTÃO ESTILIZADO
    Button(
        onClick = {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo), // seu logo aqui
            contentDescription = "Google Sign-In",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Entrar com Google")
    }
}