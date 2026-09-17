package com.example.app_leituras.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.repository.LivroRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val emAndamento: List<Livro> = emptyList(),
    val queroLer: List<Livro> = emptyList(),
    val lido: List<Livro> = emptyList()
)

class DashboardViewModel(
    livroRepository: LivroRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        livroRepository.observarLivrosPorStatus(StatusLeitura.LENDO),
        livroRepository.observarLivrosPorStatus(StatusLeitura.QUERO_LER),
        livroRepository.observarLivrosPorStatus(StatusLeitura.LIDO)
    ) { emAndamento, queroLer, lido ->
        DashboardUiState(emAndamento = emAndamento, queroLer = queroLer, lido = lido)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DashboardUiState()
    )
}

// Sem Hilt/Koin por enquanto: injeta o LivroRepository manualmente por construtor.
class DashboardViewModelFactory(
    private val livroRepository: LivroRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(livroRepository) as T
    }
}
