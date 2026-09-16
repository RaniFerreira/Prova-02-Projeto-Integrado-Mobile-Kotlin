package com.example.rastreadorprazo.data

import android.content.Context

enum class TemaModo {
    SISTEMA,
    CLARO,
    ESCURO
}

class SettingsRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("ajustes_rastreador_prazo", Context.MODE_PRIVATE)

    fun obterTemaModo(): TemaModo =
        TemaModo.entries.find { it.name == prefs.getString(CHAVE_TEMA, null) } ?: TemaModo.SISTEMA

    fun definirTemaModo(modo: TemaModo) {
        prefs.edit().putString(CHAVE_TEMA, modo.name).apply()
    }

    fun notificacoesGlobaisAtivas(): Boolean =
        prefs.getBoolean(CHAVE_NOTIFICACOES, true)

    fun definirNotificacoesGlobaisAtivas(ativo: Boolean) {
        prefs.edit().putBoolean(CHAVE_NOTIFICACOES, ativo).apply()
    }

    private companion object {
        const val CHAVE_TEMA = "tema_modo"
        const val CHAVE_NOTIFICACOES = "notificacoes_ativas"
    }
}
