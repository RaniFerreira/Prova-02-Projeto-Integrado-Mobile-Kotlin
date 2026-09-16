package com.example.app_leituras.data.mapper

import com.example.app_leituras.data.local.entity.StatusLeitura as StatusLeituraEntity
import com.example.app_leituras.domain.model.StatusLeitura as StatusLeituraDomain

fun StatusLeituraEntity.toDomain(): StatusLeituraDomain = when (this) {
    StatusLeituraEntity.QUERO_LER -> StatusLeituraDomain.QUERO_LER
    StatusLeituraEntity.LENDO -> StatusLeituraDomain.LENDO
    StatusLeituraEntity.LIDO -> StatusLeituraDomain.LIDO
}

fun StatusLeituraDomain.toEntity(): StatusLeituraEntity = when (this) {
    StatusLeituraDomain.QUERO_LER -> StatusLeituraEntity.QUERO_LER
    StatusLeituraDomain.LENDO -> StatusLeituraEntity.LENDO
    StatusLeituraDomain.LIDO -> StatusLeituraEntity.LIDO
}
