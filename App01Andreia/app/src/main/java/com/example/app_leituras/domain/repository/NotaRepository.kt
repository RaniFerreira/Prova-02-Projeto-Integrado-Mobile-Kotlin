package com.example.app_leituras.domain.repository

import com.example.app_leituras.domain.model.Nota
import kotlinx.coroutines.flow.Flow

interface NotaRepository {

    fun observarNotasPorLivro(livroId: Long): Flow<List<Nota>>

    suspend fun adicionarNota(nota: Nota): Long
}
