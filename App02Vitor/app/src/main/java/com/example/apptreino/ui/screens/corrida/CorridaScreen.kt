package com.example.apptreino.ui.screens.corrida

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apptreino.ui.components.AppCard
import com.example.apptreino.ui.components.PrimaryFab
import com.example.apptreino.ui.components.SpaceBetweenRow
import com.example.apptreino.ui.components.TopBarSection
import com.example.apptreino.ui.components.UiStateContent
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.BgApp
import com.example.apptreino.ui.theme.Dimens
import com.example.apptreino.ui.theme.Primary
import com.example.apptreino.ui.theme.Success
import com.example.apptreino.ui.theme.TextMuted

@Composable
fun CorridaScreen(viewModel: CorridaViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarDialogoNovaCorrida by remember { mutableStateOf(false) }

    Scaffold(containerColor = BgApp) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TopBarSection(
                title = "Corridas",
                subtitle = "distanciaKm, tempoTotalSegundos, pace calculado"
            )

            UiStateContent(
                state = uiState,
                emptyMessage = "Nenhuma corrida registrada ainda.",
                modifier = Modifier.weight(1f)
            ) { corridas ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Dimens.screenPaddingH,
                        end = Dimens.screenPaddingH,
                        top = Dimens.contentPaddingTop,
                        bottom = Dimens.contentPaddingBottom
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.contentItemSpacing)
                ) {
                    items(corridas, key = { it.corridaId }) { corrida ->
                        CorridaCard(corrida = corrida)
                    }
                }
            }

            PrimaryFab(
                label = "+ Registrar corrida",
                modifier = Modifier.padding(Dimens.screenPaddingH),
                onClick = { mostrarDialogoNovaCorrida = true }
            )
        }
    }

    if (mostrarDialogoNovaCorrida) {
        NovaCorridaDialog(
            onConfirmar = { distanciaKm, tempoTotalSegundos ->
                viewModel.registrarCorrida(distanciaKm, tempoTotalSegundos)
                mostrarDialogoNovaCorrida = false
            },
            onCancelar = { mostrarDialogoNovaCorrida = false }
        )
    }
}

@Composable
private fun CorridaCard(corrida: CorridaUiModel) {
    AppCard {
        SpaceBetweenRow(modifier = Modifier.fillMaxWidth(), start = {
            Text(text = corrida.dataFormatada, style = AppTextStyles.cardSubtitle, color = TextMuted)
        }, end = {
            Text(text = corrida.distanciaFormatada, style = AppTextStyles.highlightValue, color = Primary)
        })
        SpaceBetweenRow(modifier = Modifier.fillMaxWidth(), start = {
            Text(text = corrida.tempoFormatado, style = AppTextStyles.cardSubtitle, color = TextMuted)
        }, end = {
            Text(text = corrida.paceFormatado, style = AppTextStyles.highlightLineSmall, color = Success)
        })
    }
}

@Composable
private fun NovaCorridaDialog(onConfirmar: (Double, Int) -> Unit, onCancelar: () -> Unit) {
    var distancia by remember { mutableStateOf("") }
    var tempoMinutos by remember { mutableStateOf("") }

    val distanciaKm = distancia.replace(',', '.').toDoubleOrNull()
    val tempoTotalSegundos = tempoMinutos.replace(',', '.').toDoubleOrNull()?.let { (it * 60).toInt() }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(text = "Registrar corrida", style = AppTextStyles.cardTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.fieldsRowSpacing)) {
                OutlinedTextField(
                    value = distancia,
                    onValueChange = { distancia = it },
                    label = { Text("Distância (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = tempoMinutos,
                    onValueChange = { tempoMinutos = it },
                    label = { Text("Tempo total (min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(distanciaKm!!, tempoTotalSegundos!!) },
                enabled = distanciaKm != null && distanciaKm > 0 && tempoTotalSegundos != null && tempoTotalSegundos > 0
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
