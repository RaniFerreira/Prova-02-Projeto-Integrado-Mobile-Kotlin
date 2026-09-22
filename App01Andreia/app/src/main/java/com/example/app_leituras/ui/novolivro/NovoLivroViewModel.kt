package com.example.app_leituras.ui.novolivro

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.repository.LivroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Campos obrigatórios do formulário — usados para marcar erro visual quando vazios/inválidos.
enum class CampoNovoLivro {
    TITULO,
    AUTOR,
    TOTAL_PAGINAS
}

data class NovoLivroUiState(
    val titulo: String = "",
    val autor: String = "",
    val totalPaginas: String = "",
    val genero: String? = null,
    val status: StatusLeitura = StatusLeitura.QUERO_LER,
    val capaUrl: String? = null,
    val googleBooksId: String? = null,
    val camposInvalidos: Set<CampoNovoLivro> = emptySet(),
    val salvando: Boolean = false,
    val livroSalvoId: Long? = null
)

@HiltViewModel
class NovoLivroViewModel @Inject constructor(
    private val livroRepository: LivroRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(estadoInicial(savedStateHandle))
    val uiState: StateFlow<NovoLivroUiState> = _uiState.asStateFlow()

    fun onTituloChange(valor: String) {
        _uiState.update { it.copy(titulo = valor, camposInvalidos = it.camposInvalidos - CampoNovoLivro.TITULO) }
    }

    fun onAutorChange(valor: String) {
        _uiState.update { it.copy(autor = valor, camposInvalidos = it.camposInvalidos - CampoNovoLivro.AUTOR) }
    }

    fun onTotalPaginasChange(valor: String) {
        _uiState.update {
            it.copy(totalPaginas = valor, camposInvalidos = it.camposInvalidos - CampoNovoLivro.TOTAL_PAGINAS)
        }
    }

    fun onGeneroChange(genero: String) {
        _uiState.update { estado -> estado.copy(genero = if (estado.genero == genero) null else genero) }
    }

    fun onStatusChange(status: StatusLeitura) {
        _uiState.update { it.copy(status = status) }
    }

    fun onCapaChange(capaUrl: String?) {
        _uiState.update { it.copy(capaUrl = capaUrl) }
    }

    // O picker de fotos do Android só garante acesso de leitura à URI enquanto o processo do
    // app está vivo — pra sobreviver a reaberturas do app (a capa é salva no Room e reaparece
    // dias depois no Dashboard/Book Detail), copiamos os bytes pro armazenamento interno do app
    // e guardamos esse caminho em vez da URI original do picker.
    fun onCapaSelecionada(uri: Uri) {
        viewModelScope.launch {
            val caminhoSalvo = withContext(Dispatchers.IO) { copiarParaArmazenamentoInterno(uri) }
            if (caminhoSalvo != null) onCapaChange(caminhoSalvo)
        }
    }

    private fun copiarParaArmazenamentoInterno(uri: Uri): String? {
        return try {
            val pastaCapas = File(context.filesDir, "capas").apply { mkdirs() }
            val destino = File(pastaCapas, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { entrada ->
                destino.outputStream().use { saida -> entrada.copyTo(saida) }
            } ?: return null
            Uri.fromFile(destino).toString()
        } catch (e: IOException) {
            null
        }
    }

    fun onSalvarClick() {
        val estado = _uiState.value
        val camposInvalidos = buildSet {
            if (estado.titulo.isBlank()) add(CampoNovoLivro.TITULO)
            if (estado.autor.isBlank()) add(CampoNovoLivro.AUTOR)
            if ((estado.totalPaginas.toIntOrNull() ?: 0) <= 0) add(CampoNovoLivro.TOTAL_PAGINAS)
        }
        if (camposInvalidos.isNotEmpty()) {
            _uiState.update { it.copy(camposInvalidos = camposInvalidos) }
            return
        }

        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            val id = livroRepository.salvar(
                Livro(
                    titulo = estado.titulo.trim(),
                    autor = estado.autor.trim(),
                    totalPaginas = estado.totalPaginas.toInt(),
                    genero = estado.genero.orEmpty(),
                    capaUrl = estado.capaUrl,
                    status = estado.status,
                    paginaAtual = 0,
                    googleBooksId = estado.googleBooksId,
                    dataCriacao = System.currentTimeMillis()
                )
            )
            _uiState.update { it.copy(salvando = false, livroSalvoId = id) }
        }
    }

    private companion object {
        // Pré-preenchimento vindo da Buscar Livro (resultado da Google Books API) chega como
        // argumentos de rota primitivos, não como um Livro inteiro — Livro não é o tipo certo
        // pra passar via SavedStateHandle/Navigation Compose (não é Parcelable, e nem faria
        // sentido: "livroPreenchido" aqui representa um LivroBusca, não um livro já cadastrado).
        // Sem "titulo", assume cadastro manual vazio (fluxo "não encontrei" da Buscar Livro).
        fun estadoInicial(savedStateHandle: SavedStateHandle): NovoLivroUiState {
            val titulo = savedStateHandle.get<String>("titulo") ?: return NovoLivroUiState()
            return NovoLivroUiState(
                titulo = titulo,
                autor = savedStateHandle.get<String>("autor").orEmpty(),
                totalPaginas = savedStateHandle.get<Int>("totalPaginas")?.takeIf { it > 0 }?.toString().orEmpty(),
                genero = savedStateHandle.get<String>("genero")?.ifBlank { null },
                status = StatusLeitura.QUERO_LER,
                capaUrl = savedStateHandle.get<String>("capaUrl"),
                googleBooksId = savedStateHandle.get<String>("googleBooksId")
            )
        }
    }
}
