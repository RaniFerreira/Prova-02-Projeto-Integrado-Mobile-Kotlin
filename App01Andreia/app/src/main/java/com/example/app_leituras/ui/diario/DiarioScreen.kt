package com.example.app_leituras.ui.diario

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_leituras.domain.model.Nota
import com.example.app_leituras.domain.model.TipoNota
import com.example.app_leituras.ui.components.CardPainel
import com.example.app_leituras.ui.components.ChipSelecionavel
import com.example.app_leituras.ui.components.TagPilula
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import java.util.Calendar

private val MESES_ABREVIADOS = listOf(
    "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiarioScreen(
    viewModel: DiarioViewModel,
    tituloLivro: String,
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiarioContent(
        uiState = uiState,
        tituloLivro = tituloLivro,
        onVoltarClick = onVoltarClick,
        onAbrirNovaNota = viewModel::onAbrirNovaNota,
        onFecharNovaNota = viewModel::onFecharNovaNota,
        onConteudoChange = viewModel::onConteudoChange,
        onTipoChange = viewModel::onTipoChange,
        onSalvarNota = viewModel::onSalvarNota,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiarioContent(
    uiState: DiarioUiState,
    tituloLivro: String,
    onVoltarClick: () -> Unit,
    onAbrirNovaNota: () -> Unit,
    onFecharNovaNota: () -> Unit,
    onConteudoChange: (String) -> Unit,
    onTipoChange: (TipoNota) -> Unit,
    onSalvarNota: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CabecalhoDiario(onVoltarClick = onVoltarClick)
                    TagPilula(texto = tituloLivro)
                }
            }

            if (uiState.notas.isEmpty()) {
                item {
                    Text(
                        text = "Nenhuma nota ainda — toque em \"+\" para registrar a primeira.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            } else {
                items(uiState.notas, key = { it.id }) { nota ->
                    CardNota(nota = nota)
                }
            }

            // Espaço extra no fim da lista para o conteúdo não ficar escondido atrás do FAB.
            item { Box(modifier = Modifier.padding(bottom = 72.dp)) }
        }

        FloatingActionButton(
            onClick = onAbrirNovaNota,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            TextoIconePlus()
        }
    }

    if (uiState.formularioAberto) {
        ModalBottomSheet(
            onDismissRequest = onFecharNovaNota,
            sheetState = rememberModalBottomSheetState()
        ) {
            FormularioNovaNota(
                conteudo = uiState.conteudo,
                tipo = uiState.tipo,
                erro = uiState.erro,
                salvando = uiState.salvando,
                onConteudoChange = onConteudoChange,
                onTipoChange = onTipoChange,
                onSalvarNota = onSalvarNota
            )
        }
    }
}

@Composable
private fun CabecalhoDiario(onVoltarClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "←",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable(onClick = onVoltarClick)
            )
            Text(
                text = "Notas & Citações",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "Diário de leitura",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CardNota(nota: Nota) {
    CardPainel {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatarData(nota.dataHora),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TagPilula(texto = rotuloTipoNota(nota.tipo))
            }
            Text(
                text = nota.conteudo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FormularioNovaNota(
    conteudo: String,
    tipo: TipoNota,
    erro: String?,
    salvando: Boolean,
    onConteudoChange: (String) -> Unit,
    onTipoChange: (TipoNota) -> Unit,
    onSalvarNota: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Nova nota", style = MaterialTheme.typography.headlineSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TipoNota.entries.forEach { opcao ->
                ChipSelecionavel(
                    texto = rotuloTipoNota(opcao),
                    selecionado = tipo == opcao,
                    onClick = { onTipoChange(opcao) }
                )
            }
        }

        OutlinedTextField(
            value = conteudo,
            onValueChange = onConteudoChange,
            label = { Text("O que você quer registrar?") },
            isError = erro != null,
            supportingText = { erro?.let { Text(it) } },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSalvarNota,
            enabled = !salvando,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (salvando) "Salvando..." else "Salvar nota")
        }
    }
}

@Composable
private fun TextoIconePlus() {
    Text(
        text = "+",
        fontSize = 24.sp,
        lineHeight = 24.sp,
        style = LocalTextStyle.current.copy(
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        )
    )
}

private fun rotuloTipoNota(tipo: TipoNota): String = when (tipo) {
    TipoNota.NOTA -> "Anotação"
    TipoNota.INSIGHT -> "Insight"
    TipoNota.CITACAO -> "Citação"
}

private fun formatarData(instante: Long): String {
    val calendario = Calendar.getInstance().apply { timeInMillis = instante }
    val dia = calendario.get(Calendar.DAY_OF_MONTH)
    val mes = MESES_ABREVIADOS[calendario.get(Calendar.MONTH)]
    val ano = calendario.get(Calendar.YEAR)
    return "%02d %s %d".format(dia, mes, ano)
}

@Preview(name = "Light", showBackground = true, heightDp = 1200)
@Preview(name = "Dark", showBackground = true, heightDp = 1200, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DiarioContentPreview() {
    AppLeiturasTheme {
        DiarioContent(
            uiState = DiarioUiState(
                notas = listOf(
                    Nota(
                        id = 4L,
                        livroId = 3L,
                        tipo = TipoNota.CITACAO,
                        conteudo = "\"Código limpo é aquele que foi escrito por alguém que se importa.\"",
                        dataHora = dataDe(2026, Calendar.JUNE, 5)
                    ),
                    Nota(
                        id = 3L,
                        livroId = 3L,
                        tipo = TipoNota.INSIGHT,
                        conteudo = "O princípio SRP aplicado a funções torna o código muito mais testável.",
                        dataHora = dataDe(2026, Calendar.JUNE, 4)
                    ),
                    Nota(
                        id = 2L,
                        livroId = 3L,
                        tipo = TipoNota.NOTA,
                        conteudo = "Revisar capítulo sobre tratamento de erros antes da prova.",
                        dataHora = dataDe(2026, Calendar.JUNE, 3)
                    ),
                    Nota(
                        id = 1L,
                        livroId = 3L,
                        tipo = TipoNota.CITACAO,
                        conteudo = "\"Nomes devem revelar a intenção do programador.\"",
                        dataHora = dataDe(2026, Calendar.JUNE, 2)
                    )
                ),
                carregando = false
            ),
            tituloLivro = "Clean Code",
            onVoltarClick = {},
            onAbrirNovaNota = {},
            onFecharNovaNota = {},
            onConteudoChange = {},
            onTipoChange = {},
            onSalvarNota = {}
        )
    }
}

@Preview(name = "Vazio", showBackground = true, heightDp = 800)
@Composable
private fun DiarioContentVazioPreview() {
    AppLeiturasTheme {
        DiarioContent(
            uiState = DiarioUiState(carregando = false),
            tituloLivro = "Clean Code",
            onVoltarClick = {},
            onAbrirNovaNota = {},
            onFecharNovaNota = {},
            onConteudoChange = {},
            onTipoChange = {},
            onSalvarNota = {}
        )
    }
}

@Preview(name = "Formulário de nova nota aberto", showBackground = true, heightDp = 900)
@Composable
private fun DiarioContentFormularioAbertoPreview() {
    AppLeiturasTheme {
        DiarioContent(
            uiState = DiarioUiState(
                notas = emptyList(),
                formularioAberto = true,
                tipo = TipoNota.INSIGHT,
                carregando = false
            ),
            tituloLivro = "Clean Code",
            onVoltarClick = {},
            onAbrirNovaNota = {},
            onFecharNovaNota = {},
            onConteudoChange = {},
            onTipoChange = {},
            onSalvarNota = {}
        )
    }
}

private fun dataDe(ano: Int, mes: Int, dia: Int): Long =
    Calendar.getInstance().apply { set(ano, mes, dia, 9, 0, 0) }.timeInMillis
