package com.example.apptreino.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.apptreino.TreinosApplication
import com.example.apptreino.di.GenericViewModelFactory
import com.example.apptreino.ui.screens.busca.BuscaFiltroScreen
import com.example.apptreino.ui.screens.busca.BuscaFiltroViewModel
import com.example.apptreino.ui.screens.corrida.CorridaScreen
import com.example.apptreino.ui.screens.corrida.CorridaViewModel
import com.example.apptreino.ui.screens.detalhe.DetalheTreinoScreen
import com.example.apptreino.ui.screens.detalhe.DetalheTreinoViewModel
import com.example.apptreino.ui.screens.historico.HistoricoCargaScreen
import com.example.apptreino.ui.screens.historico.HistoricoCargaViewModel
import com.example.apptreino.ui.screens.home.HomeTreinosScreen
import com.example.apptreino.ui.screens.home.HomeTreinosViewModel
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.Primary
import com.example.apptreino.ui.theme.Surface
import com.example.apptreino.ui.theme.TextMuted

private object Rotas {
    const val HOME = "home"
    const val DETALHE = "detalhe/{treinoId}"
    const val HISTORICO = "historico/{exercicioId}"
    const val CORRIDA = "corrida"
    const val BUSCA = "busca"

    fun detalhe(treinoId: Long) = "detalhe/$treinoId"
    fun historico(exercicioId: Long) = "historico/$exercicioId"
}

private data class ItemNavegacao(val rota: String, val label: String, val icone: androidx.compose.ui.graphics.vector.ImageVector)

private val itensPrincipais = listOf(
    ItemNavegacao(Rotas.HOME, "Treinos", Icons.Filled.FitnessCenter),
    ItemNavegacao(Rotas.CORRIDA, "Corridas", Icons.AutoMirrored.Filled.DirectionsRun),
    ItemNavegacao(Rotas.BUSCA, "Buscar", Icons.Filled.Search)
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as TreinosApplication
    val container = application.container

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination

    val mostrarBarraNavegacao = itensPrincipais.any { rotaAtual?.hierarchy?.any { destino -> destino.route == it.rota } == true }

    Scaffold(
        bottomBar = {
            if (mostrarBarraNavegacao) {
                NavigationBar(containerColor = Surface, contentColor = TextMuted) {
                    itensPrincipais.forEach { item ->
                        val selecionado = rotaAtual?.hierarchy?.any { it.route == item.rota } == true
                        NavigationBarItem(
                            selected = selecionado,
                            onClick = {
                                navController.navigate(item.rota) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icone, contentDescription = item.label) },
                            label = { Text(item.label, style = AppTextStyles.chipLabel) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Surface,
                                selectedTextColor = Primary,
                                indicatorColor = Primary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Rotas.HOME,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Rotas.HOME) {
                val viewModel: HomeTreinosViewModel = viewModel(
                    factory = GenericViewModelFactory { HomeTreinosViewModel(container.treinoRepository) }
                )
                HomeTreinosScreen(
                    viewModel = viewModel,
                    onTreinoClick = { treinoId -> navController.navigate(Rotas.detalhe(treinoId)) }
                )
            }

            composable(Rotas.DETALHE) { entry ->
                val treinoId = entry.arguments?.getString("treinoId")?.toLongOrNull() ?: return@composable
                val viewModel: DetalheTreinoViewModel = viewModel(
                    key = "detalhe_$treinoId",
                    factory = GenericViewModelFactory {
                        DetalheTreinoViewModel(container.treinoRepository, container.exercicioRepository, treinoId)
                    }
                )
                DetalheTreinoScreen(
                    viewModel = viewModel,
                    onExercicioClick = { exercicioId -> navController.navigate(Rotas.historico(exercicioId)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Rotas.HISTORICO) { entry ->
                val exercicioId = entry.arguments?.getString("exercicioId")?.toLongOrNull() ?: return@composable
                val viewModel: HistoricoCargaViewModel = viewModel(
                    key = "historico_$exercicioId",
                    factory = GenericViewModelFactory {
                        HistoricoCargaViewModel(container.exercicioRepository, container.cargaHistoricoRepository, exercicioId)
                    }
                )
                HistoricoCargaScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }

            composable(Rotas.CORRIDA) {
                val viewModel: CorridaViewModel = viewModel(
                    factory = GenericViewModelFactory { CorridaViewModel(container.corridaRepository) }
                )
                CorridaScreen(viewModel = viewModel)
            }

            composable(Rotas.BUSCA) {
                val viewModel: BuscaFiltroViewModel = viewModel(
                    factory = GenericViewModelFactory { BuscaFiltroViewModel(container.exercicioRepository) }
                )
                BuscaFiltroScreen(
                    viewModel = viewModel,
                    onExercicioClick = { exercicioId -> navController.navigate(Rotas.historico(exercicioId)) }
                )
            }
        }
    }
}
