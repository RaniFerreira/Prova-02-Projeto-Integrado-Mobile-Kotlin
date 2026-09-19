package com.example.apptreino.data.repository

import com.example.apptreino.data.dao.CorridaDao
import com.example.apptreino.data.entity.CorridaEntity
import kotlinx.coroutines.flow.Flow

class CorridaRepository(private val corridaDao: CorridaDao) {

    fun listarCorridas(): Flow<List<CorridaEntity>> =
        corridaDao.listarCorridas()

    suspend fun registrarCorrida(distanciaKm: Double, tempoTotalSegundos: Int, paceSegundosPorKm: Int): Long =
        corridaDao.inserir(
            CorridaEntity(
                dataRegistro = System.currentTimeMillis(),
                distanciaKm = distanciaKm,
                tempoTotalSegundos = tempoTotalSegundos,
                paceSegundosPorKm = paceSegundosPorKm
            )
        )
}
