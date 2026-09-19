package com.example.apptreino.ui.screens.detalhe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptreino.data.relation.TreinoComExercicios
import com.example.apptreino.data.repository.ExercicioRepository
import com.example.apptreino.data.repository.TreinoRepository
import com.example.apptreino.ui.state.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetalheTreinoViewModel(
    private val treinoRepository: TreinoRepository,
    private val exercicioRepository: ExercicioRepository,
    val treinoId: Long
) : ViewModel() {

    val uiState: StateFlow<UiState<TreinoComExercicios>> =
        treinoRepository.buscarTreinoComExercicios(treinoId)
            .map<TreinoComExercicios?, UiState<TreinoComExercicios>> { treino ->
                if (treino == null) UiState.Error("Ficha de treino não encontrada") else UiState.Success(treino)
            }
            .catch { erro -> emit(UiState.Error(erro.message ?: "Erro ao carregar os exercicios")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun adicionarExercicio(nome: String, grupamentoMuscular: String) {
        if (nome.isBlank() || grupamentoMuscular.isBlank()) return
        val ordemAtual = (uiState.value as? UiState.Success)?.data?.exercicios?.size ?: 0
        viewModelScope.launch {
            exercicioRepository.inserirExercicio(
                treinoId = treinoId,
                nome = nome.trim(),
                grupamentoMuscular = grupamentoMuscular.trim(),
                ordem = ordemAtual + 1
            )
        }
    }
}
