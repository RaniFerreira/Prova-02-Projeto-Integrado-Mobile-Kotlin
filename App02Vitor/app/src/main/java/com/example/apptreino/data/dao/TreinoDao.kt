package com.example.apptreino.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.apptreino.data.entity.TreinoEntity
import com.example.apptreino.data.relation.TreinoComExercicios
import kotlinx.coroutines.flow.Flow

@Dao
interface TreinoDao {

    @Insert
    suspend fun inserir(treino: TreinoEntity): Long

    @Transaction
    @Query("SELECT * FROM treinos ORDER BY treinoId ASC")
    fun listarTreinosComExercicios(): Flow<List<TreinoComExercicios>>

    @Transaction
    @Query("SELECT * FROM treinos WHERE treinoId = :treinoId")
    fun buscarTreinoComExercicios(treinoId: Long): Flow<TreinoComExercicios?>
}
