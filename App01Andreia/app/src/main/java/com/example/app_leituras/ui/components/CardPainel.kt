package com.example.app_leituras.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Card branco padrão (16dp/elevação 2dp) reutilizado nas telas de detalhe (Book Detail, Ler Agora),
// mesmo estilo do CardLivro — opcionalmente clicável.
// comBorda = variante do Book Detail no Figma: borda fina verde, cantos de 14dp e sem sombra.
// Sem onClick usa o Card não clicável: um Card clicável desabilitado aplicaria a cor de
// "disabled" do Material 3 e o fundo deixaria de ser branco.
@Composable
fun CardPainel(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    comBorda: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(if (comBorda) 14.dp else 16.dp)
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    val border = if (comBorda) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
    val elevation = CardDefaults.cardElevation(defaultElevation = if (comBorda) 0.dp else 2.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation
        ) {
            Box(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation
        ) {
            Box(modifier = Modifier.padding(contentPadding)) {
                content()
            }
        }
    }
}
