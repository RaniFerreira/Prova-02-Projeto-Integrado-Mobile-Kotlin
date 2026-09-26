package com.example.app_leituras.data.repository.fake

import com.example.app_leituras.data.remote.GoogleBooksRepository
import com.example.app_leituras.data.remote.RetrofitConfig
import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.ResultadoBusca
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.model.chaveTituloAutor
import com.example.app_leituras.domain.repository.LivroRepository
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Fonte de dados fake em memória, para validar o fluxo reativo
 * (Flow -> ViewModel -> UI) antes de plugar o Room de verdade.
 *
 * A busca por API (buscarNaApi) é a exceção: em vez de simular resultados
 * fake, ela delega para o [GoogleBooksRepository], que faz a chamada de
 * rede de verdade. Essa busca não depende do Room nem dos livros fake
 * acima, então não há motivo pra fingir aqui — e assim a tela de busca já
 * é validada com dados reais da API desde já.
 */
class FakeLivroRepository(
    private val googleBooksRepository: GoogleBooksRepository = GoogleBooksRepository(RetrofitConfig.googleBooksApi)
) : LivroRepository {

    private val proximoId = AtomicLong(7L)

    private val agora = System.currentTimeMillis()

    private val _livros = MutableStateFlow(
        listOf(
            Livro(
                id = 1L,
                titulo = "Duna",
                autor = "Frank Herbert",
                totalPaginas = 688,
                genero = "Ficção Científica",
                capaUrl = null,
                status = StatusLeitura.QUERO_LER,
                paginaAtual = 0,
                googleBooksId = null,
                dataCriacao = agora - DIA * 1
            ),
            Livro(
                id = 2L,
                titulo = "O Nome do Vento",
                autor = "Patrick Rothfuss",
                totalPaginas = 662,
                genero = "Fantasia",
                capaUrl = null,
                status = StatusLeitura.QUERO_LER,
                paginaAtual = 0,
                googleBooksId = null,
                dataCriacao = agora - DIA * 2
            ),
            Livro(
                id = 3L,
                titulo = "1984",
                autor = "George Orwell",
                totalPaginas = 328,
                genero = "Ficção",
                capaUrl = null,
                status = StatusLeitura.LENDO,
                paginaAtual = 120,
                googleBooksId = null,
                dataCriacao = agora - DIA * 3
            ),
            Livro(
                id = 4L,
                titulo = "Sapiens",
                autor = "Yuval Noah Harari",
                totalPaginas = 464,
                genero = "Não-ficção",
                capaUrl = null,
                status = StatusLeitura.LENDO,
                paginaAtual = 200,
                googleBooksId = null,
                dataCriacao = agora - DIA * 4
            ),
            Livro(
                id = 5L,
                titulo = "O Hobbit",
                autor = "J.R.R. Tolkien",
                totalPaginas = 310,
                genero = "Fantasia",
                capaUrl = null,
                status = StatusLeitura.LIDO,
                paginaAtual = 310,
                googleBooksId = null,
                dataCriacao = agora - DIA * 5
            ),
            Livro(
                id = 6L,
                titulo = "A Revolução dos Bichos",
                autor = "George Orwell",
                totalPaginas = 152,
                genero = "Ficção",
                capaUrl = null,
                status = StatusLeitura.LIDO,
                paginaAtual = 152,
                googleBooksId = null,
                dataCriacao = agora - DIA * 6
            )
        )
    )

    val livros = _livros.asStateFlow()

    override fun observarLivrosPorStatus(status: StatusLeitura): Flow<List<Livro>> =
        _livros.map { lista -> lista.filter { it.status == status } }

    override fun observarLivrosFiltrados(genero: String?, status: StatusLeitura?): Flow<List<Livro>> =
        _livros.map { lista ->
            lista.filter { livro ->
                (genero == null || livro.genero == genero) &&
                    (status == null || livro.status == status)
            }
        }

    override fun observarLivro(id: Long): Flow<Livro?> =
        _livros.map { lista -> lista.firstOrNull { it.id == id } }

    override suspend fun buscar(id: Long): Livro? = _livros.value.firstOrNull { it.id == id }

    override suspend fun salvar(livro: Livro): Long {
        val id = if (livro.id != 0L) livro.id else proximoId.getAndIncrement()
        val livroSalvo = livro.copy(id = id)
        _livros.update { lista ->
            if (lista.any { it.id == id }) {
                lista.map { if (it.id == id) livroSalvo else it }
            } else {
                lista + livroSalvo
            }
        }
        return id
    }

    override suspend fun buscarExistente(livro: Livro): Livro? {
        val chave = chaveTituloAutor(livro.titulo, livro.autor)
        return _livros.value.firstOrNull { existente ->
            (livro.googleBooksId != null && existente.googleBooksId == livro.googleBooksId) ||
                chaveTituloAutor(existente.titulo, existente.autor) == chave
        }
    }

    override suspend fun atualizarStatus(id: Long, status: StatusLeitura) {
        _livros.update { lista ->
            lista.map { if (it.id == id) it.copy(status = status) else it }
        }
    }

    override suspend fun avancarPaginaAtual(id: Long, paginaAtual: Int) {
        _livros.update { lista ->
            lista.map { if (it.id == id) it.copy(paginaAtual = maxOf(it.paginaAtual, paginaAtual)) else it }
        }
    }

    override fun buscarNaApi(query: String): Flow<ResultadoBusca> =
        googleBooksRepository.buscar(query)

    private companion object {
        const val DIA = 86_400_000L
    }
}
