package com.example.apptreino.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptreino.data.relation.TreinoComExercicios
import com.example.apptreino.data.repository.TreinoRepository
import com.example.apptreino.ui.state.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeTreinosViewModel(
    private val treinoRepository: TreinoRepository
) : ViewModel() {

    val uiState: StateFlow<UiState<List<TreinoComExercicios>>> =
        treinoRepository.listarTreinosComExercicios()
            .map<List<TreinoComExercicios>, UiState<List<TreinoComExercicios>>> { fichas ->
                if (fichas.isEmpty()) UiState.Empty else UiState.Success(fichas)
            }
            .catch { erro -> emit(UiState.Error(erro.message ?: "Erro ao carregar as fichas de treino")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun criarFicha(nome: String) {
        if (nome.isBlank()) return
        viewModelScope.launch {
            treinoRepository.inserirTreino(nome.trim())
        }
    }
}
