package com.example.apptreino.ui.screens.detalhe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apptreino.data.entity.ExercicioEntity
import com.example.apptreino.data.relation.TreinoComExercicios
import com.example.apptreino.ui.components.AppCard
import com.example.apptreino.ui.components.AppChip
import com.example.apptreino.ui.components.PrimaryFab
import com.example.apptreino.ui.components.SpaceBetweenRow
import com.example.apptreino.ui.components.TopBarSection
import com.example.apptreino.ui.components.UiStateContent
import com.example.apptreino.ui.state.UiState
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.BgApp
import com.example.apptreino.ui.theme.Dimens
import com.example.apptreino.ui.theme.TextMuted
import com.example.apptreino.ui.theme.TextPrimary

@Composable
fun DetalheTreinoScreen(viewModel: DetalheTreinoViewModel, onExercicioClick: (Long) -> Unit, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarDialogoNovoExercicio by remember { mutableStateOf(false) }

    Scaffold(containerColor = BgApp) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val tituloTopBar = (uiState as? UiState.Success<TreinoComExercicios>)
                ?.data?.treino?.nome ?: "Ficha de treino"

            TopBarSection(
                title = tituloTopBar,
                subtitle = "campo: treinoId (FK) = ${viewModel.treinoId}",
                onBack = onBack
            )

            UiStateContent(
                state = uiState,
                emptyMessage = "Nenhum exercicio cadastrado ainda.",
                modifier = Modifier.fillMaxSize()
            ) { treinoComExercicios ->
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
                    items(treinoComExercicios.exercicios, key = { it.exercicioId }) { exercicio ->
                        ExercicioCard(
                            exercicio = exercicio,
                            onClick = { onExercicioClick(exercicio.exercicioId) }
                        )
                    }
                    item {
                        PrimaryFab(label = "+ Adicionar exercicio") {
                            mostrarDialogoNovoExercicio = true
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoNovoExercicio) {
        NovoExercicioDialog(
            onConfirmar = { nome, grupamento ->
                viewModel.adicionarExercicio(nome, grupamento)
                mostrarDialogoNovoExercicio = false
            },
            onCancelar = { mostrarDialogoNovoExercicio = false }
        )
    }
}

@Composable
private fun ExercicioCard(exercicio: ExercicioEntity, onClick: () -> Unit) {
    AppCard(modifier = Modifier.clickable(onClick = onClick)) {
        SpaceBetweenRow(modifier = Modifier.fillMaxWidth(), start = {
            Text(text = exercicio.nome, style = AppTextStyles.cardTitle, color = TextPrimary)
        }, end = {
            AppChip(label = exercicio.grupamentoMuscular, active = false)
        })
        Text(text = "Ordem: ${exercicio.ordem}", style = AppTextStyles.cardSubtitle, color = TextMuted)
    }
}

@Composable
private fun NovoExercicioDialog(onConfirmar: (String, String) -> Unit, onCancelar: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var grupamento by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(text = "Novo exercicio", style = AppTextStyles.cardTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.fieldsRowSpacing)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do exercicio") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = grupamento,
                    onValueChange = { grupamento = it },
                    label = { Text("Grupamento muscular") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(nome, grupamento) },
                enabled = nome.isNotBlank() && grupamento.isNotBlank()
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
