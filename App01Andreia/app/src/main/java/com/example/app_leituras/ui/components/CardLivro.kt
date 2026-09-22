package com.example.app_leituras.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.ui.theme.AppLeiturasTheme

private const val LARGURA_CONTEUDO_DP = 160

// Card horizontal da seção "Em andamento" (ícone + conteúdo), conforme o Figma.
@Composable
fun CardLivro(
    titulo: String,
    autor: String,
    genero: String,
    capaUrl: String?,
    status: StatusLeitura,
    modifier: Modifier = Modifier,
    paginaAtual: Int = 0,
    totalPaginas: Int = 0,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconeLivro(capaUrl = capaUrl)

            Column(
                modifier = Modifier.width(LARGURA_CONTEUDO_DP.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = autor,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TagGenero(texto = genero)

                if (status == StatusLeitura.LENDO && totalPaginas > 0) {
                    BarraProgresso(
                        percentual = paginaAtual.toFloat() / totalPaginas.toFloat(),
                        paginaAtual = paginaAtual,
                        totalPaginas = totalPaginas
                    )
                }
            }
        }
    }
}

@Composable
private fun IconeLivro(capaUrl: String?) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (capaUrl != null) {
            AsyncImage(
                model = capaUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(text = "📖", fontSize = 20.sp)
        }
    }
}

@Composable
private fun TagGenero(texto: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
            genero = "Fantasia",
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
            genero = "Ficção Científica",
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
            genero = "Ficção",
            capaUrl = null,
            status = StatusLeitura.LIDO,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Largura por conteúdo", showBackground = true)
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
                genero = "Tecnologia",
                capaUrl = null,
                status = StatusLeitura.QUERO_LER
            )
            CardLivro(
                titulo = "O Extraordinário e Longuíssimo Título de um Livro Fictício Para Testar o Layout",
                autor = "Um Autor Com Nome Bastante Comprido Sobrenome",
                genero = "Ficção Científica",
                capaUrl = null,
                status = StatusLeitura.LENDO,
                paginaAtual = 42,
                totalPaginas = 500
            )
        }
    }
}
