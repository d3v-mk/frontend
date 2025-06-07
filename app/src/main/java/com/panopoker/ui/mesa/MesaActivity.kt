package com.panopoker.ui.mesa

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
import com.panopoker.data.network.WebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MesaActivity : ComponentActivity() {
    private var mesaId: Int = -1
    private lateinit var webSocketClient: WebSocketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notch/Recorte de tela no modo fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        mesaId = intent.getIntExtra("mesa_id", -1)

        // Pega o token do usuário
        val session = SessionManager(this)
        val token = session.fetchAuthToken() ?: ""


        // Botão voltar (back) – agora é full WS!
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        webSocketClient.sairDaMesa()
                        // delay(200) // Se quiser dar um delay antes de fechar
                    } catch (_: Exception) {}
                    finish()
                }
            }
        })

        setContent {
            PanopokerTheme {
                MesaScreen(
                    mesaId = mesaId,
                )

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

    override fun onDestroy() {
        super.onDestroy()
    }
}
