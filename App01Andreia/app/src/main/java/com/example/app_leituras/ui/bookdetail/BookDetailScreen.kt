package com.example.app_leituras.ui.bookdetail

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.app_leituras.R
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.Meta
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.ui.components.BarraProgresso
import com.example.app_leituras.ui.components.CardPainel
import com.example.app_leituras.ui.components.ChipSelecionavel
import com.example.app_leituras.ui.components.rotuloStatus
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import kotlin.math.roundToInt

private const val DIA_MS = 86_400_000L

@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel = hiltViewModel(),
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
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CabecalhoLivro(livro = livro, onVoltarClick = onVoltarClick)

        CapaLivroDetalhe(capaUrl = livro.capaUrl)

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Chips de largura igual e espaçamento uniforme, centralizados (Section - Status do Figma).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusLeitura.entries.forEach { status ->
                    ChipSelecionavel(
                        texto = rotuloStatus(status),
                        selecionado = livro.status == status,
                        onClick = { onTrocarStatus(status) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 9.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        CardPainel(
            comBorda = true,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BarraProgresso(
                percentual = progresso,
                paginaAtual = (progresso * livro.totalPaginas).roundToInt(),
                totalPaginas = livro.totalPaginas,
                exibirPercentualAoLado = true,
                textoEmDestaque = true
            )
        }

        CardPainel(
            onClick = onClicarMeta,
            comBorda = true,
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.width(88.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clock),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = formatarTempo(tempoTotalSegundos),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp, lineHeight = 28.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (meta != null) formatarMeta(meta) else "Definir meta",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        CardPainel(onClick = onClicarNotas, comBorda = true, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notebook),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Notas e Anotações",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
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

// Cabeçalho do Figma: seta à esquerda e, ao lado, "Detalhes" / título / autor + chip de gênero.
@Composable
private fun CabecalhoLivro(livro: Livro, onVoltarClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "Voltar",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .clickable(onClick = onVoltarClick)
                .padding(vertical = 12.dp)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Detalhes",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = livro.titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = livro.autor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                ChipGenero(texto = livro.genero)
            }
        }
    }
}

// Chip de gênero do cabeçalho: fundo verde claro, texto verde (Frame 135:110 do Figma).
@Composable
private fun ChipGenero(texto: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// Capa em proporção de livro (140x200, ~2:3), centralizada, com borda verde (Cover Placeholder do Figma).
@Composable
private fun CapaLivroDetalhe(capaUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(width = 140.dp, height = 200.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            if (capaUrl != null) {
                AsyncImage(
                    model = capaUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_book_open),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Capa",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
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
            onTrocarStatus = {},
            onVoltarClick = {},
            onClicarLerAgora = {},
            onClicarMeta = {},
            onClicarNotas = {}
        )
    }
}
