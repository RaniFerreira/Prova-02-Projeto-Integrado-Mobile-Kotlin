package com.example.app_leituras.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import com.example.app_leituras.ui.theme.CinzaEscuro
import com.example.app_leituras.ui.theme.VerdeClaro
import com.example.app_leituras.ui.theme.VerdePrincipal
import com.example.app_leituras.ui.theme.VerdeSecundario

private data class CoresStatus(val texto: String, val container: Color, val conteudo: Color)

private fun coresPara(status: StatusLeitura): CoresStatus = when (status) {
    StatusLeitura.QUERO_LER -> CoresStatus("Quero Ler", VerdeClaro, VerdeSecundario)
    StatusLeitura.LENDO -> CoresStatus("Lendo", VerdePrincipal, CinzaEscuro)
    StatusLeitura.LIDO -> CoresStatus("Lido", VerdeSecundario, VerdeClaro)
}

@Composable
fun StatusBadge(
    status: StatusLeitura,
    modifier: Modifier = Modifier
) {
    val cores = coresPara(status)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = cores.container,
        contentColor = cores.conteudo
    ) {
        Text(
            text = cores.texto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(PaddingValues(horizontal = 10.dp, vertical = 4.dp))
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatusBadgePreview() {
    AppLeiturasTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            StatusBadge(status = StatusLeitura.QUERO_LER)
            StatusBadge(status = StatusLeitura.LENDO)
            StatusBadge(status = StatusLeitura.LIDO)
        }
    }
}
