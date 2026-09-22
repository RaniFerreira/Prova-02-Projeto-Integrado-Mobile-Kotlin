package com.example.app_leituras.ui.definirmeta

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Meta
import com.example.app_leituras.domain.repository.LivroRepository
import com.example.app_leituras.domain.repository.MetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DefinirMetaUiState(
    val livroTitulo: String = "",
    val livroAutor: String = "",
    val paginaAtual: Int = 0,
    val totalPaginas: Int = 0,
    val tempoPrevistoMinutos: String = "",
    val dataAlvo: Long? = null,
    val editando: Boolean = false,
    val erro: String? = null,
    val salvando: Boolean = false,
    val metaSalva: Boolean = false,
    val carregando: Boolean = true
)

@HiltViewModel
class DefinirMetaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val metaRepository: MetaRepository,
    livroRepository: LivroRepository
) : ViewModel() {

    // Argumento de rota (Navigation Compose), não dependência de módulo — vem do SavedStateHandle.
    private val livroId: Long = savedStateHandle.get<Long>("livroId") ?: 0L

    private val _uiState = MutableStateFlow(DefinirMetaUiState())
    val uiState: StateFlow<DefinirMetaUiState> = _uiState.asStateFlow()

    init {
        // Observa (não busca uma vez só) para pré-carregar em edição e refletir qualquer
        // mudança externa na meta enquanto a tela estiver aberta — ver explicação ao final.
        viewModelScope.launch {
            metaRepository.observarPorLivro(livroId).collect { meta ->
                _uiState.update { estado ->
                    if (meta != null) {
                        estado.copy(
                            tempoPrevistoMinutos = meta.tempoPrevistoMinutos.toString(),
                            dataAlvo = meta.dataAlvo,
                            editando = true,
                            carregando = false
                        )
                    } else {
                        estado.copy(editando = false, carregando = false)
                    }
                }
            }
        }
        // Título/autor/páginas do livro só vêm pelo livroId da rota (a tela não recebe mais
        // esses campos como parâmetro do caller), então são observados aqui pra alimentar o
        // resumo do livro e o cálculo de ritmo sugerido.
        viewModelScope.launch {
            livroRepository.observarLivro(livroId).collect { livro ->
                _uiState.update { estado ->
                    estado.copy(
                        livroTitulo = livro?.titulo.orEmpty(),
                        livroAutor = livro?.autor.orEmpty(),
                        paginaAtual = livro?.paginaAtual ?: 0,
                        totalPaginas = livro?.totalPaginas ?: 0
                    )
                }
            }
        }
    }

    fun onTempoPrevistoChange(valor: String) {
        if (valor.all { it.isDigit() }) {
            _uiState.update { it.copy(tempoPrevistoMinutos = valor, erro = null) }
        }
    }

    fun onDataAlvoChange(valor: Long?) {
        _uiState.update { it.copy(dataAlvo = valor) }
    }

    fun onSalvarClick() {
        val estado = _uiState.value
        val minutos = estado.tempoPrevistoMinutos.toIntOrNull()
        if (minutos == null || minutos <= 0) {
            _uiState.update { it.copy(erro = "Informe um tempo previsto maior que zero") }
            return
        }

        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            metaRepository.salvar(
                Meta(
                    livroId = livroId,
                    tempoPrevistoMinutos = minutos,
                    dataAlvo = estado.dataAlvo
                )
            )
            _uiState.update { it.copy(salvando = false, metaSalva = true) }
        }
    }
}
