package com.example.app_leituras.ui.definirmeta

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app_leituras.ui.components.BarraProgresso
import com.example.app_leituras.ui.components.CardPainel
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.ceil

private const val DIA_MS = 86_400_000L

private data class OpcaoPrazo(val rotulo: String, val dias: Int)

private val OPCOES_PRAZO = listOf(
    OpcaoPrazo("2 semanas", 14),
    OpcaoPrazo("1 mês", 30),
    OpcaoPrazo("3 meses", 90)
)

@Composable
fun DefinirMetaScreen(
    viewModel: DefinirMetaViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    onMetaSalva: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.metaSalva) {
        if (uiState.metaSalva) onMetaSalva()
    }

    DefinirMetaContent(
        uiState = uiState,
        livroTitulo = uiState.livroTitulo,
        livroAutor = uiState.livroAutor,
        paginaAtual = uiState.paginaAtual,
        totalPaginas = uiState.totalPaginas,
        onVoltarClick = onVoltarClick,
        onTempoPrevistoChange = viewModel::onTempoPrevistoChange,
        onDataAlvoChange = viewModel::onDataAlvoChange,
        onSalvarClick = viewModel::onSalvarClick,
        modifier = modifier
    )
}

@Composable
private fun DefinirMetaContent(
    uiState: DefinirMetaUiState,
    livroTitulo: String,
    livroAutor: String,
    paginaAtual: Int,
    totalPaginas: Int,
    onVoltarClick: () -> Unit,
    onTempoPrevistoChange: (String) -> Unit,
    onDataAlvoChange: (Long?) -> Unit,
    onSalvarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Seleção do chip de prazo é só um atalho de preenchimento (não é dado de domínio) —
    // por isso vive localmente na UI, não no NovoLivroUiState/DefinirMetaUiState.
    var diasSelecionados by rememberSaveable { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CabecalhoDefinirMeta(editando = uiState.editando, onVoltarClick = onVoltarClick)

        CardPainel {
            ResumoLivro(
                titulo = livroTitulo,
                autor = livroAutor,
                paginaAtual = paginaAtual,
                totalPaginas = totalPaginas
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Em quanto tempo quer terminar?", style = MaterialTheme.typography.titleMedium)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OPCOES_PRAZO.forEach { opcao ->
                    OpcaoDuracao(
                        texto = opcao.rotulo,
                        selecionado = diasSelecionados == opcao.dias,
                        onClick = {
                            diasSelecionados = opcao.dias
                            onDataAlvoChange(System.currentTimeMillis() + opcao.dias * DIA_MS)
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = uiState.tempoPrevistoMinutos,
            onValueChange = onTempoPrevistoChange,
            label = { Text("Tempo previsto por sessão (min)") },
            isError = uiState.erro != null,
            supportingText = { uiState.erro?.let { Text(it) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Data para concluir",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            CardPainel {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.dataAlvo?.let(::formatarData) ?: "Escolha um prazo acima",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(text = "📅", fontSize = 20.sp)
                }
            }
        }

        if (uiState.dataAlvo != null) {
            CardPainel {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Seu ritmo sugerido", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = formatarRitmo(paginaAtual, totalPaginas, uiState.dataAlvo),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    BarraProgresso(
                        percentual = if (totalPaginas > 0) paginaAtual.toFloat() / totalPaginas else 0f
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
            Text(text = if (uiState.salvando) "Salvando..." else "Salvar Meta")
        }
    }
}

@Composable
private fun CabecalhoDefinirMeta(editando: Boolean, onVoltarClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = if (editando) "Editando sua meta" else "Planeje sua leitura",
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
                text = "Definir Meta",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ResumoLivro(titulo: String, autor: String, paginaAtual: Int, totalPaginas: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📖", fontSize = 20.sp)
        }
        Column {
            Text(text = titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                text = autor,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$paginaAtual de $totalPaginas páginas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Pílula de prazo em largura total, empilhada (diferente do ChipSelecionavel, que é "hug content"
// e fica lado a lado em Row — reusá-lo aqui quebraria o layout dos outros chips do app).
@Composable
private fun OpcaoDuracao(texto: String, selecionado: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selecionado) Color.White else MaterialTheme.colorScheme.onSurface,
        border = if (selecionado) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
        )
    }
}

private fun formatarData(instante: Long): String =
    SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("pt", "BR")).format(instante)

private fun formatarRitmo(paginaAtual: Int, totalPaginas: Int, dataAlvo: Long): String {
    val paginasRestantes = (totalPaginas - paginaAtual).coerceAtLeast(0)
    if (paginasRestantes <= 0) return "Você já concluiu a leitura — parabéns!"
    val diasRestantes = ((dataAlvo - System.currentTimeMillis()) / DIA_MS).coerceAtLeast(1)
    val paginasPorDia = ceil(paginasRestantes / diasRestantes.toDouble()).toInt()
    return "Leia cerca de $paginasPorDia páginas por dia para concluir no prazo."
}

@Preview(name = "Criando meta", showBackground = true, heightDp = 1500)
@Preview(name = "Dark", showBackground = true, heightDp = 1500, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DefinirMetaContentCriandoPreview() {
    AppLeiturasTheme {
        DefinirMetaContent(
            uiState = DefinirMetaUiState(carregando = false),
            livroTitulo = "Clean Code",
            livroAutor = "Robert C. Martin",
            paginaAtual = 142,
            totalPaginas = 284,
            onVoltarClick = {},
            onTempoPrevistoChange = {},
            onDataAlvoChange = {},
            onSalvarClick = {}
        )
    }
}

@Preview(name = "Editando meta existente", showBackground = true, heightDp = 1500)
@Composable
private fun DefinirMetaContentEditandoPreview() {
    AppLeiturasTheme {
        DefinirMetaContent(
            uiState = DefinirMetaUiState(
                tempoPrevistoMinutos = "30",
                dataAlvo = System.currentTimeMillis() + 60L * DIA_MS,
                editando = true,
                carregando = false
            ),
            livroTitulo = "Clean Code",
            livroAutor = "Robert C. Martin",
            paginaAtual = 142,
            totalPaginas = 284,
            onVoltarClick = {},
            onTempoPrevistoChange = {},
            onDataAlvoChange = {},
            onSalvarClick = {}
        )
    }
}

@Preview(name = "Com erro de validação", showBackground = true, heightDp = 1500)
@Composable
private fun DefinirMetaContentErroPreview() {
    AppLeiturasTheme {
        DefinirMetaContent(
            uiState = DefinirMetaUiState(erro = "Informe um tempo previsto maior que zero", carregando = false),
            livroTitulo = "Clean Code",
            livroAutor = "Robert C. Martin",
            paginaAtual = 142,
            totalPaginas = 284,
            onVoltarClick = {},
            onTempoPrevistoChange = {},
            onDataAlvoChange = {},
            onSalvarClick = {}
        )
    }
}
