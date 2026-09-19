package com.example.apptreino.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.apptreino.data.entity.ExercicioEntity
import com.example.apptreino.data.entity.TreinoEntity

data class TreinoComExercicios(
    @Embedded
    val treino: TreinoEntity,
    @Relation(
        parentColumn = "treinoId",
        entityColumn = "treinoId"
    )
    val exercicios: List<ExercicioEntity>
)
