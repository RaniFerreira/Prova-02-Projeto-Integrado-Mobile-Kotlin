@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.app_leituras.ui.buscarlivro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.ResultadoBusca
import com.example.app_leituras.domain.repository.LivroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuscarLivroUiState(
    val query: String = "",
    val resultado: ResultadoBusca = ResultadoBusca.Vazio
)

@HiltViewModel
class BuscarLivroViewModel @Inject constructor(
    private val livroRepository: LivroRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")

    private val _uiState = MutableStateFlow(BuscarLivroUiState())
    val uiState: StateFlow<BuscarLivroUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(400)
                .distinctUntilChanged()
                .flatMapLatest { query -> livroRepository.buscarNaApi(query) }
                .collect { resultado ->
                    _uiState.update { it.copy(resultado = resultado) }
                }
        }
    }

    fun onQueryChange(valor: String) {
        _uiState.update { it.copy(query = valor) }
        _query.value = valor
    }
}
