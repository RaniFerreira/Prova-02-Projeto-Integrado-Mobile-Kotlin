package com.example.app_leituras.ui.sessaoleitura

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_leituras.domain.model.SessaoLeitura
import com.example.app_leituras.domain.repository.LeituraRepository
import com.example.app_leituras.domain.repository.LivroRepository
import java.util.Calendar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SessaoLeituraUiState(
    val tituloLivro: String = "",
    val totalPaginas: Int = 0,
    val paginaInicio: Int = 0,
    val paginaAtualInput: String = "",
    val tempoDecorridoSegundos: Long = 0L,
    val rodando: Boolean = false,
    val horaInicioSessao: Long? = null,
    val tempoLidoHojeSegundos: Long = 0L,
    val paginasLidasHoje: Int = 0,
    val erro: String? = null,
    val sessaoFinalizada: Boolean = false,
    val carregando: Boolean = true
)

class SessaoLeituraViewModel(
    private val livroId: Long,
    private val livroRepository: LivroRepository,
    private val leituraRepository: LeituraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessaoLeituraUiState())
    val uiState: StateFlow<SessaoLeituraUiState> = _uiState.asStateFlow()

    private var jobCronometro: Job? = null

    init {
        carregarEstadoInicial()
    }

    private fun carregarEstadoInicial() {
        viewModelScope.launch {
            val livro = livroRepository.buscar(livroId)
            val sessoes = leituraRepository.observarSessoesPorLivro(livroId).first()

            val paginaInicio = sessoes.maxOfOrNull { it.paginaFim } ?: livro?.paginaAtual ?: 0
            val agora = System.currentTimeMillis()
            val sessoesDeHoje = sessoes.filter { mesmoDia(it.dataHora, agora) }

            _uiState.update {
                it.copy(
                    tituloLivro = livro?.titulo.orEmpty(),
                    totalPaginas = livro?.totalPaginas ?: 0,
                    paginaInicio = paginaInicio,
                    tempoLidoHojeSegundos = sessoesDeHoje.sumOf { sessao -> sessao.duracaoSegundos },
                    paginasLidasHoje = sessoesDeHoje.sumOf { sessao -> (sessao.paginaFim - sessao.paginaInicio).coerceAtLeast(0) },
                    carregando = false
                )
            }
        }
    }

    fun onIniciarPausar() {
        val vaiRodar = !_uiState.value.rodando
        _uiState.update { estado ->
            estado.copy(
                rodando = vaiRodar,
                horaInicioSessao = estado.horaInicioSessao ?: if (vaiRodar) System.currentTimeMillis() else null
            )
        }
        if (vaiRodar) iniciarCronometro() else jobCronometro?.cancel()
    }

    private fun iniciarCronometro() {
        jobCronometro?.cancel()
        jobCronometro = viewModelScope.launch {
            while (isActive) {
                delay(1_000L)
                _uiState.update { it.copy(tempoDecorridoSegundos = it.tempoDecorridoSegundos + 1) }
            }
        }
    }

    fun onPaginaAtualChange(valor: String) {
        if (valor.all { it.isDigit() }) {
            _uiState.update { it.copy(paginaAtualInput = valor, erro = null) }
        }
    }

    fun onFinalizarSessao() {
        val estado = _uiState.value
        val paginaFim = estado.paginaAtualInput.toIntOrNull()
        if (paginaFim == null || paginaFim <= estado.paginaInicio) {
            _uiState.update { it.copy(erro = "Informe uma página maior que ${estado.paginaInicio}") }
            return
        }

        jobCronometro?.cancel()
        viewModelScope.launch {
            leituraRepository.registrarSessao(
                SessaoLeitura(
                    livroId = livroId,
                    paginaInicio = estado.paginaInicio,
                    paginaFim = paginaFim,
                    duracaoSegundos = estado.tempoDecorridoSegundos,
                    dataHora = System.currentTimeMillis()
                )
            )
            _uiState.update { it.copy(rodando = false, sessaoFinalizada = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        jobCronometro?.cancel()
    }

    private fun mesmoDia(instanteA: Long, instanteB: Long): Boolean {
        val calendarioA = Calendar.getInstance().apply { timeInMillis = instanteA }
        val calendarioB = Calendar.getInstance().apply { timeInMillis = instanteB }
        return calendarioA.get(Calendar.YEAR) == calendarioB.get(Calendar.YEAR) &&
            calendarioA.get(Calendar.DAY_OF_YEAR) == calendarioB.get(Calendar.DAY_OF_YEAR)
    }
}

// Sem Hilt/Koin por enquanto: injeta os repositórios manualmente por construtor (mesmo padrão das outras telas).
class SessaoLeituraViewModelFactory(
    private val livroId: Long,
    private val livroRepository: LivroRepository,
    private val leituraRepository: LeituraRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SessaoLeituraViewModel(livroId, livroRepository, leituraRepository) as T
    }
}
