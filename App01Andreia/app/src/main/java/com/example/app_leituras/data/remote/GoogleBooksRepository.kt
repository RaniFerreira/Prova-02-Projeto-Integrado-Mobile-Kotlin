package com.example.app_leituras.data.remote

import com.example.app_leituras.data.mapper.paraDominio
import com.example.app_leituras.domain.model.ResultadoBusca
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

/**
 * Implementação real (rede de verdade) da busca na Google Books API.
 * Fica separada do LivroRepositoryImpl porque é a única parte do app que
 * sai do escopo local: não depende do Room.
 *
 * Sem valor default no "api": um construtor @Inject com parâmetro default gera, para o
 * Kotlin/Java, dois construtores visíveis (um com o parâmetro, outro sintético sem ele) —
 * o Dagger/Hilt rejeita isso ("may only contain one injected constructor"). Fora do Hilt
 * (ex.: instanciação manual), passe RetrofitConfig.googleBooksApi explicitamente.
 */
class GoogleBooksRepository @Inject constructor(
    private val api: GoogleBooksApi
) {

    fun buscar(query: String): Flow<ResultadoBusca> = flow {
        emit(ResultadoBusca.Carregando)

        if (query.isBlank()) {
            emit(ResultadoBusca.Vazio)
            return@flow
        }

        try {
            val resposta = api.buscarLivros(query)
            val livros = resposta.items.orEmpty().mapNotNull { it.paraDominio() }
            emit(if (livros.isEmpty()) ResultadoBusca.Vazio else ResultadoBusca.Sucesso(livros))
        } catch (e: IOException) {
            emit(ResultadoBusca.Erro("Sem conexão com a internet. Verifique sua rede e tente novamente."))
        } catch (e: HttpException) {
            emit(ResultadoBusca.Erro("A busca falhou (erro ${e.code()}). Tente novamente."))
        }
    }.flowOn(Dispatchers.IO)
}
