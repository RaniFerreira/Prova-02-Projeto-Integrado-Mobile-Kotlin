package com.example.app_leituras.domain.model

/**
 * Resultado de uma busca na Google Books API — não confundir com [Livro],
 * que representa um livro já cadastrado no catálogo local (Room).
 * Um [LivroBusca] só vira [Livro] quando o usuário escolhe cadastrá-lo.
 */
data class LivroBusca(
    val googleBooksId: String,
    val titulo: String,
    val autor: String,
    val totalPaginas: Int,
    val genero: String,
    val capaUrl: String?
)
