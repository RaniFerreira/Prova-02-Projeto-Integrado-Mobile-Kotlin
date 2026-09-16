package com.example.app_leituras.data.mapper

import com.example.app_leituras.data.local.entity.NotaEntity
import com.example.app_leituras.domain.model.Nota

fun NotaEntity.toDomain(): Nota = Nota(
    id = id,
    livroId = livroId,
    tipo = tipo.toDomain(),
    conteudo = conteudo,
    dataHora = dataHora
)

fun Nota.toEntity(): NotaEntity = NotaEntity(
    id = id,
    livroId = livroId,
    tipo = tipo.toEntity(),
    conteudo = conteudo,
    dataHora = dataHora
)
