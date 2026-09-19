package com.example.app_leituras.ui.novolivro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.repository.LivroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class NovoLivroViewModel(
    private val livroRepository: LivroRepository,
    livroPreenchido: Livro? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(estadoInicial(livroPreenchido))
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
        fun estadoInicial(livroPreenchido: Livro?): NovoLivroUiState {
            if (livroPreenchido == null) return NovoLivroUiState()
            return NovoLivroUiState(
                titulo = livroPreenchido.titulo,
                autor = livroPreenchido.autor,
                totalPaginas = if (livroPreenchido.totalPaginas > 0) livroPreenchido.totalPaginas.toString() else "",
                genero = livroPreenchido.genero.ifBlank { null },
                status = livroPreenchido.status,
                capaUrl = livroPreenchido.capaUrl,
                googleBooksId = livroPreenchido.googleBooksId
            )
        }
    }
}

// Sem Hilt/Koin por enquanto: injeta o LivroRepository manualmente por construtor (mesmo padrão do Dashboard).
class NovoLivroViewModelFactory(
    private val livroRepository: LivroRepository,
    private val livroPreenchido: Livro? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NovoLivroViewModel(livroRepository, livroPreenchido) as T
    }
}
