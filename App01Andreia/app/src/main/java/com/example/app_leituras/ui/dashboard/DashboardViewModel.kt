package com.example.app_leituras.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.repository.LivroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

// Gêneros fixos extraídos dos livros de exemplo do AppDatabase — ainda não há uma fonte
// dinâmica (ex.: DISTINCT genero no Room) para alimentar o seletor de filtro.
val GENEROS_DISPONIVEIS = listOf("Ficção Científica", "Fantasia", "Ficção", "Não-ficção")

data class FiltroState(
    val generoSelecionado: String? = null,
    val statusSelecionado: StatusLeitura? = null
)

data class DashboardUiState(
    val emAndamento: List<Livro> = emptyList(),
    val queroLer: List<Livro> = emptyList(),
    val lido: List<Livro> = emptyList(),
    val filtro: FiltroState = FiltroState(),
    val generosDisponiveis: List<String> = GENEROS_DISPONIVEIS
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val livroRepository: LivroRepository
) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroState())

    // Toda vez que o filtro muda, flatMapLatest troca a coleta para uma nova consulta reativa
    // do repository, cancelando a anterior — o mesmo princípio do debounce+flatMapLatest da
    // busca (ver BuscarLivroViewModel), só que aqui a origem da mudança é um clique de chip,
    // não digitação, então não há debounce.
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = _filtro
        .flatMapLatest { filtro ->
            livroRepository.observarLivrosFiltrados(filtro.generoSelecionado, filtro.statusSelecionado)
                .map { livros -> filtro to livros }
        }
        .map { (filtro, livros) ->
            // O filtro vale para as 3 seções: reagrupa a mesma lista filtrada por status,
            // em vez de refazer 3 consultas separadas (uma por status) como antes.
            DashboardUiState(
                emAndamento = livros.filter { it.status == StatusLeitura.LENDO },
                queroLer = livros.filter { it.status == StatusLeitura.QUERO_LER },
                lido = livros.filter { it.status == StatusLeitura.LIDO },
                filtro = filtro
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DashboardUiState()
        )

    // Gênero e status são mutuamente exclusivos na UI (uma única fileira de chips, um
    // selecionado por vez — ver print do Figma), então selecionar um limpa o outro.
    fun onGeneroSelecionado(genero: String?) {
        _filtro.update { it.copy(generoSelecionado = genero, statusSelecionado = null) }
    }

    fun onStatusSelecionado(status: StatusLeitura?) {
        _filtro.update { it.copy(statusSelecionado = status, generoSelecionado = null) }
    }

    fun onLimparFiltros() {
        _filtro.value = FiltroState()
    }
}
