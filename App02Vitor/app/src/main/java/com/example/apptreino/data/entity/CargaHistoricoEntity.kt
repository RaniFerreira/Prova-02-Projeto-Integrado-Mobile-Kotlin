package com.example.apptreino.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "carga_historico",
    foreignKeys = [
        ForeignKey(
            entity = ExercicioEntity::class,
            parentColumns = ["exercicioId"],
            childColumns = ["exercicioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exercicioId")]
)
data class CargaHistoricoEntity(
    @PrimaryKey(autoGenerate = true)
    val historicoId: Long = 0,
    val exercicioId: Long,
    val dataRegistro: Long,
    val cargaKg: Double,
    val series: Int,
    val repeticoes: Int
)
