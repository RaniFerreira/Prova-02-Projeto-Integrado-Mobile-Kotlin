package com.example.apptreino.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.apptreino.data.entity.CargaHistoricoEntity
import com.example.apptreino.data.entity.ExercicioEntity

data class ExercicioComHistorico(
    @Embedded
    val exercicio: ExercicioEntity,
    @Relation(
        parentColumn = "exercicioId",
        entityColumn = "exercicioId"
    )
    val historico: List<CargaHistoricoEntity>
)
