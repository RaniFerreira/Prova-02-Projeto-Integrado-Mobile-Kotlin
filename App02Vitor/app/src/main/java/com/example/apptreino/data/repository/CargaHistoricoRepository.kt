package com.example.apptreino.data.repository

import com.example.apptreino.data.dao.CargaHistoricoDao
import com.example.apptreino.data.entity.CargaHistoricoEntity
import kotlinx.coroutines.flow.Flow

class CargaHistoricoRepository(private val cargaHistoricoDao: CargaHistoricoDao) {

    fun listarHistoricoDoExercicio(exercicioId: Long): Flow<List<CargaHistoricoEntity>> =
        cargaHistoricoDao.listarHistoricoDoExercicio(exercicioId)

    suspend fun registrarCarga(exercicioId: Long, cargaKg: Double, series: Int, repeticoes: Int): Long =
        cargaHistoricoDao.inserir(
            CargaHistoricoEntity(
                exercicioId = exercicioId,
                dataRegistro = System.currentTimeMillis(),
                cargaKg = cargaKg,
                series = series,
                repeticoes = repeticoes
            )
        )
}
