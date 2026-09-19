package com.example.app_leituras.ui.definirmeta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Meta
import com.example.app_leituras.domain.repository.MetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DefinirMetaUiState(
    val tempoPrevistoMinutos: String = "",
    val dataAlvo: Long? = null,
    val editando: Boolean = false,
    val erro: String? = null,
    val salvando: Boolean = false,
    val metaSalva: Boolean = false,
    val carregando: Boolean = true
)

class DefinirMetaViewModel(
    private val livroId: Long,
    private val metaRepository: MetaRepository
) : ViewModel() {

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

// Sem Hilt/Koin por enquanto: injeta o MetaRepository manualmente por construtor (mesmo padrão das outras telas).
class DefinirMetaViewModelFactory(
    private val livroId: Long,
    private val metaRepository: MetaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DefinirMetaViewModel(livroId, metaRepository) as T
    }
}
