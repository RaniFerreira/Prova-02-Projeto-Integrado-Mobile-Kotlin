package com.example.app_leituras.data.mapper

import com.example.app_leituras.data.local.entity.SessaoLeituraEntity
import com.example.app_leituras.domain.model.SessaoLeitura

fun SessaoLeituraEntity.toDomain(): SessaoLeitura = SessaoLeitura(
    id = id,
    livroId = livroId,
    paginaInicio = paginaInicio,
    paginaFim = paginaFim,
    duracaoSegundos = duracaoSegundos,
    dataHora = dataHora
)

fun SessaoLeitura.toEntity(): SessaoLeituraEntity = SessaoLeituraEntity(
    id = id,
    livroId = livroId,
    paginaInicio = paginaInicio,
    paginaFim = paginaFim,
    duracaoSegundos = duracaoSegundos,
    dataHora = dataHora
)
