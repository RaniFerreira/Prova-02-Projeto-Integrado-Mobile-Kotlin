package com.example.apptreino.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.apptreino.data.entity.CorridaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CorridaDao {

    @Insert
    suspend fun inserir(corrida: CorridaEntity): Long

    @Query("SELECT * FROM corridas ORDER BY dataRegistro DESC")
    fun listarCorridas(): Flow<List<CorridaEntity>>
}
