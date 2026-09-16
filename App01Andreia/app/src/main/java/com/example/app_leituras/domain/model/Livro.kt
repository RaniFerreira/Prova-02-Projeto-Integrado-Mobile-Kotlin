package com.example.app_leituras.domain.model

data class Livro(
    val id: Long = 0L,
    val titulo: String,
    val autor: String,
    val totalPaginas: Int,
    val genero: String,
    val capaUrl: String?,
    val status: StatusLeitura,
    val paginaAtual: Int = 0,
    val googleBooksId: String?,
    val dataCriacao: Long
)
