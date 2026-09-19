package com.example.apptreino.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apptreino.data.relation.TreinoComExercicios
import com.example.apptreino.ui.components.AppCard
import com.example.apptreino.ui.components.PrimaryFab
import com.example.apptreino.ui.components.TopBarSection
import com.example.apptreino.ui.components.UiStateContent
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.BgApp
import com.example.apptreino.ui.theme.Dimens
import com.example.apptreino.ui.theme.TextMuted
import com.example.apptreino.ui.theme.TextPrimary

@Composable
fun HomeTreinosScreen(
    viewModel: HomeTreinosViewModel,
    onTreinoClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarDialogoNovaFicha by remember { mutableStateOf(false) }

    Scaffold(containerColor = BgApp) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TopBarSection(
                title = "Meus Treinos",
                subtitle = "Fichas cadastradas (Treino A, B, C...)"
            )

            UiStateContent(
                state = uiState,
                emptyMessage = "Nenhuma ficha de treino cadastrada ainda.",
                modifier = Modifier.weight(1f)
            ) { fichas ->
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
                    items(fichas, key = { it.treino.treinoId }) { ficha ->
                        FichaTreinoCard(ficha = ficha, onClick = { onTreinoClick(ficha.treino.treinoId) })
                    }
                }
            }

            PrimaryFab(
                label = "+ Nova ficha de treino",
                modifier = Modifier.padding(Dimens.screenPaddingH),
                onClick = { mostrarDialogoNovaFicha = true }
            )
        }
    }

    if (mostrarDialogoNovaFicha) {
        NovaFichaDialog(
            onConfirmar = { nome ->
                viewModel.criarFicha(nome)
                mostrarDialogoNovaFicha = false
            },
            onCancelar = { mostrarDialogoNovaFicha = false }
        )
    }
}

@Composable
private fun FichaTreinoCard(ficha: TreinoComExercicios, onClick: () -> Unit) {
    AppCard(modifier = Modifier.clickable(onClick = onClick)) {
        Text(text = ficha.treino.nome, style = AppTextStyles.cardTitle, color = TextPrimary)
        Text(text = "${ficha.exercicios.size} exercicios", style = AppTextStyles.cardSubtitle, color = TextMuted)
    }
}

@Composable
private fun NovaFichaDialog(onConfirmar: (String) -> Unit, onCancelar: () -> Unit) {
    var nome by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(text = "Nova ficha de treino", style = AppTextStyles.cardTitle) },
        text = {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome da ficha") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(nome) }, enabled = nome.isNotBlank()) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
