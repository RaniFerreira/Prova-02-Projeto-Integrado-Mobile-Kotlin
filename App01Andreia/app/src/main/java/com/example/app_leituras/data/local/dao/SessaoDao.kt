package com.example.app_leituras.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app_leituras.data.local.entity.SessaoLeituraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessaoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(sessao: SessaoLeituraEntity): Long

    @Delete
    suspend fun deletar(sessao: SessaoLeituraEntity)

    @Query("SELECT * FROM sessoes_leitura WHERE livroId = :livroId ORDER BY data_hora DESC")
    fun observarSessoesPorLivro(livroId: Long): Flow<List<SessaoLeituraEntity>>

    @Query("SELECT COALESCE(SUM(duracaoSegundos), 0) FROM sessoes_leitura WHERE livroId = :livroId")
    fun observarTempoTotalSegundos(livroId: Long): Flow<Long>

    @Query("SELECT MAX(paginaFim) FROM sessoes_leitura WHERE livroId = :livroId")
    suspend fun buscarUltimaPaginaLida(livroId: Long): Int?
}
