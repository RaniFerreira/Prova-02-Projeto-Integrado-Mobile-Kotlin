package com.example.app_leituras.ui.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.ui.components.CardLivro
import com.example.app_leituras.ui.components.ChipSelecionavel
import com.example.app_leituras.ui.components.ItemLivroLista
import com.example.app_leituras.ui.components.rotuloStatus
import com.example.app_leituras.ui.theme.AppLeiturasTheme

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onLivroClick: (Long) -> Unit = {},
    onAdicionarLivroClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(
        uiState = uiState,
        onLivroClick = onLivroClick,
        onAdicionarLivroClick = onAdicionarLivroClick,
        onGeneroSelecionado = viewModel::onGeneroSelecionado,
        onStatusSelecionado = viewModel::onStatusSelecionado,
        onLimparFiltros = viewModel::onLimparFiltros,
        modifier = modifier
    )
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onLivroClick: (Long) -> Unit,
    onAdicionarLivroClick: () -> Unit,
    onGeneroSelecionado: (String?) -> Unit,
    onStatusSelecionado: (StatusLeitura?) -> Unit,
    onLimparFiltros: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        CabecalhoTela()
        FiltrosDashboard(
            filtro = uiState.filtro,
            generosDisponiveis = uiState.generosDisponiveis,
            onGeneroSelecionado = onGeneroSelecionado,
            onStatusSelecionado = onStatusSelecionado,
            onLimparFiltros = onLimparFiltros
        )
        SecaoEmAndamento(livros = uiState.emAndamento, onLivroClick = onLivroClick)
        SecaoListaLivros(
            titulo = "Quero Ler",
            livros = uiState.queroLer,
            concluido = false,
            onLivroClick = onLivroClick,
            onAdicionarClick = onAdicionarLivroClick
        )
        SecaoListaLivros(
            titulo = "Lido",
            livros = uiState.lido,
            concluido = true,
            onLivroClick = onLivroClick
        )
    }
}

// Cabeçalho da tela ("Olá, boa leitura!" / "Início"), conforme o Header do Figma.
@Composable
private fun CabecalhoTela() {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "Olá, boa leitura!",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Início",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Fileira de chips de filtro (status + gênero), logo abaixo do título "Início", conforme o
// Figma. É de seleção única: "Todos" limpa tudo, e escolher um status ou gênero limpa o outro
// eixo do filtro (ver comentário em DashboardViewModel.onGeneroSelecionado/onStatusSelecionado).
@Composable
private fun FiltrosDashboard(
    filtro: FiltroState,
    generosDisponiveis: List<String>,
    onGeneroSelecionado: (String?) -> Unit,
    onStatusSelecionado: (StatusLeitura?) -> Unit,
    onLimparFiltros: () -> Unit,
    modifier: Modifier = Modifier
) {
    val semFiltro = filtro.generoSelecionado == null && filtro.statusSelecionado == null

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ChipSelecionavel(texto = "Todos", selecionado = semFiltro, onClick = onLimparFiltros)
        }
        items(StatusLeitura.entries) { status ->
            ChipSelecionavel(
                texto = rotuloStatus(status),
                selecionado = filtro.statusSelecionado == status,
                onClick = {
                    if (filtro.statusSelecionado == status) onLimparFiltros() else onStatusSelecionado(status)
                }
            )
        }
        items(generosDisponiveis) { genero ->
            ChipSelecionavel(
                texto = genero,
                selecionado = filtro.generoSelecionado == genero,
                onClick = {
                    if (filtro.generoSelecionado == genero) onLimparFiltros() else onGeneroSelecionado(genero)
                }
            )
        }
    }
}

@Composable
private fun CabecalhoSecao(titulo: String, onAdicionarClick: (() -> Unit)? = null) {
    if (onAdicionarClick == null) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge
            )
            BotaoAdicionar(onClick = onAdicionarClick)
        }
    }
}

// Botão "+" ao lado de "Quero Ler" — leva para a busca de livro e depois adiciona ao catálogo.
@Composable
private fun BotaoAdicionar(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            style = LocalTextStyle.current.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
        )
    }
}

@Composable
private fun TextoSecaoVazia() {
    Text(
        text = "Nenhum livro por aqui ainda",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// Seção "Em andamento": cards horizontais em uma LazyRow, como no card do Figma.
@Composable
private fun SecaoEmAndamento(
    livros: List<Livro>,
    onLivroClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CabecalhoSecao(titulo = "Em andamento")
        if (livros.isEmpty()) {
            TextoSecaoVazia()
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(livros, key = { it.id }) { livro ->
                    CardLivro(
                        titulo = livro.titulo,
                        autor = livro.autor,
                        genero = livro.genero,
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

// Seções "Quero Ler" e "Lido": lista vertical de itens (sem card), como no Figma.
@Composable
private fun SecaoListaLivros(
    titulo: String,
    livros: List<Livro>,
    concluido: Boolean,
    onLivroClick: (Long) -> Unit,
    onAdicionarClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CabecalhoSecao(titulo = titulo, onAdicionarClick = onAdicionarClick)
        if (livros.isEmpty()) {
            TextoSecaoVazia()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                livros.forEach { livro ->
                    ItemLivroLista(
                        titulo = livro.titulo,
                        autor = livro.autor,
                        concluido = concluido,
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
            onLivroClick = {},
            onAdicionarLivroClick = {},
            onGeneroSelecionado = {},
            onStatusSelecionado = {},
            onLimparFiltros = {}
        )
    }
}

@Preview(name = "Vazio", showBackground = true)
@Composable
private fun DashboardContentVazioPreview() {
    AppLeiturasTheme {
        DashboardContent(
            uiState = DashboardUiState(),
            onLivroClick = {},
            onAdicionarLivroClick = {},
            onGeneroSelecionado = {},
            onStatusSelecionado = {},
            onLimparFiltros = {}
        )
    }
}

// Demonstra o filtro ativo: chip "Ficção Científica" selecionado (destacado em verde) e o
// mesmo filtro já aplicado nas 3 seções — só "Duna" (Ficção Científica) sobrevive, então
// "Em andamento" e "Lido" aparecem vazios, provando que o filtro corta as 3 seções juntas.
@Preview(name = "Filtro de gênero ativo", showBackground = true)
@Composable
private fun DashboardContentFiltroGeneroPreview() {
    val livrosFiltrados = livrosMock.filter { it.genero == "Ficção Científica" }
    AppLeiturasTheme {
        DashboardContent(
            uiState = DashboardUiState(
                emAndamento = livrosFiltrados.filter { it.status == StatusLeitura.LENDO },
                queroLer = livrosFiltrados.filter { it.status == StatusLeitura.QUERO_LER },
                lido = livrosFiltrados.filter { it.status == StatusLeitura.LIDO },
                filtro = FiltroState(generoSelecionado = "Ficção Científica")
            ),
            onLivroClick = {},
            onAdicionarLivroClick = {},
            onGeneroSelecionado = {},
            onStatusSelecionado = {},
            onLimparFiltros = {}
        )
    }
}

// Demonstra o filtro de status ativo: chip "Lendo" selecionado — só "1984" sobrevive, então
// "Quero Ler" e "Lido" aparecem vazios (mesmo princípio do preview acima, eixo diferente).
@Preview(name = "Filtro de status ativo", showBackground = true)
@Composable
private fun DashboardContentFiltroStatusPreview() {
    val livrosFiltrados = livrosMock.filter { it.status == StatusLeitura.LENDO }
    AppLeiturasTheme {
        DashboardContent(
            uiState = DashboardUiState(
                emAndamento = livrosFiltrados.filter { it.status == StatusLeitura.LENDO },
                queroLer = livrosFiltrados.filter { it.status == StatusLeitura.QUERO_LER },
                lido = livrosFiltrados.filter { it.status == StatusLeitura.LIDO },
                filtro = FiltroState(statusSelecionado = StatusLeitura.LENDO)
            ),
            onLivroClick = {},
            onAdicionarLivroClick = {},
            onGeneroSelecionado = {},
            onStatusSelecionado = {},
            onLimparFiltros = {}
        )
    }
}
