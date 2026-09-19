package com.example.apptreino.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercicios",
    foreignKeys = [
        ForeignKey(
            entity = TreinoEntity::class,
            parentColumns = ["treinoId"],
            childColumns = ["treinoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("treinoId")]
)
data class ExercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val exercicioId: Long = 0,
    val treinoId: Long,
    val nome: String,
    val grupamentoMuscular: String,
    val ordem: Int
)
