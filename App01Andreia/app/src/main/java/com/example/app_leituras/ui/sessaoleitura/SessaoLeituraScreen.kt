package com.example.app_leituras.ui.sessaoleitura

import android.content.res.Configuration
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

@Composable
fun SessaoLeituraScreen(
    viewModel: SessaoLeituraViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    onSessaoFinalizada: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.sessaoFinalizada) {
        if (uiState.sessaoFinalizada) onSessaoFinalizada()
    }

    SessaoLeituraContent(
        uiState = uiState,
        onVoltarClick = onVoltarClick,
        onIniciarPausar = viewModel::onIniciarPausar,
        onPaginaAtualChange = viewModel::onPaginaAtualChange,
        onFinalizarSessao = viewModel::onFinalizarSessao,
        modifier = modifier
    )
}

@Composable
private fun SessaoLeituraContent(
    uiState: SessaoLeituraUiState,
    onVoltarClick: () -> Unit,
    onIniciarPausar: () -> Unit,
    onPaginaAtualChange: (String) -> Unit,
    onFinalizarSessao: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CabecalhoSessao(titulo = uiState.tituloLivro, onVoltarClick = onVoltarClick)

        CardPainel {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Última página lida",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BarraProgresso(
                    percentual = if (uiState.totalPaginas > 0) uiState.paginaInicio.toFloat() / uiState.totalPaginas else 0f,
                    paginaAtual = uiState.paginaInicio,
                    totalPaginas = uiState.totalPaginas,
                    exibirPercentualAoLado = true
                )
            }
        }

        CardPainel {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = rotuloCronometro(uiState),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatarCronometro(uiState.tempoDecorridoSegundos),
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 40.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "Seu tempo será registrado automaticamente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = onIniciarPausar,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(text = if (uiState.rodando) "⏸ Pausar" else "▶ Iniciar")
        }

        CardPainel {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Sessão atual", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LinhaSessaoAtual(rotulo = "Início", valor = formatarHoraInicio(uiState.horaInicioSessao))
                    LinhaSessaoAtual(rotulo = "Páginas hoje", valor = "${uiState.paginasLidasHoje} páginas")
                    LinhaSessaoAtual(rotulo = "Tempo hoje", valor = formatarTempoResumido(uiState.tempoLidoHojeSegundos))
                }
            }
        }

        OutlinedTextField(
            value = uiState.paginaAtualInput,
            onValueChange = onPaginaAtualChange,
            label = { Text("Página alcançada") },
            isError = uiState.erro != null,
            supportingText = { uiState.erro?.let { Text(it) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onFinalizarSessao,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(text = "Finalizar e registrar leitura")
        }
    }
}

@Composable
private fun CabecalhoSessao(titulo: String, onVoltarClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Sessão de leitura",
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
                text = titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LinhaSessaoAtual(rotulo: String, valor: String) {
    Column {
        Text(text = rotulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun rotuloCronometro(uiState: SessaoLeituraUiState): String = when {
    uiState.rodando -> "LENDO..."
    uiState.tempoDecorridoSegundos > 0L -> "PAUSADO"
    else -> "PRONTO PARA COMEÇAR"
}

private fun formatarCronometro(segundosTotal: Long): String {
    val horas = segundosTotal / 3600
    val minutos = (segundosTotal % 3600) / 60
    val segundos = segundosTotal % 60
    return String.format(Locale.US, "%02d:%02d:%02d", horas, minutos, segundos)
}

private fun formatarTempoResumido(segundosTotal: Long): String {
    val minutos = segundosTotal / 60
    return "$minutos min"
}

private fun formatarHoraInicio(horaInicioSessao: Long?): String {
    if (horaInicioSessao == null) return "--:--"
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(horaInicioSessao)
}

@Preview(name = "Pronto para começar", showBackground = true, heightDp = 1400)
@Preview(name = "Dark", showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SessaoLeituraContentPreview() {
    AppLeiturasTheme {
        SessaoLeituraContent(
            uiState = SessaoLeituraUiState(
                tituloLivro = "Clean Code",
                totalPaginas = 284,
                paginaInicio = 142,
                paginasLidasHoje = 0,
                tempoLidoHojeSegundos = 0L
            ),
            onVoltarClick = {},
            onIniciarPausar = {},
            onPaginaAtualChange = {},
            onFinalizarSessao = {}
        )
    }
}

@Preview(name = "Lendo (cronômetro rodando)", showBackground = true, heightDp = 1400)
@Composable
private fun SessaoLeituraContentRodandoPreview() {
    AppLeiturasTheme {
        SessaoLeituraContent(
            uiState = SessaoLeituraUiState(
                tituloLivro = "Clean Code",
                totalPaginas = 284,
                paginaInicio = 142,
                paginaAtualInput = "158",
                tempoDecorridoSegundos = 632L,
                rodando = true,
                horaInicioSessao = System.currentTimeMillis(),
                paginasLidasHoje = 16,
                tempoLidoHojeSegundos = 632L
            ),
            onVoltarClick = {},
            onIniciarPausar = {},
            onPaginaAtualChange = {},
            onFinalizarSessao = {}
        )
    }
}

@Preview(name = "Com erro de validação", showBackground = true, heightDp = 1400)
@Composable
private fun SessaoLeituraContentErroPreview() {
    AppLeiturasTheme {
        SessaoLeituraContent(
            uiState = SessaoLeituraUiState(
                tituloLivro = "Clean Code",
                totalPaginas = 284,
                paginaInicio = 142,
                paginaAtualInput = "100",
                erro = "Informe uma página maior que 142"
            ),
            onVoltarClick = {},
            onIniciarPausar = {},
            onPaginaAtualChange = {},
            onFinalizarSessao = {}
        )
    }
}
