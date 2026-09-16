package com.example.app_leituras.domain.repository

import com.example.app_leituras.domain.model.Livro
import com.example.app_leituras.domain.model.StatusLeitura
import kotlinx.coroutines.flow.Flow

interface LivroRepository {

    fun observarLivrosPorStatus(status: StatusLeitura): Flow<List<Livro>>

    fun observarLivrosFiltrados(genero: String?, status: StatusLeitura?): Flow<List<Livro>>

    suspend fun buscar(id: Long): Livro?

    suspend fun salvar(livro: Livro): Long

    suspend fun atualizarStatus(id: Long, status: StatusLeitura)
}
