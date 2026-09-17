package com.example.app_leituras.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import kotlin.math.roundToInt

// Exibe "página Y de Z" quando paginaAtual/totalPaginas são informados, senão o percentual.
@Composable
fun BarraProgresso(
    percentual: Float,
    modifier: Modifier = Modifier,
    paginaAtual: Int? = null,
    totalPaginas: Int? = null
) {
    val progresso = percentual.coerceIn(0f, 1f)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LinearProgressIndicator(
            progress = { progresso },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        val texto = if (paginaAtual != null && totalPaginas != null) {
            "página $paginaAtual de $totalPaginas"
        } else {
            "${(progresso * 100).roundToInt()}%"
        }
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
