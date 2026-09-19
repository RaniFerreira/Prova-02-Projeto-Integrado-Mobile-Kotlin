package com.example.apptreino.ui.screens.historico

data class CargaHistoricoUiModel(
    val historicoId: Long,
    val dataFormatada: String,
    val cargaKg: Double,
    val series: Int,
    val repeticoes: Int
)
