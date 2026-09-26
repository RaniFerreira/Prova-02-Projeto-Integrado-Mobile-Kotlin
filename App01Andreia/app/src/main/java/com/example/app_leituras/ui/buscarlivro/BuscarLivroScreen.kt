package com.example.app_leituras.ui.buscarlivro

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.app_leituras.domain.model.LivroBusca
import com.example.app_leituras.domain.model.ResultadoBusca
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.example.app_leituras.R

@Composable
fun BuscarLivroScreen(
    viewModel: BuscarLivroViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onVoltarClick: () -> Unit = {},
    onResultadoSelecionado: (LivroBusca) -> Unit = {},
    onCadastrarManualmente: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BuscarLivroContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onVoltarClick = onVoltarClick,
        onResultadoSelecionado = onResultadoSelecionado,
        onCadastrarManualmente = onCadastrarManualmente,
        modifier = modifier
    )
}

@Composable
private fun BuscarLivroContent(
    uiState: BuscarLivroUiState,
    onQueryChange: (String) -> Unit,
    onVoltarClick: () -> Unit,
    onResultadoSelecionado: (LivroBusca) -> Unit,
    onCadastrarManualmente: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CabecalhoBuscarLivro(onVoltarClick = onVoltarClick)
        }

        item {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                placeholder = { Text("Ex: Clean Code ou Robert Martin") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        when (val resultado = uiState.resultado) {
            ResultadoBusca.Carregando -> item { EstadoCarregando() }

            is ResultadoBusca.Sucesso -> {
                item {
                    Text(text = "Resultados", style = MaterialTheme.typography.titleLarge)
                }
                items(resultado.livros, key = { it.googleBooksId }) { livroBusca ->
                    ItemResultadoBusca(
                        livroBusca = livroBusca,
                        onClick = { onResultadoSelecionado(livroBusca) }
                    )
                }
            }

            ResultadoBusca.Vazio -> item {
                if (uiState.query.isBlank()) {
                    EstadoMensagem(
                        titulo = "Busque um livro",
                        descricao = "Digite um título, autor ou ISBN para pesquisar na Google Books."
                    )
                } else {
                    EstadoMensagem(
                        titulo = "Nenhum resultado encontrado",
                        descricao = "Não encontramos livros para \"${uiState.query}\"."
                    )
                }
            }

            is ResultadoBusca.Erro -> item {
                EstadoMensagem(titulo = "Não foi possível buscar", descricao = resultado.mensagem)
            }
        }

        item {
            BotaoCadastrarManualmente(onClick = onCadastrarManualmente)
        }
    }
}

@Composable
private fun CabecalhoBuscarLivro(onVoltarClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "Voltar",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .clickable(onClick = onVoltarClick)
                .padding(vertical = 12.dp)
        )
        Text(
            text = "Buscar por API",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Buscar Livro",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Encontre seu próximo livro para o catálogo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ItemResultadoBusca(livroBusca: LivroBusca, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CapaResultado(capaUrl = livroBusca.capaUrl)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = livroBusca.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = livroBusca.autor,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(text = "Usar", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun CapaResultado(capaUrl: String?) {
    Box(
        modifier = Modifier
            .size(48.dp)
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
            Icon(
                painter = painterResource(R.drawable.ic_book),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun EstadoCarregando() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Text(
            text = "Buscando...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EstadoMensagem(titulo: String, descricao: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = descricao,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Sempre visível no fim da lista (não só nos estados vazio/erro): permite cadastrar na mão
// mesmo quando a busca já trouxe resultados, mas nenhum deles é o livro certo.
@Composable
private fun BotaoCadastrarManualmente(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Cadastrar manualmente")
    }
}

private val resultadosMock = listOf(
    LivroBusca(
        googleBooksId = "id1",
        titulo = "Clean Code",
        autor = "Robert C. Martin",
        totalPaginas = 464,
        genero = "Tecnologia",
        capaUrl = null
    ),
    LivroBusca(
        googleBooksId = "id2",
        titulo = "Clean Architecture",
        autor = "Robert C. Martin",
        totalPaginas = 432,
        genero = "Tecnologia",
        capaUrl = null
    ),
    LivroBusca(
        googleBooksId = "id3",
        titulo = "The Clean Coder",
        autor = "Robert C. Martin",
        totalPaginas = 256,
        genero = "Tecnologia",
        capaUrl = null
    )
)

@Preview(name = "Carregando", showBackground = true, heightDp = 900)
@Composable
private fun BuscarLivroContentCarregandoPreview() {
    AppLeiturasTheme {
        BuscarLivroContent(
            uiState = BuscarLivroUiState(query = "Clean", resultado = ResultadoBusca.Carregando),
            onQueryChange = {},
            onVoltarClick = {},
            onResultadoSelecionado = {},
            onCadastrarManualmente = {}
        )
    }
}

@Preview(name = "Resultados — Light", showBackground = true, heightDp = 900)
@Preview(name = "Resultados — Dark", showBackground = true, heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BuscarLivroContentResultadosPreview() {
    AppLeiturasTheme {
        BuscarLivroContent(
            uiState = BuscarLivroUiState(
                query = "Robert Martin",
                resultado = ResultadoBusca.Sucesso(resultadosMock)
            ),
            onQueryChange = {},
            onVoltarClick = {},
            onResultadoSelecionado = {},
            onCadastrarManualmente = {}
        )
    }
}

@Preview(name = "Vazio (sem busca)", showBackground = true, heightDp = 900)
@Composable
private fun BuscarLivroContentVazioPreview() {
    AppLeiturasTheme {
        BuscarLivroContent(
            uiState = BuscarLivroUiState(query = "", resultado = ResultadoBusca.Vazio),
            onQueryChange = {},
            onVoltarClick = {},
            onResultadoSelecionado = {},
            onCadastrarManualmente = {}
        )
    }
}

@Preview(name = "Vazio (sem resultado)", showBackground = true, heightDp = 900)
@Composable
private fun BuscarLivroContentSemResultadoPreview() {
    AppLeiturasTheme {
        BuscarLivroContent(
            uiState = BuscarLivroUiState(query = "xyzabc123", resultado = ResultadoBusca.Vazio),
            onQueryChange = {},
            onVoltarClick = {},
            onResultadoSelecionado = {},
            onCadastrarManualmente = {}
        )
    }
}

@Preview(name = "Erro", showBackground = true, heightDp = 900)
@Composable
private fun BuscarLivroContentErroPreview() {
    AppLeiturasTheme {
        BuscarLivroContent(
            uiState = BuscarLivroUiState(
                query = "Duna",
                resultado = ResultadoBusca.Erro("Sem conexão com a internet. Verifique sua rede e tente novamente.")
            ),
            onQueryChange = {},
            onVoltarClick = {},
            onResultadoSelecionado = {},
            onCadastrarManualmente = {}
        )
    }
}
