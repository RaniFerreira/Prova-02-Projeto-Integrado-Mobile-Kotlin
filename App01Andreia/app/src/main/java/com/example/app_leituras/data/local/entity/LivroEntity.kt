package com.example.app_leituras.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// googleBooksId único: o mesmo livro da API não pode entrar duas vezes no catálogo.
// (SQLite permite vários NULL num índice único, então cadastros manuais não são afetados.)
@Entity(
    tableName = "livros",
    indices = [Index(value = ["googleBooksId"], unique = true)]
)
data class LivroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val titulo: String,
    val autor: String,
    val totalPaginas: Int,
    val genero: String,
    val capaUrl: String?,
    val status: StatusLeitura,
    val paginaAtual: Int = 0,
    val googleBooksId: String?,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long
)
