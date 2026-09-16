package com.example.rastreadorprazo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rastreadorprazo.ui.ObrigacoesViewModel
import com.example.rastreadorprazo.ui.components.AppBottomBar
import com.example.rastreadorprazo.ui.components.ObrigacaoCard
import com.example.rastreadorprazo.ui.navigation.Rotas
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val diasSemana = listOf("D", "S", "T", "Q", "Q", "S", "S")
private val localePtBr = Locale.forLanguageTag("pt-BR")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(navController: NavController, viewModel: ObrigacoesViewModel) {
    val obrigacoes by viewModel.todasObrigacoes.collectAsState()
    var mesAtual by remember { mutableStateOf(YearMonth.now()) }
    var dataSelecionada by remember { mutableStateOf(LocalDate.now()) }

    val obrigacoesPorDia = remember(obrigacoes) { obrigacoes.groupBy { it.dataVencimento } }
    val obrigacoesDoDia = obrigacoesPorDia[dataSelecionada].orEmpty()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Calendário") }) },
        bottomBar = { AppBottomBar(navController = navController, rotaAtual = Rotas.CALENDARIO) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { mesAtual = mesAtual.minusMonths(1) }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Mês anterior")
                }
                Text(
                    text = mesAtual.month.getDisplayName(TextStyle.FULL, localePtBr)
                        .replaceFirstChar { it.uppercase() } + " ${mesAtual.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { mesAtual = mesAtual.plusMonths(1) }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Próximo mês")
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                diasSemana.forEach { dia ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            dia,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            val primeiroDia = mesAtual.atDay(1)
            val deslocamento = primeiroDia.dayOfWeek.value % 7
            val totalDias = mesAtual.lengthOfMonth()
            val celulas: List<LocalDate?> = List(deslocamento) { null } + (1..totalDias).map { mesAtual.atDay(it) }
            val linhas = celulas.chunked(7)

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                linhas.forEach { linha ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        linha.forEach { dia ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dia != null) {
                                    val selecionado = dia == dataSelecionada
                                    val temObrigacao = obrigacoesPorDia.containsKey(dia)
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                if (selecionado) MaterialTheme.colorScheme.primary
                                                else Color.Transparent
                                            )
                                            .clickable { dataSelecionada = dia },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            dia.dayOfMonth.toString(),
                                            color = if (selecionado) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (temObrigacao) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (selecionado) MaterialTheme.colorScheme.onPrimary
                                                        else MaterialTheme.colorScheme.primary
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Obrigações em ${dataSelecionada.dayOfMonth}/${dataSelecionada.monthValue}/${dataSelecionada.year}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (obrigacoesDoDia.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Nenhuma obrigação nesta data.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(obrigacoesDoDia, key = { it.id }) { obrigacao ->
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
