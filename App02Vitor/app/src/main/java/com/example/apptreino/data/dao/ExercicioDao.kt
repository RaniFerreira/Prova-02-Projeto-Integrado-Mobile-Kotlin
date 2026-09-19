package com.example.apptreino.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.apptreino.data.entity.ExercicioEntity
import com.example.apptreino.data.relation.ExercicioComHistorico
import kotlinx.coroutines.flow.Flow

@Dao
interface ExercicioDao {

    @Insert
    suspend fun inserir(exercicio: ExercicioEntity): Long

    @Query("SELECT * FROM exercicios WHERE treinoId = :treinoId ORDER BY ordem ASC")
    fun listarExerciciosDoTreino(treinoId: Long): Flow<List<ExercicioEntity>>

    @Transaction
    @Query("SELECT * FROM exercicios WHERE exercicioId = :exercicioId")
    fun buscarExercicioComHistorico(exercicioId: Long): Flow<ExercicioComHistorico?>

    @Query("SELECT DISTINCT grupamentoMuscular FROM exercicios ORDER BY grupamentoMuscular ASC")
    fun listarGrupamentosDisponiveis(): Flow<List<String>>

    @Query(
        """
        SELECT * FROM exercicios
        WHERE (:termo = '' OR nome LIKE '%' || :termo || '%')
        AND (:grupamento = '' OR grupamentoMuscular = :grupamento)
        ORDER BY nome ASC
        """
    )
    fun buscarComFiltro(termo: String, grupamento: String): Flow<List<ExercicioEntity>>
}
