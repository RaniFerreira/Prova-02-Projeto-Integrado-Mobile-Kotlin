package com.example.app_leituras.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "metas",
    foreignKeys = [
        ForeignKey(
            entity = LivroEntity::class,
            parentColumns = ["id"],
            childColumns = ["livroId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("livroId", unique = true)]
)
data class MetaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val livroId: Long,
    val tempoPrevistoMinutos: Int,
    @ColumnInfo(name = "data_alvo")
    val dataAlvo: Long?
)
