package com.example.apptreino.data.repository

import com.example.apptreino.data.dao.TreinoDao
import com.example.apptreino.data.entity.TreinoEntity
import com.example.apptreino.data.relation.TreinoComExercicios
import kotlinx.coroutines.flow.Flow

class TreinoRepository(private val treinoDao: TreinoDao) {

    fun listarTreinosComExercicios(): Flow<List<TreinoComExercicios>> =
        treinoDao.listarTreinosComExercicios()

    fun buscarTreinoComExercicios(treinoId: Long): Flow<TreinoComExercicios?> =
        treinoDao.buscarTreinoComExercicios(treinoId)

    suspend fun inserirTreino(nome: String): Long =
        treinoDao.inserir(TreinoEntity(nome = nome))
}
