package com.example.app_leituras.data.repository.fake

import com.example.app_leituras.domain.model.Nota
import com.example.app_leituras.domain.model.TipoNota
import com.example.app_leituras.domain.repository.NotaRepository
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNotaRepository : NotaRepository {

    private val proximoId = AtomicLong(3L)

    private val agora = System.currentTimeMillis()

    private val _notas = MutableStateFlow(
        listOf(
            Nota(
                id = 1L,
                livroId = 3L,
                tipo = TipoNota.CITACAO,
                conteudo = "\"A Guerra é Paz. A Liberdade é Escravidão. A Ignorância é Força.\"",
                dataHora = agora - HORA * 20
            ),
            Nota(
                id = 2L,
                livroId = 5L,
                tipo = TipoNota.INSIGHT,
                conteudo = "A jornada do herói fica bem clara nos primeiros capítulos.",
                dataHora = agora - HORA * 96
            )
        )
    )

    override fun observarNotasPorLivro(livroId: Long): Flow<List<Nota>> =
        _notas.map { lista -> lista.filter { it.livroId == livroId }.sortedByDescending { it.dataHora } }

    override suspend fun adicionarNota(nota: Nota): Long {
        val id = if (nota.id != 0L) nota.id else proximoId.getAndIncrement()
        val notaSalva = nota.copy(id = id)
        _notas.update { it + notaSalva }
        return id
    }

    private companion object {
        const val HORA = 3_600_000L
    }
}
