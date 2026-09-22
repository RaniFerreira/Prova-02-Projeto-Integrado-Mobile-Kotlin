package com.example.app_leituras.data.repository

import com.example.app_leituras.data.local.dao.MetaDao
import com.example.app_leituras.data.mapper.toDomain
import com.example.app_leituras.data.mapper.toEntity
import com.example.app_leituras.domain.model.Meta
import com.example.app_leituras.domain.repository.MetaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementação real da meta de leitura, apoiada no Room (MetaDao).
 *
 * O "salvar" não precisa checar manualmente se já existe meta pra decidir entre inserir/
 * atualizar: MetaDao.inserir já usa @Insert(onConflict = OnConflictStrategy.REPLACE), e a
 * MetaEntity já tem um índice único em livroId (1 meta por livro). Então inserir uma meta nova
 * pra um livroId que já tem meta faz o SQLite substituir a linha antiga automaticamente — é
 * exatamente o upsert-por-livroId que a regra de negócio pede, sem lógica extra aqui.
 */
class MetaRepositoryImpl @Inject constructor(
    private val metaDao: MetaDao
) : MetaRepository {

    override fun observarPorLivro(livroId: Long): Flow<Meta?> =
        metaDao.observarPorLivro(livroId).map { entity -> entity?.toDomain() }

    override suspend fun salvar(meta: Meta) {
        metaDao.inserir(meta.toEntity())
    }
}
