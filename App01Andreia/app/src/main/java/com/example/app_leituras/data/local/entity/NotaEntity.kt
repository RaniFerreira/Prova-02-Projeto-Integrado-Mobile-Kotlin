package com.example.app_leituras.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notas",
    foreignKeys = [
        ForeignKey(
            entity = LivroEntity::class,
            parentColumns = ["id"],
            childColumns = ["livroId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("livroId")]
)
data class NotaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val livroId: Long,
    val tipo: TipoNota,
    val conteudo: String,
    @ColumnInfo(name = "data_hora")
    val dataHora: Long
)
