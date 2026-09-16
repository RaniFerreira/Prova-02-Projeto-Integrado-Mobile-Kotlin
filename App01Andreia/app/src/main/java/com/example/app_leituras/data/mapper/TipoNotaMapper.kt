package com.example.app_leituras.data.mapper

import com.example.app_leituras.data.local.entity.TipoNota as TipoNotaEntity
import com.example.app_leituras.domain.model.TipoNota as TipoNotaDomain

fun TipoNotaEntity.toDomain(): TipoNotaDomain = when (this) {
    TipoNotaEntity.NOTA -> TipoNotaDomain.NOTA
    TipoNotaEntity.INSIGHT -> TipoNotaDomain.INSIGHT
    TipoNotaEntity.CITACAO -> TipoNotaDomain.CITACAO
}

fun TipoNotaDomain.toEntity(): TipoNotaEntity = when (this) {
    TipoNotaDomain.NOTA -> TipoNotaEntity.NOTA
    TipoNotaDomain.INSIGHT -> TipoNotaEntity.INSIGHT
    TipoNotaDomain.CITACAO -> TipoNotaEntity.CITACAO
}
