package com.example.rastreadorprazo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rastreadorprazo.ui.FiltroStatus
import com.example.rastreadorprazo.ui.ObrigacoesViewModel
import com.example.rastreadorprazo.ui.components.AppBottomBar
import com.example.rastreadorprazo.ui.components.ObrigacaoCard
import com.example.rastreadorprazo.ui.navigation.Rotas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaObrigacoesScreen(navController: NavController, viewModel: ObrigacoesViewModel) {
    val obrigacoes by viewModel.obrigacoes.collectAsState()
    val filtroAtual by viewModel.filtro.collectAsState()
    val busca by viewModel.busca.collectAsState()
    var mostrarBusca by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Obrigações") },
                actions = {
                    IconButton(onClick = {
                        mostrarBusca = !mostrarBusca
                        if (!mostrarBusca) viewModel.definirBusca("")
                    }) {
                        Icon(
                            if (mostrarBusca) Icons.Filled.Close else Icons.Filled.Search,
                            contentDescription = "Buscar"
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppBottomBar(rotaAtual = Rotas.LISTA, onInicioClick = {})
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Rotas.FORM_NOVA) }) {
                Icon(Icons.Filled.Add, contentDescription = "Nova Obrigação")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (mostrarBusca) {
                OutlinedTextField(
                    value = busca,
                    onValueChange = viewModel::definirBusca,
                    label = { Text("Buscar por título ou favorecido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            SecondaryTabRow(selectedTabIndex = FiltroStatus.entries.indexOf(filtroAtual)) {
                FiltroStatus.entries.forEach { filtro ->
                    Tab(
                        selected = filtro == filtroAtual,
                        onClick = { viewModel.definirFiltro(filtro) },
                        text = { Text(filtro.rotulo) }
                    )
                }
            }

            if (obrigacoes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Nenhuma obrigação encontrada.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(obrigacoes, key = { it.id }) { obrigacao ->
                        ObrigacaoCard(
                            obrigacao = obrigacao,
                            onClick = { navController.navigate(Rotas.detalhes(obrigacao.id)) }
                        )
                    }
                }
            }
        }
    }
}
