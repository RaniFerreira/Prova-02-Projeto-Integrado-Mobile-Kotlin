package com.example.app_leituras.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import kotlin.math.roundToInt

// Exibe "X/Y páginas" quando paginaAtual/totalPaginas são informados, senão o percentual.
// Texto acima da barra e barra de 8dp, seguindo o card "Em andamento" do Figma.
// exibirPercentualAoLado (usado no Book Detail) mostra "Página X de Y" + "XX%" lado a lado,
// sem alterar o comportamento padrão já usado no Dashboard/CardLivro.
@Composable
fun BarraProgresso(
    percentual: Float,
    modifier: Modifier = Modifier,
    paginaAtual: Int? = null,
    totalPaginas: Int? = null,
    exibirPercentualAoLado: Boolean = false
) {
    val progresso = percentual.coerceIn(0f, 1f)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (exibirPercentualAoLado && paginaAtual != null && totalPaginas != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Página $paginaAtual de $totalPaginas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${(progresso * 100).roundToInt()}%",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            val texto = if (paginaAtual != null && totalPaginas != null) {
                "$paginaAtual/$totalPaginas páginas"
            } else {
                "${(progresso * 100).roundToInt()}%"
            }
            Text(
                text = texto,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { progresso },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BarraProgressoComPaginasPreview() {
    AppLeiturasTheme {
        BarraProgresso(
            percentual = 0.42f,
            paginaAtual = 126,
            totalPaginas = 300,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Percentual", showBackground = true)
@Composable
private fun BarraProgressoPercentualPreview() {
    AppLeiturasTheme {
        BarraProgresso(
            percentual = 0.75f,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Percentual ao lado (Book Detail)", showBackground = true)
@Composable
private fun BarraProgressoPercentualAoLadoPreview() {
    AppLeiturasTheme {
        BarraProgresso(
            percentual = 0.5f,
            paginaAtual = 142,
            totalPaginas = 284,
            exibirPercentualAoLado = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}
