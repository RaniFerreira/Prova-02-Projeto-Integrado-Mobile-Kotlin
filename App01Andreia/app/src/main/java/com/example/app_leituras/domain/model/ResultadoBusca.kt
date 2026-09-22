package com.example.app_leituras.domain.model

/** Estado de uma busca na Google Books API, já pronto para a UI observar. */
sealed class ResultadoBusca {
    data object Carregando : ResultadoBusca()
    data class Sucesso(val livros: List<LivroBusca>) : ResultadoBusca()
    data object Vazio : ResultadoBusca()
    data class Erro(val mensagem: String) : ResultadoBusca()
}
