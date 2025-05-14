package com.panopoker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.panopoker.navigation.PanoPokerNav
import com.panopoker.ui.theme.PanopokerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Permite que o conteúdo ocupe toda a tela
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PanopokerTheme {
                Surface {
                    PanoPokerNav()
                }
            }
        }

        // ✅ Some com status bar e barra de navegação
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController?.hide(WindowInsetsCompat.Type.systemBars())
        insetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
