package com.example.app_leituras.ui.diario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Nota
import com.example.app_leituras.domain.model.TipoNota
import com.example.app_leituras.domain.repository.NotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiarioUiState(
    val notas: List<Nota> = emptyList(),
    val formularioAberto: Boolean = false,
    val conteudo: String = "",
    val tipo: TipoNota = TipoNota.NOTA,
    val erro: String? = null,
    val salvando: Boolean = false,
    val carregando: Boolean = true
)

class DiarioViewModel(
    private val livroId: Long,
    private val notaRepository: NotaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiarioUiState())
    val uiState: StateFlow<DiarioUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            notaRepository.observarNotasPorLivro(livroId).collect { notas ->
                _uiState.update { it.copy(notas = notas, carregando = false) }
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

// Sem Hilt/Koin por enquanto: injeta o NotaRepository manualmente por construtor (mesmo padrão das outras telas).
class DiarioViewModelFactory(
    private val livroId: Long,
    private val notaRepository: NotaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiarioViewModel(livroId, notaRepository) as T
    }
}
