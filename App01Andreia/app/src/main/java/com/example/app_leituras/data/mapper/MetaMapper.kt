package com.example.app_leituras.data.mapper

import com.example.app_leituras.data.local.entity.MetaEntity
import com.example.app_leituras.domain.model.Meta

fun MetaEntity.toDomain(): Meta = Meta(
    id = id,
    livroId = livroId,
    tempoPrevistoMinutos = tempoPrevistoMinutos,
    dataAlvo = dataAlvo
)

fun Meta.toEntity(): MetaEntity = MetaEntity(
    id = id,
    livroId = livroId,
    tempoPrevistoMinutos = tempoPrevistoMinutos,
    dataAlvo = dataAlvo
)
