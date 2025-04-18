package com.panopoker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.panopoker.navigation.PanoPokerNav
import com.panopoker.ui.theme.PanopokerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PanopokerTheme {
                Surface {
                    PanoPokerNav()
                }
            }
        }
    }
}
