package com.example.app_leituras.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.app_leituras.ui.bookdetail.BookDetailScreen
import com.example.app_leituras.ui.buscarlivro.BuscarLivroScreen
import com.example.app_leituras.ui.dashboard.DashboardScreen
import com.example.app_leituras.ui.definirmeta.DefinirMetaScreen
import com.example.app_leituras.ui.diario.DiarioScreen
import com.example.app_leituras.ui.novolivro.NovoLivroScreen
import com.example.app_leituras.ui.sessaoleitura.SessaoLeituraScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onLivroClick = { livroId -> navController.navigate(bookDetailRoute(livroId)) },
                onAdicionarLivroClick = { navController.navigate(Screen.BuscarLivro.route) }
            )
        }

        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(navArgument("livroId") { type = NavType.LongType })
        ) { backStackEntry ->
            val livroId = backStackEntry.arguments?.getLong("livroId") ?: 0L
            BookDetailScreen(
                onVoltarClick = { navController.popBackStack() },
                onClicarLerAgora = { navController.navigate(sessaoLeituraRoute(livroId)) },
                onClicarMeta = { navController.navigate(definirMetaRoute(livroId)) },
                onClicarNotas = { navController.navigate(diarioNotasRoute(livroId)) }
            )
        }

        composable(
            route = Screen.SessaoLeitura.route,
            arguments = listOf(navArgument("livroId") { type = NavType.LongType })
        ) {
            SessaoLeituraScreen(
                onVoltarClick = { navController.popBackStack() },
                onSessaoFinalizada = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DefinirMeta.route,
            arguments = listOf(navArgument("livroId") { type = NavType.LongType })
        ) {
            DefinirMetaScreen(
                onVoltarClick = { navController.popBackStack() },
                onMetaSalva = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DiarioNotas.route,
            arguments = listOf(navArgument("livroId") { type = NavType.LongType })
        ) {
            DiarioScreen(
                onVoltarClick = { navController.popBackStack() }
            )
        }

        composable(Screen.BuscarLivro.route) {
            BuscarLivroScreen(
                onVoltarClick = { navController.popBackStack() },
                onResultadoSelecionado = { livroBusca -> navController.navigate(novoLivroRoute(livroBusca)) },
                onCadastrarManualmente = { navController.navigate(novoLivroRoute()) }
            )
        }

        composable(
            route = Screen.NovoLivro.route,
            arguments = listOf(
                navArgument("googleBooksId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("titulo") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("autor") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("totalPaginas") { type = NavType.IntType; defaultValue = -1 },
                navArgument("genero") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("capaUrl") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            NovoLivroScreen(
                onLivroSalvo = { livroId ->
                    // Remove Buscar Livro e Novo Livro (e qualquer Book Detail anterior) da pilha,
                    // voltando pro Dashboard e já empilhando o Book Detail do livro recém-criado.
                    navController.navigate(bookDetailRoute(livroId)) {
                        popUpTo(Screen.Dashboard.route) { inclusive = false }
                    }
                }
            )
        }
    }
}
