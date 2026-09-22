package com.example.app_leituras.data.repository

import com.example.app_leituras.data.local.dao.NotaDao
import com.example.app_leituras.data.mapper.toDomain
import com.example.app_leituras.data.mapper.toEntity
import com.example.app_leituras.domain.model.Nota
import com.example.app_leituras.domain.repository.NotaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Implementação real das notas/insights/citações, apoiada no Room (NotaDao). */
class NotaRepositoryImpl @Inject constructor(
    private val notaDao: NotaDao
) : NotaRepository {

    override fun observarNotasPorLivro(livroId: Long): Flow<List<Nota>> =
        notaDao.observarNotasPorLivro(livroId).map { lista -> lista.map { it.toDomain() } }

    override suspend fun adicionarNota(nota: Nota): Long = notaDao.inserir(nota.toEntity())
}
