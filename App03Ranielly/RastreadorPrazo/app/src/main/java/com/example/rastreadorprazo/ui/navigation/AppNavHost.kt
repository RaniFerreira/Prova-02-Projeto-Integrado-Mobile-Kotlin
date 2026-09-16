package com.example.rastreadorprazo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rastreadorprazo.ui.AjustesViewModel
import com.example.rastreadorprazo.ui.ObrigacoesViewModel
import com.example.rastreadorprazo.ui.screens.AjustesScreen
import com.example.rastreadorprazo.ui.screens.CalendarioScreen
import com.example.rastreadorprazo.ui.screens.DetalhesObrigacaoScreen
import com.example.rastreadorprazo.ui.screens.ListaObrigacoesScreen
import com.example.rastreadorprazo.ui.screens.NotificacoesScreen
import com.example.rastreadorprazo.ui.screens.NovaObrigacaoScreen

@Composable
fun AppNavHost(pendingObrigacaoId: Long?, onPendingConsumido: () -> Unit) {
    val navController = rememberNavController()
    val viewModel: ObrigacoesViewModel = viewModel()
    val ajustesViewModel: AjustesViewModel = viewModel()

    LaunchedEffect(pendingObrigacaoId) {
        if (pendingObrigacaoId != null) {
            navController.navigate(Rotas.detalhes(pendingObrigacaoId))
            onPendingConsumido()
        }
    }

    NavHost(navController = navController, startDestination = Rotas.LISTA) {
        composable(Rotas.LISTA) {
            ListaObrigacoesScreen(navController, viewModel)
        }
        composable(Rotas.CALENDARIO) {
            CalendarioScreen(navController, viewModel)
        }
        composable(Rotas.NOTIFICACOES) {
            NotificacoesScreen(navController, viewModel)
        }
        composable(Rotas.AJUSTES) {
            AjustesScreen(navController, ajustesViewModel)
        }
        composable(Rotas.FORM_NOVA) {
            NovaObrigacaoScreen(navController, viewModel, obrigacaoId = null)
        }
        composable(
            Rotas.FORM_EDITAR,
            arguments = listOf(navArgument("obrigacaoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("obrigacaoId")
            NovaObrigacaoScreen(navController, viewModel, obrigacaoId = id)
        }
        composable(
            Rotas.DETALHES,
            arguments = listOf(navArgument("obrigacaoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("obrigacaoId") ?: 0L
            DetalhesObrigacaoScreen(navController, viewModel, id)
        }
    }
}
