package com.example.app_leituras.data.mapper

import com.example.app_leituras.data.remote.dto.VolumeDto
import com.example.app_leituras.domain.model.LivroBusca

/**
 * Converte um item da resposta da Google Books API para o modelo de domínio
 * usado na tela de busca. Retorna null quando o volume não tem nem título
 * (acontece em alguns resultados da API), já que sem título não há o que mostrar.
 */
fun VolumeDto.paraDominio(): LivroBusca? {
    val info = volumeInfo ?: return null
    val titulo = info.title ?: return null

    return LivroBusca(
        googleBooksId = id,
        titulo = titulo,
        autor = info.authors?.joinToString(", ") ?: "Autor desconhecido",
        totalPaginas = info.pageCount ?: 0,
        genero = info.categories?.firstOrNull().orEmpty(),
        // a API costuma devolver a capa em http:// — troca pra https:// pra evitar bloqueio de cleartext.
        capaUrl = info.imageLinks?.thumbnail?.replace("http://", "https://")
    )
}
