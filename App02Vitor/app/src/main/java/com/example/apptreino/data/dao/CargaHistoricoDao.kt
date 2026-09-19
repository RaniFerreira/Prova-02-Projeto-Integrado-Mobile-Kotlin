package com.example.apptreino.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.apptreino.data.entity.CargaHistoricoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CargaHistoricoDao {

    @Insert
    suspend fun inserir(historico: CargaHistoricoEntity): Long

    @Query("SELECT * FROM carga_historico WHERE exercicioId = :exercicioId ORDER BY dataRegistro DESC")
    fun listarHistoricoDoExercicio(exercicioId: Long): Flow<List<CargaHistoricoEntity>>
}
