package com.example.app_leituras.data.repository

import com.example.app_leituras.data.local.dao.SessaoDao
import com.example.app_leituras.data.mapper.toDomain
import com.example.app_leituras.data.mapper.toEntity
import com.example.app_leituras.domain.model.SessaoLeitura
import com.example.app_leituras.domain.repository.LeituraRepository
import com.example.app_leituras.domain.repository.LivroRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Implementação real das sessões de leitura, apoiada no Room (SessaoDao).
 * Depende da abstração [LivroRepository] (não de uma implementação concreta) só para ler o
 * totalPaginas do livro ao calcular o progresso (Dependency Inversion).
 */
class LeituraRepositoryImpl @Inject constructor(
    private val sessaoDao: SessaoDao,
    private val livroRepository: LivroRepository
) : LeituraRepository {

    override fun observarSessoesPorLivro(livroId: Long): Flow<List<SessaoLeitura>> =
        sessaoDao.observarSessoesPorLivro(livroId).map { lista -> lista.map { it.toDomain() } }

    // Além de gravar a sessão, avança livros.paginaAtual: é esse campo que o card
    // "Em andamento" do Dashboard exibe (sem isso ele ficava parado na página do cadastro).
    // Chegou à última página: o livro passa para LIDO automaticamente.
    override suspend fun registrarSessao(sessao: SessaoLeitura): Long {
        val id = sessaoDao.inserir(sessao.toEntity())
        livroRepository.avancarPaginaAtual(sessao.livroId, sessao.paginaFim)
        marcarComoLidoSeTerminou(livroRepository, sessao)
        return id
    }

    // Progresso depende de DUAS fontes reativas independentes — sessões (Room) e o livro em si
    // (totalPaginas). combine() reemite sempre
    // que qualquer uma das duas mudar, sem precisar re-consultar a outra manualmente.
    override fun calcularProgresso(livroId: Long): Flow<Float> = combine(
        sessaoDao.observarSessoesPorLivro(livroId),
        livroRepository.observarLivro(livroId)
    ) { sessoes, livro ->
        if (livro == null || livro.totalPaginas <= 0) return@combine 0f
        val paginaAtual = sessoes.maxOfOrNull { it.paginaFim } ?: livro.paginaAtual
        (paginaAtual.toFloat() / livro.totalPaginas.toFloat()).coerceIn(0f, 1f)
    }

    override fun calcularTempoTotal(livroId: Long): Flow<Long> =
        sessaoDao.observarTempoTotalSegundos(livroId)
}
