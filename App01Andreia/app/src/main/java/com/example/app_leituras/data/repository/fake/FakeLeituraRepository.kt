package com.example.app_leituras.data.repository.fake

import com.example.app_leituras.domain.model.SessaoLeitura
import com.example.app_leituras.domain.repository.LeituraRepository
import com.example.app_leituras.domain.repository.LivroRepository
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Fonte de dados fake em memória. Depende da abstração [LivroRepository]
 * (não da fake concreta) só para ler o total de páginas do livro e
 * calcular o progresso — assim continua funcionando quando o Livro
 * passar a vir do Room.
 */
class FakeLeituraRepository(
    private val livroRepository: LivroRepository
) : LeituraRepository {

    private val proximoId = AtomicLong(3L)

    private val agora = System.currentTimeMillis()

    private val _sessoes = MutableStateFlow(
        listOf(
            // Sessões do livro id=3 ("1984"), que já está com paginaAtual = 120
            SessaoLeitura(
                id = 1L,
                livroId = 3L,
                paginaInicio = 0,
                paginaFim = 60,
                duracaoSegundos = 1_800L,
                dataHora = agora - HORA * 48
            ),
            SessaoLeitura(
                id = 2L,
                livroId = 3L,
                paginaInicio = 60,
                paginaFim = 120,
                duracaoSegundos = 2_100L,
                dataHora = agora - HORA * 24
            ),
            // Sessão do livro id=4 ("Sapiens"), que já está com paginaAtual = 200
            SessaoLeitura(
                id = 3L,
                livroId = 4L,
                paginaInicio = 0,
                paginaFim = 200,
                duracaoSegundos = 5_400L,
                dataHora = agora - HORA * 12
            )
        )
    )

    override fun observarSessoesPorLivro(livroId: Long): Flow<List<SessaoLeitura>> =
        _sessoes.map { lista -> lista.filter { it.livroId == livroId }.sortedByDescending { it.dataHora } }

    override suspend fun registrarSessao(sessao: SessaoLeitura): Long {
        val id = if (sessao.id != 0L) sessao.id else proximoId.getAndIncrement()
        val sessaoSalva = sessao.copy(id = id)
        _sessoes.update { it + sessaoSalva }
        return id
    }

    override fun calcularProgresso(livroId: Long): Flow<Float> =
        observarSessoesPorLivro(livroId).map { sessoes ->
            val livro = livroRepository.buscar(livroId) ?: return@map 0f
            if (livro.totalPaginas <= 0) return@map 0f
            val paginaAtual = sessoes.maxOfOrNull { it.paginaFim } ?: livro.paginaAtual
            (paginaAtual.toFloat() / livro.totalPaginas.toFloat()).coerceIn(0f, 1f)
        }

    override fun calcularTempoTotal(livroId: Long): Flow<Long> =
        observarSessoesPorLivro(livroId).map { sessoes -> sessoes.sumOf { it.duracaoSegundos } }

    private companion object {
        const val HORA = 3_600_000L
    }
}
