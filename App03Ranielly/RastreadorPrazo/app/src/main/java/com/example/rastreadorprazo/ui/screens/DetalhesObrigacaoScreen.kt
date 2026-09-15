package com.example.rastreadorprazo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.rastreadorprazo.data.StatusObrigacao
import com.example.rastreadorprazo.data.TipoObrigacao
import com.example.rastreadorprazo.ui.ObrigacoesViewModel
import com.example.rastreadorprazo.ui.components.StatusChip
import com.example.rastreadorprazo.ui.navigation.Rotas
import com.example.rastreadorprazo.util.formatarData
import com.example.rastreadorprazo.util.formatarDataHora
import com.example.rastreadorprazo.util.formatarHora
import com.example.rastreadorprazo.util.formatarMoeda
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesObrigacaoScreen(
    navController: NavController,
    viewModel: ObrigacoesViewModel,
    obrigacaoId: Long
) {
    val obrigacao by viewModel.obterPorId(obrigacaoId).collectAsState(initial = null)
    var mostrarConfirmacaoExclusao by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Rotas.formEditar(obrigacaoId)) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                }
            )
        }
    ) { innerPadding ->
        val atual = obrigacao
        if (atual == null) {
            Box(
                Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Obrigação não encontrada.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(atual.titulo, style = MaterialTheme.typography.headlineSmall)
                            StatusChip(atual.status)
                        }
                        Text(
                            "Favorecido: ${atual.favorecido}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text("Valor a Pagar", style = MaterialTheme.typography.labelMedium)
                        Text(formatarMoeda(atual.valor), style = MaterialTheme.typography.headlineMedium)

                        val dias = ChronoUnit.DAYS.between(LocalDate.now(), atual.dataVencimento)
                        val textoPrazo = when {
                            dias > 0 -> "Vence em $dias dia${if (dias > 1) "s" else ""} (${formatarData(atual.dataVencimento)})"
                            dias == 0L -> "Vence hoje (${formatarData(atual.dataVencimento)})"
                            else -> "Venceu há ${-dias} dia${if (-dias > 1) "s" else ""} (${formatarData(atual.dataVencimento)})"
                        }
                        Text(textoPrazo, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("INFORMAÇÕES DA OBRIGAÇÃO", style = MaterialTheme.typography.labelMedium)
                        LinhaInfo(
                            "Tipo de Obrigação",
                            if (atual.tipo == TipoObrigacao.FATURA) "Fatura / Cobrança" else "Prazo / Entrega"
                        )
                        LinhaInfo("Vencimento original", formatarData(atual.dataVencimento))
                        LinhaInfo(
                            "Lembrete",
                            if (atual.lembreteAtivo) "Ativo às ${formatarHora(atual.horaLembrete)}" else "Desativado"
                        )
                        LinhaInfo("Data de Criação", formatarDataHora(atual.dataCriacao))
                    }
                }

                if (atual.status == StatusObrigacao.PENDENTE) {
                    Button(
                        onClick = { viewModel.marcarComoPaga(atual) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Marcar como Paga")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate(Rotas.formEditar(obrigacaoId)) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Editar")
                    }
                    OutlinedButton(
                        onClick = { viewModel.cancelarObrigacao(atual) },
                        enabled = atual.status == StatusObrigacao.PENDENTE,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }

                TextButton(
                    onClick = { mostrarConfirmacaoExclusao = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir Obrigação")
                }
            }

            if (mostrarConfirmacaoExclusao) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacaoExclusao = false },
                    title = { Text("Excluir obrigação?") },
                    text = { Text("Esta ação não pode ser desfeita.") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.excluir(atual)
                            mostrarConfirmacaoExclusao = false
                            navController.popBackStack()
                        }) { Text("Excluir") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarConfirmacaoExclusao = false }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}

@Composable
private fun LinhaInfo(rotulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(rotulo, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}
