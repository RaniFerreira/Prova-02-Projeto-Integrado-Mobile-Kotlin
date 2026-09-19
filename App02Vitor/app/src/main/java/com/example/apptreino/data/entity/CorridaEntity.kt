package com.example.apptreino.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "corridas")
data class CorridaEntity(
    @PrimaryKey(autoGenerate = true)
    val corridaId: Long = 0,
    val dataRegistro: Long,
    val distanciaKm: Double,
    val tempoTotalSegundos: Int,
    val paceSegundosPorKm: Int
)
