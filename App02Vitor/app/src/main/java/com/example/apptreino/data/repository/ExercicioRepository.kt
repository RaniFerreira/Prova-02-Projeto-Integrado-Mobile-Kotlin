package com.example.apptreino.data.repository

import com.example.apptreino.data.dao.ExercicioDao
import com.example.apptreino.data.entity.ExercicioEntity
import com.example.apptreino.data.relation.ExercicioComHistorico
import kotlinx.coroutines.flow.Flow

class ExercicioRepository(private val exercicioDao: ExercicioDao) {

    fun listarExerciciosDoTreino(treinoId: Long): Flow<List<ExercicioEntity>> =
        exercicioDao.listarExerciciosDoTreino(treinoId)

    fun buscarExercicioComHistorico(exercicioId: Long): Flow<ExercicioComHistorico?> =
        exercicioDao.buscarExercicioComHistorico(exercicioId)

    fun listarGrupamentosDisponiveis(): Flow<List<String>> =
        exercicioDao.listarGrupamentosDisponiveis()

    fun buscarComFiltro(termo: String, grupamento: String): Flow<List<ExercicioEntity>> =
        exercicioDao.buscarComFiltro(termo, grupamento)

    suspend fun inserirExercicio(treinoId: Long, nome: String, grupamentoMuscular: String, ordem: Int): Long =
        exercicioDao.inserir(
            ExercicioEntity(
                treinoId = treinoId,
                nome = nome,
                grupamentoMuscular = grupamentoMuscular,
                ordem = ordem
            )
        )
}
