package com.example.app_leituras.domain.repository

import com.example.app_leituras.domain.model.SessaoLeitura
import kotlinx.coroutines.flow.Flow

interface LeituraRepository {

    fun observarSessoesPorLivro(livroId: Long): Flow<List<SessaoLeitura>>

    suspend fun registrarSessao(sessao: SessaoLeitura): Long

    /** Progresso de 0f a 1f, recalculado a cada nova sessão registrada. */
    fun calcularProgresso(livroId: Long): Flow<Float>

    /** Soma da duração (em segundos) de todas as sessões do livro. */
    fun calcularTempoTotal(livroId: Long): Flow<Long>
}
