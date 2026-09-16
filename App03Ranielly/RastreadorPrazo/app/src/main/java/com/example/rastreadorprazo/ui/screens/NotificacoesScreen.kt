package com.example.rastreadorprazo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rastreadorprazo.data.Obrigacao
import com.example.rastreadorprazo.data.StatusObrigacao
import com.example.rastreadorprazo.ui.ObrigacoesViewModel
import com.example.rastreadorprazo.ui.components.AppBottomBar
import com.example.rastreadorprazo.ui.navigation.Rotas
import com.example.rastreadorprazo.util.formatarData
import com.example.rastreadorprazo.util.formatarHora
import com.example.rastreadorprazo.util.formatarMoeda
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacoesScreen(navController: NavController, viewModel: ObrigacoesViewModel) {
    val obrigacoes by viewModel.todasObrigacoes.collectAsState()
    val lembretes = remember(obrigacoes) {
        obrigacoes
            .filter { it.status == StatusObrigacao.PENDENTE && it.lembreteAtivo }
            .sortedWith(compareBy({ it.dataVencimento }, { it.horaLembrete }))
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Notificações") }) },
        bottomBar = { AppBottomBar(navController = navController, rotaAtual = Rotas.NOTIFICACOES) }
    ) { innerPadding ->
        if (lembretes.isEmpty()) {
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Nenhum lembrete agendado.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(lembretes, key = { it.id }) { obrigacao ->
                    NotificacaoCard(
                        obrigacao = obrigacao,
                        onVerDetalhes = { navController.navigate(Rotas.detalhes(obrigacao.id)) },
                        onMarcarPaga = { viewModel.marcarComoPaga(obrigacao) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificacaoCard(
    obrigacao: Obrigacao,
    onVerDetalhes: () -> Unit,
    onMarcarPaga: () -> Unit
) {
    val dias = ChronoUnit.DAYS.between(LocalDate.now(), obrigacao.dataVencimento)
    val quando = when {
        dias > 0 -> "Vence em $dias dia${if (dias > 1) "s" else ""}"
        dias == 0L -> "Vence hoje"
        else -> "Venceu há ${-dias} dia${if (-dias > 1) "s" else ""}"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Lembrete de Vencimento",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            formatarHora(obrigacao.horaLembrete),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "O item ${obrigacao.titulo} vence em ${formatarData(obrigacao.dataVencimento)}. " +
                            "Valor a pagar: ${formatarMoeda(obrigacao.valor)}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        quando,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onVerDetalhes, modifier = Modifier.weight(1f)) {
                    Text("Ver Detalhes")
                }
                Button(onClick = onMarcarPaga, modifier = Modifier.weight(1f)) {
                    Text("Marcar como Paga")
                }
            }
        }
    }
}
