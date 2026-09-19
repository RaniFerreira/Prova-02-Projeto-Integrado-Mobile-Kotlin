package com.example.app_leituras.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.Meta
import com.example.app_leituras.domain.model.Nota
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.repository.LeituraRepository
import com.example.app_leituras.domain.repository.LivroRepository
import com.example.app_leituras.domain.repository.MetaRepository
import com.example.app_leituras.domain.repository.NotaRepository
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

class BookDetailViewModel(
    private val livroId: Long,
    private val livroRepository: LivroRepository,
    leituraRepository: LeituraRepository,
    metaRepository: MetaRepository,
    notaRepository: NotaRepository
) : ViewModel() {

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

// Sem Hilt/Koin por enquanto: injeta os repositórios manualmente por construtor (mesmo padrão do Dashboard).
class BookDetailViewModelFactory(
    private val livroId: Long,
    private val livroRepository: LivroRepository,
    private val leituraRepository: LeituraRepository,
    private val metaRepository: MetaRepository,
    private val notaRepository: NotaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookDetailViewModel(
            livroId = livroId,
            livroRepository = livroRepository,
            leituraRepository = leituraRepository,
            metaRepository = metaRepository,
            notaRepository = notaRepository
        ) as T
    }
}
