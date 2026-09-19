package com.example.app_leituras.domain.repository

import com.example.app_leituras.domain.model.Meta
import kotlinx.coroutines.flow.Flow

interface MetaRepository {

    fun observarPorLivro(livroId: Long): Flow<Meta?>

    suspend fun salvar(meta: Meta)
}
