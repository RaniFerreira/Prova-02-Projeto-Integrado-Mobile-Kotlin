package com.example.rastreadorprazo.ui.navigation

object Rotas {
    const val LISTA = "lista"
    const val FORM_NOVA = "form"
    const val FORM_EDITAR = "form/{obrigacaoId}"
    const val DETALHES = "detalhes/{obrigacaoId}"
    const val CALENDARIO = "calendario"
    const val NOTIFICACOES = "notificacoes"
    const val AJUSTES = "ajustes"

    fun formEditar(id: Long) = "form/$id"
    fun detalhes(id: Long) = "detalhes/$id"
}
