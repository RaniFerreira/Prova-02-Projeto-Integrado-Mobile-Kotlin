package com.example.rastreadorprazo.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rastreadorprazo.data.StatusObrigacao

private val CorPendente = Color(0xFFFFA000)
private val CorPaga = Color(0xFF2E7D32)
private val CorCancelada = Color(0xFF757575)

@Composable
fun StatusChip(status: StatusObrigacao) {
    val (cor, rotulo) = when (status) {
        StatusObrigacao.PENDENTE -> CorPendente to "Pendente"
        StatusObrigacao.PAGA -> CorPaga to "Paga"
        StatusObrigacao.CANCELADA -> CorCancelada to "Cancelada"
    }
    Surface(
        color = cor.copy(alpha = 0.15f),
        contentColor = cor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
