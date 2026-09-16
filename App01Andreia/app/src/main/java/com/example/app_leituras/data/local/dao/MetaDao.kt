package com.example.app_leituras.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app_leituras.data.local.entity.MetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(meta: MetaEntity): Long

    @Update
    suspend fun atualizar(meta: MetaEntity)

    @Delete
    suspend fun deletar(meta: MetaEntity)

    @Query("SELECT * FROM metas WHERE livroId = :livroId")
    suspend fun buscarPorLivro(livroId: Long): MetaEntity?

    @Query("SELECT * FROM metas WHERE livroId = :livroId")
    fun observarPorLivro(livroId: Long): Flow<MetaEntity?>
}
