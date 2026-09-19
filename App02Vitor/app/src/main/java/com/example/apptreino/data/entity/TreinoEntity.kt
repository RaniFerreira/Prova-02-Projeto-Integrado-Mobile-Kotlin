package com.example.apptreino.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "treinos")
data class TreinoEntity(
    @PrimaryKey(autoGenerate = true)
    val treinoId: Long = 0,
    val nome: String
)
