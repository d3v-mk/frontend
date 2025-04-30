package com.panopoker.ui.mesa

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.panopoker.ui.theme.PanopokerTheme
import com.panopoker.data.session.SessionManager
import com.panopoker.data.network.RetrofitInstance
import com.panopoker.data.service.MesaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MesaActivity : ComponentActivity() {
    private var mesaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        mesaId = intent.getIntExtra("mesaId", -1)

        // Botão voltar
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val session = SessionManager(this@MesaActivity)
                val token = session.fetchAuthToken() ?: ""

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitInstance.retrofit
                            .create(MesaService::class.java)
                            .sairDaMesa(mesaId, "Bearer $token")
                    } catch (_: Exception) {
                        // Ignora erros
                    } finally {
                        finish()
                    }
                }
            }
        })

        setContent {
            PanopokerTheme {
                MesaScreen(mesaId = mesaId, navController = null)

                // Fullscreen após montar o conteúdo
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        window.setDecorFitsSystemWindows(false)
                        window.insetsController?.let { controller ->
                            controller.hide(
                                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
                            )
                            controller.systemBarsBehavior =
                                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_FULLSCREEN or
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    }
                }
            }
        }
    }
}