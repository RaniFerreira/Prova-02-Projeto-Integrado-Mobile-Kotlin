package com.example.app_leituras.domain.model

data class Meta(
    val id: Long = 0L,
    val livroId: Long,
    val tempoPrevistoMinutos: Int,
    val dataAlvo: Long?
)
