package com.example.app_leituras.domain.repository

import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.ResultadoBusca
import com.example.app_leituras.domain.model.StatusLeitura
import kotlinx.coroutines.flow.Flow

interface LivroRepository {

    fun observarLivrosPorStatus(status: StatusLeitura): Flow<List<Livro>>

    fun observarLivrosFiltrados(genero: String?, status: StatusLeitura?): Flow<List<Livro>>

    /** Observa um único livro (ex.: Book Detail), refletindo mudanças de status em tempo real. */
    fun observarLivro(id: Long): Flow<Livro?>

    suspend fun buscar(id: Long): Livro?

    suspend fun salvar(livro: Livro): Long

    /**
     * Livro já cadastrado que corresponde a [livro] — mesmo googleBooksId ou, sem ele, mesmo
     * título + autor (ver chaveTituloAutor). Evita o mesmo livro em duas seções do Dashboard.
     */
    suspend fun buscarExistente(livro: Livro): Livro?

    suspend fun atualizarStatus(id: Long, status: StatusLeitura)

    /** Avança a página atual do livro (nunca retrocede) — chamado ao registrar uma sessão. */
    suspend fun avancarPaginaAtual(id: Long, paginaAtual: Int)

    /** Busca livros na Google Books API. Emite Carregando -> Sucesso/Vazio/Erro. */
    fun buscarNaApi(query: String): Flow<ResultadoBusca>
}
