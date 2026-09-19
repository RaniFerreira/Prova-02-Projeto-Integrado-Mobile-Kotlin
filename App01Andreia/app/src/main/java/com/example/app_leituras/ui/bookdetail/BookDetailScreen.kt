package com.example.app_leituras.ui.bookdetail

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.Meta
import com.example.app_leituras.domain.model.Nota
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.model.TipoNota
import com.example.app_leituras.ui.components.BarraProgresso
import com.example.app_leituras.ui.components.CardPainel
import com.example.app_leituras.ui.components.ChipSelecionavel
import com.example.app_leituras.ui.components.rotuloStatus
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import kotlin.math.roundToInt

private const val DIA_MS = 86_400_000L

@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel,
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    onClicarLerAgora: () -> Unit = {},
    onClicarMeta: () -> Unit = {},
    onClicarNotas: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val livro = uiState.livro

    if (livro == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = if (uiState.carregando) "Carregando..." else "Livro não encontrado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    BookDetailContent(
        livro = livro,
        progresso = uiState.progresso,
        tempoTotalSegundos = uiState.tempoTotalSegundos,
        meta = uiState.meta,
        notasRecentes = uiState.notasRecentes,
        onTrocarStatus = viewModel::onTrocarStatus,
        onVoltarClick = onVoltarClick,
        onClicarLerAgora = onClicarLerAgora,
        onClicarMeta = onClicarMeta,
        onClicarNotas = onClicarNotas,
        modifier = modifier
    )
}

@Composable
private fun BookDetailContent(
    livro: Livro,
    progresso: Float,
    tempoTotalSegundos: Long,
    meta: Meta?,
    notasRecentes: List<Nota>,
    onTrocarStatus: (StatusLeitura) -> Unit,
    onVoltarClick: () -> Unit,
    onClicarLerAgora: () -> Unit,
    onClicarMeta: () -> Unit,
    onClicarNotas: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CabecalhoLivro(livro = livro, onVoltarClick = onVoltarClick)

        CapaLivroDetalhe()

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Status", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusLeitura.entries.forEach { status ->
                    ChipSelecionavel(
                        texto = rotuloStatus(status),
                        selecionado = livro.status == status,
                        onClick = { onTrocarStatus(status) }
                    )
                }
            }
        }

        CardPainel {
            BarraProgresso(
                percentual = progresso,
                paginaAtual = (progresso * livro.totalPaginas).roundToInt(),
                totalPaginas = livro.totalPaginas,
                exibirPercentualAoLado = true
            )
        }

        CardPainel(onClick = onClicarMeta) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "🕐", fontSize = 28.sp)
                Column {
                    Text(
                        text = formatarTempo(tempoTotalSegundos),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (meta != null) formatarMeta(meta) else "Definir meta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        CardPainel(onClick = onClicarNotas) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "📓", fontSize = 20.sp)
                Column {
                    Text(text = "Notas e Anotações", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = notasRecentes.firstOrNull()?.conteudo ?: "Nenhuma nota ainda",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Button(
            onClick = onClicarLerAgora,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(text = "Ler agora")
        }
    }
}

@Composable
private fun CabecalhoLivro(livro: Livro, onVoltarClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Detalhes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "←",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable(onClick = onVoltarClick)
            )
            Text(
                text = livro.titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = "${livro.autor} • ${livro.genero}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// TODO: quando a busca via Google Books API for integrada, trocar este placeholder
// por um carregador de imagem (ex.: Coil) usando capaUrl — mesmo TODO do IconeLivro/CardLivro.
@Composable
private fun CapaLivroDetalhe() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "📖", fontSize = 48.sp)
            Text(
                text = "Capa",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun formatarTempo(segundosTotal: Long): String {
    val horas = segundosTotal / 3600
    val minutos = (segundosTotal % 3600) / 60
    return if (horas > 0) "${horas}h e ${minutos}min" else "${minutos}min"
}

private fun formatarMeta(meta: Meta): String {
    val dataAlvo = meta.dataAlvo
    if (dataAlvo != null) {
        val diasRestantes = ((dataAlvo - System.currentTimeMillis()) / DIA_MS).coerceAtLeast(0)
        val meses = diasRestantes / 30
        return if (meses > 0) "Meta: $meses meses" else "Meta: $diasRestantes dias"
    }
    return "Meta: ${meta.tempoPrevistoMinutos} min"
}

@Preview(name = "Light", showBackground = true, heightDp = 1400)
@Preview(name = "Dark", showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookDetailContentPreview() {
    AppLeiturasTheme {
        BookDetailContent(
            livro = Livro(
                id = 3L,
                titulo = "1984",
                autor = "George Orwell",
                totalPaginas = 328,
                genero = "Ficção",
                capaUrl = null,
                status = StatusLeitura.LENDO,
                paginaAtual = 142,
                googleBooksId = null,
                dataCriacao = 0L
            ),
            progresso = 0.5f,
            tempoTotalSegundos = 17_160L,
            meta = Meta(id = 1L, livroId = 3L, tempoPrevistoMinutos = 30, dataAlvo = System.currentTimeMillis() + 60L * DIA_MS),
            notasRecentes = listOf(
                Nota(
                    id = 1L,
                    livroId = 3L,
                    tipo = TipoNota.CITACAO,
                    conteudo = "\"A Guerra é Paz. A Liberdade é Escravidão. A Ignorância é Força.\"",
                    dataHora = 0L
                )
            ),
            onTrocarStatus = {},
            onVoltarClick = {},
            onClicarLerAgora = {},
            onClicarMeta = {},
            onClicarNotas = {}
        )
    }
}

@Preview(name = "Sem meta definida", showBackground = true, heightDp = 1400)
@Composable
private fun BookDetailContentSemMetaPreview() {
    AppLeiturasTheme {
        BookDetailContent(
            livro = Livro(
                id = 4L,
                titulo = "Sapiens",
                autor = "Yuval Noah Harari",
                totalPaginas = 464,
                genero = "Não-ficção",
                capaUrl = null,
                status = StatusLeitura.LENDO,
                paginaAtual = 200,
                googleBooksId = null,
                dataCriacao = 0L
            ),
            progresso = 200f / 464f,
            tempoTotalSegundos = 5_400L,
            meta = null,
            notasRecentes = emptyList(),
            onTrocarStatus = {},
            onVoltarClick = {},
            onClicarLerAgora = {},
            onClicarMeta = {},
            onClicarNotas = {}
        )
    }
}
