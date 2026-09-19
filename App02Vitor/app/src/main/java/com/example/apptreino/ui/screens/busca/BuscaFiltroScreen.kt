package com.example.apptreino.ui.screens.busca

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apptreino.data.entity.ExercicioEntity
import com.example.apptreino.ui.components.AppCard
import com.example.apptreino.ui.components.AppChip
import com.example.apptreino.ui.components.SpaceBetweenRow
import com.example.apptreino.ui.components.TopBarSection
import com.example.apptreino.ui.components.UiStateContent
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.BgApp
import com.example.apptreino.ui.theme.ChipBg
import com.example.apptreino.ui.theme.Dimens
import com.example.apptreino.ui.theme.TextMuted
import com.example.apptreino.ui.theme.TextPrimary

@Composable
fun BuscaFiltroScreen(viewModel: BuscaFiltroViewModel, onExercicioClick: (Long) -> Unit) {
    val termoBusca by viewModel.termoBusca.collectAsStateWithLifecycle()
    val grupamentoSelecionado by viewModel.grupamentoSelecionado.collectAsStateWithLifecycle()
    val grupamentosDisponiveis by viewModel.grupamentosDisponiveis.collectAsStateWithLifecycle()
    val resultado by viewModel.resultado.collectAsStateWithLifecycle()

    Scaffold(containerColor = BgApp) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TopBarSection(
                title = "Buscar exercicios",
                subtitle = "campo texto + filtro por grupamentoMuscular"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimens.screenPaddingH,
                        end = Dimens.screenPaddingH,
                        top = Dimens.contentPaddingTop
                    ),
                verticalArrangement = Arrangement.spacedBy(Dimens.contentItemSpacing)
            ) {
                OutlinedTextField(
                    value = termoBusca,
                    onValueChange = viewModel::onTermoBuscaChange,
                    placeholder = { Text("Buscar exercicio...") },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.searchBarCornerRadius),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = ChipBg,
                        focusedContainerColor = ChipBg,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.chipPaddingH)) {
                    item {
                        AppChip(
                            label = "Todos",
                            active = grupamentoSelecionado == GRUPAMENTO_TODOS,
                            onClick = { viewModel.onGrupamentoSelecionado(GRUPAMENTO_TODOS) }
                        )
                    }
                    items(grupamentosDisponiveis) { grupamento ->
                        AppChip(
                            label = grupamento,
                            active = grupamentoSelecionado == grupamento,
                            onClick = { viewModel.onGrupamentoSelecionado(grupamento) }
                        )
                    }
                }
            }

            UiStateContent(
                state = resultado,
                emptyMessage = "Nenhum exercicio encontrado.",
                modifier = Modifier.fillMaxSize()
            ) { exercicios ->
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
                    items(exercicios, key = { it.exercicioId }) { exercicio ->
                        ExercicioResultadoCard(
                            exercicio = exercicio,
                            onClick = { onExercicioClick(exercicio.exercicioId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExercicioResultadoCard(exercicio: ExercicioEntity, onClick: () -> Unit) {
    AppCard(modifier = Modifier.clickable(onClick = onClick)) {
        SpaceBetweenRow(modifier = Modifier.fillMaxWidth(), start = {
            Text(text = exercicio.nome, style = AppTextStyles.cardTitle, color = TextPrimary)
        }, end = {
            AppChip(label = exercicio.grupamentoMuscular, active = false)
        })
    }
}
