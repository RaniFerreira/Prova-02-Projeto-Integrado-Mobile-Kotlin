package com.example.rastreadorprazo.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rastreadorprazo.data.StatusObrigacao
import com.example.rastreadorprazo.ui.theme.CorCancelada
import com.example.rastreadorprazo.ui.theme.CorPaga
import com.example.rastreadorprazo.ui.theme.CorPendente

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
