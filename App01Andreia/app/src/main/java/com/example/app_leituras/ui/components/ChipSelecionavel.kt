package com.example.app_leituras.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_leituras.domain.model.StatusLeitura

// Chip de seleção única (gênero, status) reutilizado por Novo Livro e Book Detail —
// mesmo estilo dos chips de filtro do protótipo Figma (borda verde quando não selecionado,
// preenchimento verde sólido quando selecionado).
@Composable
fun ChipSelecionavel(texto: String, selecionado: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selecionado) Color.White else MaterialTheme.colorScheme.onSurface,
        border = if (selecionado) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

fun rotuloStatus(status: StatusLeitura): String = when (status) {
    StatusLeitura.QUERO_LER -> "Quero Ler"
    StatusLeitura.LENDO -> "Lendo"
    StatusLeitura.LIDO -> "Lido"
}
