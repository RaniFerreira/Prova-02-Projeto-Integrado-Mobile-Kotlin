package com.example.rastreadorprazo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ObrigacaoDao {

    @Insert
    suspend fun inserir(obrigacao: Obrigacao): Long

    @Update
    suspend fun atualizar(obrigacao: Obrigacao)

    @Delete
    suspend fun excluir(obrigacao: Obrigacao)

    @Query("SELECT * FROM obrigacoes ORDER BY dataVencimento ASC, horaLembrete ASC")
    fun observarTodas(): Flow<List<Obrigacao>>

    @Query("SELECT * FROM obrigacoes WHERE id = :id")
    fun observarPorId(id: Long): Flow<Obrigacao?>

    @Query("SELECT * FROM obrigacoes WHERE id = :id")
    suspend fun buscarPorId(id: Long): Obrigacao?
}
