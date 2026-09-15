package com.example.rastreadorprazo.data

import kotlinx.coroutines.flow.Flow

class ObrigacaoRepository(private val dao: ObrigacaoDao) {

    fun observarTodas(): Flow<List<Obrigacao>> = dao.observarTodas()

    fun observarPorId(id: Long): Flow<Obrigacao?> = dao.observarPorId(id)

    suspend fun buscarPorId(id: Long): Obrigacao? = dao.buscarPorId(id)

    suspend fun salvar(obrigacao: Obrigacao): Long =
        if (obrigacao.id == 0L) {
            dao.inserir(obrigacao)
        } else {
            dao.atualizar(obrigacao)
            obrigacao.id
        }

    suspend fun atualizar(obrigacao: Obrigacao) = dao.atualizar(obrigacao)

    suspend fun excluir(obrigacao: Obrigacao) = dao.excluir(obrigacao)
}
