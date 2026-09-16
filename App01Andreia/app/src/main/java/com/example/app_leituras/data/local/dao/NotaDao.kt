package com.example.app_leituras.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app_leituras.data.local.entity.NotaEntity
import com.example.app_leituras.data.local.entity.TipoNota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(nota: NotaEntity): Long

    @Update
    suspend fun atualizar(nota: NotaEntity)

    @Delete
    suspend fun deletar(nota: NotaEntity)

    @Query("SELECT * FROM notas WHERE livroId = :livroId ORDER BY data_hora DESC")
    fun observarNotasPorLivro(livroId: Long): Flow<List<NotaEntity>>

    @Query("SELECT * FROM notas WHERE livroId = :livroId AND tipo = :tipo ORDER BY data_hora DESC")
    fun observarNotasPorLivroETipo(livroId: Long, tipo: TipoNota): Flow<List<NotaEntity>>
}
