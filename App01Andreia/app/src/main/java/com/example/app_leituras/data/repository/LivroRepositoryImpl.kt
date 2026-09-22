package com.example.app_leituras.data.repository

import com.example.app_leituras.data.local.dao.LivroDao
import com.example.app_leituras.data.mapper.toDomain
import com.example.app_leituras.data.mapper.toEntity
import com.example.app_leituras.data.remote.GoogleBooksRepository
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.ResultadoBusca
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.repository.LivroRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementação real do catálogo, apoiada no Room (LivroDao) em vez do
 * MutableStateFlow em memória do FakeLivroRepository.
 */
class LivroRepositoryImpl @Inject constructor(
    private val livroDao: LivroDao,
    private val googleBooksRepository: GoogleBooksRepository
) : LivroRepository {

    override fun observarLivrosPorStatus(status: StatusLeitura): Flow<List<Livro>> =
        livroDao.observarLivrosPorStatus(status.toEntity()).map { lista -> lista.map { it.toDomain() } }

    override fun observarLivrosFiltrados(genero: String?, status: StatusLeitura?): Flow<List<Livro>> =
        livroDao.observarLivrosFiltrados(genero, status?.toEntity()).map { lista -> lista.map { it.toDomain() } }

    override fun observarLivro(id: Long): Flow<Livro?> =
        livroDao.observarPorId(id).map { entidade -> entidade?.toDomain() }

    override suspend fun buscar(id: Long): Livro? = livroDao.buscarPorId(id)?.toDomain()

    override suspend fun salvar(livro: Livro): Long = livroDao.inserir(livro.toEntity())

    override suspend fun atualizarStatus(id: Long, status: StatusLeitura) =
        livroDao.atualizarStatus(id, status.toEntity())

    // Igual ao FakeLivroRepository: a busca por API não depende do Room, então delega direto
    // pro GoogleBooksRepository (chamada de rede real via Retrofit) — ver passo 10.
    override fun buscarNaApi(query: String): Flow<ResultadoBusca> = googleBooksRepository.buscar(query)
}
