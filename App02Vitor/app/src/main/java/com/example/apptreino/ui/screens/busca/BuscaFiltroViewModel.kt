package com.example.apptreino.ui.screens.busca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptreino.data.entity.ExercicioEntity
import com.example.apptreino.data.repository.ExercicioRepository
import com.example.apptreino.ui.state.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

const val GRUPAMENTO_TODOS = ""

@OptIn(FlowPreview::class)
class BuscaFiltroViewModel(
    private val exercicioRepository: ExercicioRepository
) : ViewModel() {

    private val _termoBusca = MutableStateFlow("")
    val termoBusca: StateFlow<String> = _termoBusca.asStateFlow()

    private val _grupamentoSelecionado = MutableStateFlow(GRUPAMENTO_TODOS)
    val grupamentoSelecionado: StateFlow<String> = _grupamentoSelecionado.asStateFlow()

    val grupamentosDisponiveis: StateFlow<List<String>> =
        exercicioRepository.listarGrupamentosDisponiveis()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val resultado: StateFlow<UiState<List<ExercicioEntity>>> =
        combine(
            _termoBusca.debounce(300),
            _grupamentoSelecionado
        ) { termo, grupamento -> termo to grupamento }
            .flatMapLatest { (termo, grupamento) ->
                exercicioRepository.buscarComFiltro(termo.trim(), grupamento)
            }
            .map<List<ExercicioEntity>, UiState<List<ExercicioEntity>>> { exercicios ->
                if (exercicios.isEmpty()) UiState.Empty else UiState.Success(exercicios)
            }
            .catch { erro -> emit(UiState.Error(erro.message ?: "Erro ao buscar exercicios")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun onTermoBuscaChange(termo: String) {
        _termoBusca.value = termo
    }

    fun onGrupamentoSelecionado(grupamento: String) {
        _grupamentoSelecionado.value = grupamento
    }
}
