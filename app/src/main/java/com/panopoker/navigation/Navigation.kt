package com.panopoker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panopoker.ui.auth.LoginScreen
import com.panopoker.ui.auth.RegisterScreen
import com.panopoker.ui.lobby.LobbyScreen
import com.panopoker.ui.mesa.MesaScreen
import com.panopoker.ui.splash.SplashScreen

@Composable
fun PanoPokerNav() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") { // ⬅️ Começa pela splash

        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true } // remove splash do histórico
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("lobby")
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = {
                navController.navigate("login")
            })
        }

        composable("lobby") {
            LobbyScreen(navController = navController)
        }

        composable("mesa/{mesaId}") { backStackEntry ->
            val mesaId = backStackEntry.arguments?.getString("mesaId")?.toIntOrNull()
            if (mesaId != null) {
                MesaScreen(mesaId = mesaId, navController = navController)
            }
        }
    }
}
