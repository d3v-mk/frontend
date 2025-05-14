package com.panopoker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panopoker.ui.auth.LoginScreen
import com.panopoker.ui.auth.RegisterScreen
import com.panopoker.ui.financas.DepositoScreen
import com.panopoker.ui.lobby.LobbyScreen
import com.panopoker.ui.lobby.MesasPokerScreen
import com.panopoker.ui.mesa.MesaScreen
import com.panopoker.ui.splash.SplashScreen
import com.panopoker.ui.lobby.MesasBronzeScreen
import com.panopoker.ui.lobby.MesasOuroScreen
import com.panopoker.ui.lobby.MesasPrataScreen
import com.panopoker.ui.saque.SaqueScreen

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

        composable("lobby") {
            LobbyScreen(navController = navController)
        }

        composable("mesas_poker") {
            MesasPokerScreen(navController)
        }

        composable("mesas_bronze") {
            MesasBronzeScreen(navController)
        }

        composable("mesas_prata") {
            MesasPrataScreen()
        }

        composable("mesas_ouro") {
            MesasOuroScreen()
        }

        composable("deposito") {
            DepositoScreen(navController)
        }

        composable("saque") {
            SaqueScreen()
        }

        composable("mesa/{mesaId}") { backStackEntry ->
            val mesaId = backStackEntry.arguments?.getString("mesaId")?.toIntOrNull()
            if (mesaId != null) {
                MesaScreen(mesaId = mesaId, navController = navController)
            }
        }
    }
}
