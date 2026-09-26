package com.example.app_leituras.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.app_leituras.data.local.dao.LivroDao
import com.example.app_leituras.data.mapper.toDomain
import com.example.app_leituras.data.mapper.toEntity
import com.example.app_leituras.data.remote.GoogleBooksRepository
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.ResultadoBusca
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.model.chaveTituloAutor
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

    // Se o índice único de googleBooksId barrar a inserção (ex.: salvar concorrente do mesmo
    // livro da API), devolve o id do livro que já está no catálogo em vez de derrubar o app.
    override suspend fun salvar(livro: Livro): Long = try {
        livroDao.inserir(livro.toEntity())
    } catch (e: SQLiteConstraintException) {
        livro.googleBooksId?.let { livroDao.buscarPorGoogleBooksId(it)?.id } ?: throw e
    }

    override suspend fun buscarExistente(livro: Livro): Livro? {
        livro.googleBooksId?.let { id ->
            livroDao.buscarPorGoogleBooksId(id)?.let { return it.toDomain() }
        }
        val chave = chaveTituloAutor(livro.titulo, livro.autor)
        return livroDao.listarTodos()
            .firstOrNull { chaveTituloAutor(it.titulo, it.autor) == chave }
            ?.toDomain()
    }

    override suspend fun atualizarStatus(id: Long, status: StatusLeitura) =
        livroDao.atualizarStatus(id, status.toEntity())

    override suspend fun avancarPaginaAtual(id: Long, paginaAtual: Int) =
        livroDao.avancarPaginaAtual(id, paginaAtual)

    // Igual ao FakeLivroRepository: a busca por API não depende do Room, então delega direto
    // pro GoogleBooksRepository (chamada de rede real via Retrofit) — ver passo 10.
    override fun buscarNaApi(query: String): Flow<ResultadoBusca> = googleBooksRepository.buscar(query)
}
