package com.example.app_leituras.data.repository

import com.example.app_leituras.domain.model.SessaoLeitura
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.domain.repository.LivroRepository

/**
 * Regra compartilhada pelos repositórios de leitura (Room e fake): se a sessão chegou à
 * última página (ou passou dela), o livro sai de "Em andamento" e vai para "Lido".
 */
internal suspend fun marcarComoLidoSeTerminou(livroRepository: LivroRepository, sessao: SessaoLeitura) {
    val livro = livroRepository.buscar(sessao.livroId) ?: return
    if (livro.totalPaginas > 0 && sessao.paginaFim >= livro.totalPaginas && livro.status != StatusLeitura.LIDO) {
        livroRepository.atualizarStatus(livro.id, StatusLeitura.LIDO)
    }
}
