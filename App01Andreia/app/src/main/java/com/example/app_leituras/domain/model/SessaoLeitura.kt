package com.example.app_leituras.domain.model

data class SessaoLeitura(
    val id: Long = 0L,
    val livroId: Long,
    val paginaInicio: Int,
    val paginaFim: Int,
    val duracaoSegundos: Long,
    val dataHora: Long
)
