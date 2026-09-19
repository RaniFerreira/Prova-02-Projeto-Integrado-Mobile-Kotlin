package com.example.app_leituras.ui.novolivro

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.ui.components.ChipSelecionavel
import com.example.app_leituras.ui.components.rotuloStatus
import com.example.app_leituras.ui.theme.AppLeiturasTheme

private val GENEROS_DISPONIVEIS = listOf("Tecnologia", "Ficção", "Ciência", "Negócios", "Outros")

@Composable
fun NovoLivroScreen(
    viewModel: NovoLivroViewModel,
    modifier: Modifier = Modifier,
    onLivroSalvo: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.livroSalvoId) {
        uiState.livroSalvoId?.let(onLivroSalvo)
    }

    NovoLivroContent(
        uiState = uiState,
        onTituloChange = viewModel::onTituloChange,
        onAutorChange = viewModel::onAutorChange,
        onTotalPaginasChange = viewModel::onTotalPaginasChange,
        onGeneroChange = viewModel::onGeneroChange,
        onStatusChange = viewModel::onStatusChange,
        onSalvarClick = viewModel::onSalvarClick,
        modifier = modifier
    )
}

@Composable
private fun NovoLivroContent(
    uiState: NovoLivroUiState,
    onTituloChange: (String) -> Unit,
    onAutorChange: (String) -> Unit,
    onTotalPaginasChange: (String) -> Unit,
    onGeneroChange: (String) -> Unit,
    onStatusChange: (StatusLeitura) -> Unit,
    onSalvarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Cadastro completo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Novo Livro",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SecaoCapa(capaUrl = uiState.capaUrl)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = onTituloChange,
                label = { Text("Título") },
                isError = CampoNovoLivro.TITULO in uiState.camposInvalidos,
                supportingText = {
                    if (CampoNovoLivro.TITULO in uiState.camposInvalidos) {
                        Text("Informe o título do livro")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.autor,
                onValueChange = onAutorChange,
                label = { Text("Autor") },
                isError = CampoNovoLivro.AUTOR in uiState.camposInvalidos,
                supportingText = {
                    if (CampoNovoLivro.AUTOR in uiState.camposInvalidos) {
                        Text("Informe o autor do livro")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.totalPaginas,
                onValueChange = { novoValor -> if (novoValor.all { it.isDigit() }) onTotalPaginasChange(novoValor) },
                label = { Text("Total de páginas") },
                isError = CampoNovoLivro.TOTAL_PAGINAS in uiState.camposInvalidos,
                supportingText = {
                    if (CampoNovoLivro.TOTAL_PAGINAS in uiState.camposInvalidos) {
                        Text("Informe um número de páginas maior que zero")
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Gênero", style = MaterialTheme.typography.titleMedium)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GENEROS_DISPONIVEIS.chunked(3).forEach { linha ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        linha.forEach { genero ->
                            ChipSelecionavel(
                                texto = genero,
                                selecionado = uiState.genero == genero,
                                onClick = { onGeneroChange(genero) }
                            )
                        }
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Status", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusLeitura.entries.forEach { status ->
                    ChipSelecionavel(
                        texto = rotuloStatus(status),
                        selecionado = uiState.status == status,
                        onClick = { onStatusChange(status) }
                    )
                }
            }
        }

        Button(
            onClick = onSalvarClick,
            enabled = !uiState.salvando,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(text = if (uiState.salvando) "Salvando..." else "Salvar")
        }
    }
}

// Placeholder de capa (upload de imagem ainda não integrado — ver TODO em CardLivro/IconeLivro).
@Composable
private fun SecaoCapa(capaUrl: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Capa do livro", style = MaterialTheme.typography.titleMedium)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "📷", fontSize = 32.sp)
                Text(
                    text = if (capaUrl != null) "Capa selecionada" else "Adicionar capa",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true, heightDp = 1400)
@Preview(name = "Dark", showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NovoLivroContentPreview() {
    AppLeiturasTheme {
        NovoLivroContent(
            uiState = NovoLivroUiState(titulo = "Duna", autor = "Frank Herbert", genero = "Ficção Científica"),
            onTituloChange = {},
            onAutorChange = {},
            onTotalPaginasChange = {},
            onGeneroChange = {},
            onStatusChange = {},
            onSalvarClick = {}
        )
    }
}

@Preview(name = "Com erros de validação", showBackground = true, heightDp = 1400)
@Composable
private fun NovoLivroContentErroPreview() {
    AppLeiturasTheme {
        NovoLivroContent(
            uiState = NovoLivroUiState(
                camposInvalidos = setOf(
                    CampoNovoLivro.TITULO,
                    CampoNovoLivro.AUTOR,
                    CampoNovoLivro.TOTAL_PAGINAS
                )
            ),
            onTituloChange = {},
            onAutorChange = {},
            onTotalPaginasChange = {},
            onGeneroChange = {},
            onStatusChange = {},
            onSalvarClick = {}
        )
    }
}
