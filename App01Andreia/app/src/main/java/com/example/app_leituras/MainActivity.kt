package com.example.app_leituras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.app_leituras.ui.navigation.AppNavHost
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import dagger.hilt.android.AndroidEntryPoint

// Repositórios/DAOs/AppDatabase não são mais instanciados manualmente aqui — isso agora é
// responsabilidade do Hilt (ver di/DatabaseModule.kt e di/RepositoryModule.kt); cada tela obtém
// seu próprio ViewModel via hiltViewModel(), então o MainActivity só monta tema + NavHost.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Tema sempre claro: ignora o tema escuro do sistema, seguindo o protótipo do Figma.
            AppLeiturasTheme(darkTheme = false) {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
