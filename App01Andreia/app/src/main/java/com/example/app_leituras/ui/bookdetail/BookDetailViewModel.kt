package com.example.app_leituras.ui.bookdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.Meta
import com.example.app_leituras.domain.model.Nota
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.repository.LeituraRepository
import com.example.app_leituras.domain.repository.LivroRepository
import com.example.app_leituras.domain.repository.MetaRepository
import com.example.app_leituras.domain.repository.NotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MAX_NOTAS_PREVIEW = 3

data class BookDetailUiState(
    val livro: Livro? = null,
    val progresso: Float = 0f,
    val tempoTotalSegundos: Long = 0L,
    val meta: Meta? = null,
    val notasRecentes: List<Nota> = emptyList(),
    val carregando: Boolean = true
)

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val livroRepository: LivroRepository,
    leituraRepository: LeituraRepository,
    metaRepository: MetaRepository,
    notaRepository: NotaRepository
) : ViewModel() {

    // "livroId" é argumento de rota (Navigation Compose), não uma dependência injetável por
    // módulo — por isso vem do SavedStateHandle, não do construtor direto.
    private val livroId: Long = savedStateHandle.get<Long>("livroId") ?: 0L

    val uiState: StateFlow<BookDetailUiState> = combine(
        livroRepository.observarLivro(livroId),
        leituraRepository.calcularProgresso(livroId),
        leituraRepository.calcularTempoTotal(livroId),
        metaRepository.observarPorLivro(livroId),
        notaRepository.observarNotasPorLivro(livroId).map { it.take(MAX_NOTAS_PREVIEW) }
    ) { livro, progresso, tempoTotalSegundos, meta, notasRecentes ->
        BookDetailUiState(
            livro = livro,
            progresso = progresso,
            tempoTotalSegundos = tempoTotalSegundos,
            meta = meta,
            notasRecentes = notasRecentes,
            carregando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = BookDetailUiState()
    )

    fun onTrocarStatus(novoStatus: StatusLeitura) {
        viewModelScope.launch {
            livroRepository.atualizarStatus(livroId, novoStatus)
        }
    }
}
