package com.example.app_leituras.data.repository.fake

import com.example.app_leituras.domain.model.Meta
import com.example.app_leituras.domain.repository.MetaRepository
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Fonte de dados fake em memória, para validar o fluxo reativo do Book Detail
 * (Flow -> ViewModel -> UI) antes de plugar o Room de verdade.
 */
class FakeMetaRepository : MetaRepository {

    private val proximoId = AtomicLong(2L)

    private val agora = System.currentTimeMillis()

    private val _metas = MutableStateFlow(
        listOf(
            // Meta do livro id=3 ("1984"), que já tem sessões e progresso seedados em FakeLeituraRepository.
            Meta(
                id = 1L,
                livroId = 3L,
                tempoPrevistoMinutos = 30,
                dataAlvo = agora + MES * 2
            )
        )
    )

    override fun observarPorLivro(livroId: Long): Flow<Meta?> =
        _metas.map { lista -> lista.firstOrNull { it.livroId == livroId } }

    override suspend fun salvar(meta: Meta) {
        val id = if (meta.id != 0L) meta.id else proximoId.getAndIncrement()
        val metaSalva = meta.copy(id = id)
        _metas.update { lista ->
            if (lista.any { it.livroId == metaSalva.livroId }) {
                lista.map { if (it.livroId == metaSalva.livroId) metaSalva else it }
            } else {
                lista + metaSalva
            }
        }
    }

    private companion object {
        const val MES = 30L * 24 * 3_600_000L
    }
}
