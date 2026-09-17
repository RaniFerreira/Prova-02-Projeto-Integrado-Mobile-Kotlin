package com.example.app_leituras.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.ui.theme.AppLeiturasTheme

private const val LARGURA_CARD_DP = 150

@Composable
fun CardLivro(
    titulo: String,
    autor: String,
    capaUrl: String?,
    status: StatusLeitura,
    modifier: Modifier = Modifier,
    paginaAtual: Int = 0,
    totalPaginas: Int = 0,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        // width() fica fora de `modifier` para a largura ser fixa, não sobrescrita por fillMaxWidth()/weight() do chamador.
        modifier = Modifier.width(LARGURA_CARD_DP.dp).then(modifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            CapaLivro(capaUrl = capaUrl)

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = autor,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
            )

            StatusBadge(status = status)

            if (status == StatusLeitura.LENDO && totalPaginas > 0) {
                BarraProgresso(
                    percentual = paginaAtual.toFloat() / totalPaginas.toFloat(),
                    paginaAtual = paginaAtual,
                    totalPaginas = totalPaginas,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// TODO: quando a busca via Google Books API for integrada (item 10 do planejamento),
// trocar este placeholder por um carregador de imagem (ex.: Coil) usando capaUrl.
@Composable
private fun CapaLivro(capaUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "📖",
            fontSize = 32.sp
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CardLivroLendoPreview() {
    AppLeiturasTheme {
        CardLivro(
            titulo = "O Nome do Vento",
            autor = "Patrick Rothfuss",
            capaUrl = null,
            status = StatusLeitura.LENDO,
            paginaAtual = 126,
            totalPaginas = 300,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Quero Ler", showBackground = true)
@Composable
private fun CardLivroQueroLerPreview() {
    AppLeiturasTheme {
        CardLivro(
            titulo = "Duna",
            autor = "Frank Herbert",
            capaUrl = null,
            status = StatusLeitura.QUERO_LER,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Lido", showBackground = true)
@Composable
private fun CardLivroLidoPreview() {
    AppLeiturasTheme {
        CardLivro(
            titulo = "1984",
            autor = "George Orwell",
            capaUrl = null,
            status = StatusLeitura.LIDO,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Largura consistente", showBackground = true)
@Composable
private fun CardLivroLarguraConsistentePreview() {
    AppLeiturasTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            CardLivro(
                titulo = "Ágil",
                autor = "Kent",
                capaUrl = null,
                status = StatusLeitura.QUERO_LER
            )
            CardLivro(
                titulo = "O Extraordinário e Longuíssimo Título de um Livro Fictício Para Testar o Layout",
                autor = "Um Autor Com Nome Bastante Comprido Sobrenome",
                capaUrl = null,
                status = StatusLeitura.LENDO,
                paginaAtual = 42,
                totalPaginas = 500
            )
        }
    }
}
