package com.example.app_leituras.data.mapper

import com.example.app_leituras.data.local.entity.LivroEntity
import com.example.app_leituras.domain.model.Livro

fun LivroEntity.toDomain(): Livro = Livro(
    id = id,
    titulo = titulo,
    autor = autor,
    totalPaginas = totalPaginas,
    genero = genero,
    capaUrl = capaUrl,
    status = status.toDomain(),
    paginaAtual = paginaAtual,
    googleBooksId = googleBooksId,
    dataCriacao = dataCriacao
)

fun Livro.toEntity(): LivroEntity = LivroEntity(
    id = id,
    titulo = titulo,
    autor = autor,
    totalPaginas = totalPaginas,
    genero = genero,
    capaUrl = capaUrl,
    status = status.toEntity(),
    paginaAtual = paginaAtual,
    googleBooksId = googleBooksId,
    dataCriacao = dataCriacao
)
