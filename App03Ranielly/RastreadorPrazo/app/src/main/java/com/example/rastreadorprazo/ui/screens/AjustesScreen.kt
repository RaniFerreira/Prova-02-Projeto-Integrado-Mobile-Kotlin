package com.example.rastreadorprazo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rastreadorprazo.data.TemaModo
import com.example.rastreadorprazo.ui.AjustesViewModel
import com.example.rastreadorprazo.ui.components.AppBottomBar
import com.example.rastreadorprazo.ui.navigation.Rotas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(navController: NavController, viewModel: AjustesViewModel) {
    val temaModo by viewModel.temaModo.collectAsState()
    val notificacoesAtivas by viewModel.notificacoesAtivas.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes") }) },
        bottomBar = { AppBottomBar(navController = navController, rotaAtual = Rotas.AJUSTES) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Aparência", style = MaterialTheme.typography.labelLarge)
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = temaModo == TemaModo.SISTEMA,
                            onClick = { viewModel.definirTemaModo(TemaModo.SISTEMA) },
                            shape = SegmentedButtonDefaults.itemShape(0, 3)
                        ) { Text("Sistema") }
                        SegmentedButton(
                            selected = temaModo == TemaModo.CLARO,
                            onClick = { viewModel.definirTemaModo(TemaModo.CLARO) },
                            shape = SegmentedButtonDefaults.itemShape(1, 3)
                        ) { Text("Claro") }
                        SegmentedButton(
                            selected = temaModo == TemaModo.ESCURO,
                            onClick = { viewModel.definirTemaModo(TemaModo.ESCURO) },
                            shape = SegmentedButtonDefaults.itemShape(2, 3)
                        ) { Text("Escuro") }
                    }
                }
            }

            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Lembretes de vencimento", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Ativa ou desativa todas as notificações de lembrete",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = notificacoesAtivas,
                        onCheckedChange = { viewModel.definirNotificacoesAtivas(it) }
                    )
                }
            }

            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Sobre", style = MaterialTheme.typography.labelLarge)
                    Text("Rastreador de Prazos", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Versão 1.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
