package com.example.app_leituras

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_leituras.data.repository.fake.FakeLivroRepository
import com.example.app_leituras.domain.repository.LivroRepository
import com.example.app_leituras.ui.dashboard.DashboardScreen
import com.example.app_leituras.ui.dashboard.DashboardViewModel
import com.example.app_leituras.ui.dashboard.DashboardViewModelFactory
import com.example.app_leituras.ui.theme.AppLeiturasTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    // TODO: substituir por injeção via Hilt/Koin quando o DI for configurado.
    private val livroRepository: LivroRepository = FakeLivroRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Tema sempre claro: ignora o tema escuro do sistema, seguindo o protótipo do Figma.
            AppLeiturasTheme(darkTheme = false) {
                AppLeituras(livroRepository = livroRepository)
            }
        }
    }
}

@Composable
private fun AppLeituras(livroRepository: LivroRepository) {
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(livroRepository)
    )
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        DashboardScreen(
            viewModel = dashboardViewModel,
            modifier = Modifier.padding(innerPadding),
            onLivroClick = { livroId -> Log.d(TAG, "Livro clicado: $livroId") },
            // TODO: navegar para a tela de busca (Buscar Livro, node 145:33 no Figma) quando a
            // navegação for configurada; o fluxo lá permite buscar e depois cadastrar o livro.
            onAdicionarLivroClick = { Log.d(TAG, "Adicionar livro clicado") }
        )
    }
}
