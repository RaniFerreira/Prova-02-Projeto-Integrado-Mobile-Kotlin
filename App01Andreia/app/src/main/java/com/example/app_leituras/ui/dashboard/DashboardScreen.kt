package com.example.app_leituras.ui.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.ui.components.CardLivro
import com.example.app_leituras.ui.theme.AppLeiturasTheme

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
    onLivroClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(
        uiState = uiState,
        onLivroClick = onLivroClick,
        modifier = modifier
    )
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onLivroClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SecaoLivros(titulo = "Em andamento", livros = uiState.emAndamento, onLivroClick = onLivroClick)
        SecaoLivros(titulo = "Quero Ler", livros = uiState.queroLer, onLivroClick = onLivroClick)
        SecaoLivros(titulo = "Lido", livros = uiState.lido, onLivroClick = onLivroClick)
    }
}

@Composable
private fun SecaoLivros(
    titulo: String,
    livros: List<Livro>,
    onLivroClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (livros.isEmpty()) {
            Text(
                text = "Nenhum livro por aqui ainda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(livros, key = { it.id }) { livro ->
                    CardLivro(
                        titulo = livro.titulo,
                        autor = livro.autor,
                        capaUrl = livro.capaUrl,
                        status = livro.status,
                        paginaAtual = livro.paginaAtual,
                        totalPaginas = livro.totalPaginas,
                        onClick = { onLivroClick(livro.id) }
                    )
                }
            }
        }
    }
}

private val livrosMock = listOf(
    Livro(
        id = 1L,
        titulo = "1984",
        autor = "George Orwell",
        totalPaginas = 328,
        genero = "Ficção",
        capaUrl = null,
        status = StatusLeitura.LENDO,
        paginaAtual = 120,
        googleBooksId = null,
        dataCriacao = 0L
    ),
    Livro(
        id = 2L,
        titulo = "Duna",
        autor = "Frank Herbert",
        totalPaginas = 688,
        genero = "Ficção Científica",
        capaUrl = null,
        status = StatusLeitura.QUERO_LER,
        paginaAtual = 0,
        googleBooksId = null,
        dataCriacao = 0L
    ),
    Livro(
        id = 3L,
        titulo = "O Hobbit",
        autor = "J.R.R. Tolkien",
        totalPaginas = 310,
        genero = "Fantasia",
        capaUrl = null,
        status = StatusLeitura.LIDO,
        paginaAtual = 310,
        googleBooksId = null,
        dataCriacao = 0L
    )
)

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DashboardContentPreview() {
    AppLeiturasTheme {
        DashboardContent(
            uiState = DashboardUiState(
                emAndamento = livrosMock.filter { it.status == StatusLeitura.LENDO },
                queroLer = livrosMock.filter { it.status == StatusLeitura.QUERO_LER },
                lido = livrosMock.filter { it.status == StatusLeitura.LIDO }
            ),
            onLivroClick = {}
        )
    }
}

@Preview(name = "Vazio", showBackground = true)
@Composable
private fun DashboardContentVazioPreview() {
    AppLeiturasTheme {
        DashboardContent(
            uiState = DashboardUiState(),
            onLivroClick = {}
        )
    }
}
