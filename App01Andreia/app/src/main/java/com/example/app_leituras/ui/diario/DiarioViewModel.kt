package com.example.app_leituras.ui.diario

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Nota
import com.example.app_leituras.domain.model.TipoNota
import com.example.app_leituras.domain.repository.LivroRepository
import com.example.app_leituras.domain.repository.NotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiarioUiState(
    val tituloLivro: String = "",
    val notas: List<Nota> = emptyList(),
    val formularioAberto: Boolean = false,
    val conteudo: String = "",
    val tipo: TipoNota = TipoNota.NOTA,
    val erro: String? = null,
    val salvando: Boolean = false,
    val carregando: Boolean = true
)

@HiltViewModel
class DiarioViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notaRepository: NotaRepository,
    livroRepository: LivroRepository
) : ViewModel() {

    // Argumento de rota (Navigation Compose), não dependência de módulo — vem do SavedStateHandle.
    private val livroId: Long = savedStateHandle.get<Long>("livroId") ?: 0L

    private val _uiState = MutableStateFlow(DiarioUiState())
    val uiState: StateFlow<DiarioUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            notaRepository.observarNotasPorLivro(livroId).collect { notas ->
                _uiState.update { it.copy(notas = notas, carregando = false) }
            }
        }
        // Título do livro só vem pelo livroId da rota (a tela não recebe mais isso como
        // parâmetro do caller), então é observado aqui pra alimentar a pílula do cabeçalho.
        viewModelScope.launch {
            livroRepository.observarLivro(livroId).collect { livro ->
                _uiState.update { it.copy(tituloLivro = livro?.titulo.orEmpty()) }
            }
        }
    }

    fun onAbrirNovaNota() {
        _uiState.update { it.copy(formularioAberto = true, conteudo = "", tipo = TipoNota.NOTA, erro = null) }
    }

    fun onFecharNovaNota() {
        _uiState.update { it.copy(formularioAberto = false, conteudo = "", erro = null) }
    }

    fun onConteudoChange(valor: String) {
        _uiState.update { it.copy(conteudo = valor, erro = null) }
    }

    fun onTipoChange(tipo: TipoNota) {
        _uiState.update { it.copy(tipo = tipo) }
    }

    fun onSalvarNota() {
        val estado = _uiState.value
        if (estado.conteudo.isBlank()) {
            _uiState.update { it.copy(erro = "Escreva algo antes de salvar") }
            return
        }

        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            notaRepository.adicionarNota(
                Nota(
                    livroId = livroId,
                    tipo = estado.tipo,
                    conteudo = estado.conteudo.trim(),
                    dataHora = System.currentTimeMillis()
                )
            )
            _uiState.update { it.copy(salvando = false, formularioAberto = false, conteudo = "") }
        }
    }
}
