package com.example.apptreino.ui.screens.historico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apptreino.ui.components.AppCard
import com.example.apptreino.ui.components.PrimaryFab
import com.example.apptreino.ui.components.SpaceBetweenRow
import com.example.apptreino.ui.components.TopBarSection
import com.example.apptreino.ui.components.UiStateContent
import com.example.apptreino.ui.state.UiState
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.BgApp
import com.example.apptreino.ui.theme.Dimens
import com.example.apptreino.ui.theme.Primary
import com.example.apptreino.ui.theme.TextMuted
import com.example.apptreino.ui.theme.TextPrimary

@Composable
fun HistoricoCargaScreen(viewModel: HistoricoCargaViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val salvarState by viewModel.salvarState.collectAsStateWithLifecycle()

    var cargaKg by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var repeticoes by remember { mutableStateOf("") }

    LaunchedEffect(salvarState) {
        if (salvarState is SalvarState.Success) {
            cargaKg = ""
            series = ""
            repeticoes = ""
        }
    }

    Scaffold(containerColor = BgApp) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val nomeExercicio = (uiState as? UiState.Success<HistoricoCargaUiState>)?.data?.nomeExercicio

            TopBarSection(
                title = nomeExercicio ?: "Historico de carga",
                subtitle = "cargaKg, series, repeticoes por data",
                onBack = onBack
            )

            UiStateContent(
                state = uiState,
                emptyMessage = "Nenhum registro de carga ainda.",
                modifier = Modifier.fillMaxSize()
            ) { dados ->
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
                    item {
                        NovoRegistroForm(
                            cargaKg = cargaKg,
                            onCargaKgChange = { cargaKg = it },
                            series = series,
                            onSeriesChange = { series = it },
                            repeticoes = repeticoes,
                            onRepeticoesChange = { repeticoes = it },
                            salvarState = salvarState,
                            onSalvar = { viewModel.registrarCarga(cargaKg, series, repeticoes) }
                        )
                    }

                    item {
                        Text(
                            text = "Historico (mais recente primeiro)",
                            style = AppTextStyles.highlightLineSmall,
                            color = TextMuted
                        )
                    }

                    if (dados.historico.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhum registro de carga ainda.",
                                style = AppTextStyles.cardSubtitle,
                                color = TextMuted
                            )
                        }
                    } else {
                        items(dados.historico, key = { it.historicoId }) { registro ->
                            HistoricoItemCard(registro = registro)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NovoRegistroForm(
    cargaKg: String,
    onCargaKgChange: (String) -> Unit,
    series: String,
    onSeriesChange: (String) -> Unit,
    repeticoes: String,
    onRepeticoesChange: (String) -> Unit,
    salvarState: SalvarState,
    onSalvar: () -> Unit
) {
    AppCard {
        Text(text = "Novo registro", style = AppTextStyles.highlightLineLarge, color = TextMuted)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.fieldsRowSpacing)
        ) {
            CampoFormulario(
                label = "Carga (kg)",
                valor = cargaKg,
                onValorChange = onCargaKgChange,
                teclado = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
            CampoFormulario(
                label = "Séries",
                valor = series,
                onValorChange = onSeriesChange,
                teclado = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            CampoFormulario(
                label = "Reps",
                valor = repeticoes,
                onValorChange = onRepeticoesChange,
                teclado = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }

        if (salvarState is SalvarState.Error) {
            Text(text = salvarState.mensagem, style = AppTextStyles.cardSubtitle, color = Primary)
        }

        PrimaryFab(
            label = if (salvarState is SalvarState.Loading) "Salvando..." else "Salvar registro",
            onClick = onSalvar
        )
    }
}

@Composable
private fun CampoFormulario(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit,
    teclado: KeyboardType,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, style = AppTextStyles.fieldLabel, color = TextMuted)
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            textStyle = AppTextStyles.fieldValue,
            singleLine = true,
            shape = RoundedCornerShape(Dimens.fieldCornerRadius),
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HistoricoItemCard(registro: CargaHistoricoUiModel) {
    AppCard {
        SpaceBetweenRow(start = {
            Text(text = registro.dataFormatada, style = AppTextStyles.cardSubtitle, color = TextMuted)
        }, end = {
            Text(
                text = "${registro.cargaKg} kg - ${registro.series}x${registro.repeticoes}",
                style = AppTextStyles.highlightLineLarge,
                color = TextPrimary
            )
        })
    }
}
