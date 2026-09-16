package com.example.app_leituras.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "livros")
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
