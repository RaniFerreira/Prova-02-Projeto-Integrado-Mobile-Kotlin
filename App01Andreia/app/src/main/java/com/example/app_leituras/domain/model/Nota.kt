package com.example.app_leituras.domain.model

data class Nota(
    val id: Long = 0L,
    val livroId: Long,
    val tipo: TipoNota,
    val conteudo: String,
    val dataHora: Long
)
