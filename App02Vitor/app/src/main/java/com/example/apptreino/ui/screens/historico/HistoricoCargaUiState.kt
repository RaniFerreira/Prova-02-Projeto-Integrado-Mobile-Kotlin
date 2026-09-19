package com.example.apptreino.ui.screens.historico

data class HistoricoCargaUiState(
    val nomeExercicio: String,
    val historico: List<CargaHistoricoUiModel>
)

sealed class SalvarState {
    data object Idle : SalvarState()
    data object Loading : SalvarState()
    data object Success : SalvarState()
    data class Error(val mensagem: String) : SalvarState()
}
