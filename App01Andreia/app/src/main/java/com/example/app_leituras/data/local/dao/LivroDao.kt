package com.example.app_leituras.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app_leituras.data.local.entity.LivroEntity
import com.example.app_leituras.data.local.entity.StatusLeitura
import kotlinx.coroutines.flow.Flow

@Dao
interface LivroDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(livro: LivroEntity): Long

    @Update
    suspend fun atualizar(livro: LivroEntity)

    @Delete
    suspend fun deletar(livro: LivroEntity)

    @Query("SELECT * FROM livros WHERE id = :livroId")
    suspend fun buscarPorId(livroId: Long): LivroEntity?

    @Query("SELECT * FROM livros WHERE id = :livroId")
    fun observarPorId(livroId: Long): Flow<LivroEntity?>

    @Query("SELECT * FROM livros ORDER BY data_criacao DESC")
    fun observarTodos(): Flow<List<LivroEntity>>

    @Query("SELECT * FROM livros WHERE status = :status ORDER BY data_criacao DESC")
    fun observarLivrosPorStatus(status: StatusLeitura): Flow<List<LivroEntity>>

    @Query(
        """
        SELECT * FROM livros
        WHERE (:genero IS NULL OR genero = :genero)
        AND (:status IS NULL OR status = :status)
        ORDER BY data_criacao DESC
        """
    )
    //Flow: sempre observando a lista (que vive mudando).
    fun observarLivrosFiltrados(genero: String?, status: StatusLeitura?): Flow<List<LivroEntity>>

    @Query("UPDATE livros SET paginaAtual = :paginaAtual WHERE id = :livroId")
    suspend fun atualizarPaginaAtual(livroId: Long, paginaAtual: Int)

    @Query("UPDATE livros SET status = :status WHERE id = :livroId")
    suspend fun atualizarStatus(livroId: Long, status: StatusLeitura)
}
