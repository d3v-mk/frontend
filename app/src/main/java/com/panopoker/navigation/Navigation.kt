package com.panopoker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panopoker.ui.auth.LoginScreen
import com.panopoker.ui.auth.RegisterScreen
import com.panopoker.ui.lobby.LobbyScreen
import com.panopoker.ui.lobby.MesasPokerScreen
import com.panopoker.ui.mesa.MesaScreen
import com.panopoker.ui.splash.SplashScreen
import com.panopoker.ui.perfil.PerfilScreen
import com.panopoker.ui.financas.PromotoresScreen


@Composable
fun PanoPokerNav() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") { // ⬅️ Começa pela splash

        composable("splash") {
            SplashScreen { route ->
                navController.navigate(route) {
                    popUpTo("splash") { inclusive = true }
                }
            }
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

        composable("perfil") {
            PerfilScreen(navController = navController)
        }

        composable("lobby") {
            LobbyScreen(navController = navController)
        }

        composable("mesas_poker") {
            MesasPokerScreen(navController)
        }

        composable("promotores") {
            PromotoresScreen(navController = navController)
        }


        composable("mesa/{mesaId}") { backStackEntry ->
            val mesaId = backStackEntry.arguments?.getString("mesaId")?.toIntOrNull()
            if (mesaId != null) {
                MesaScreen(mesaId = mesaId, navController = navController)
            }
        }
    }
}
