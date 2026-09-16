package com.example.rastreadorprazo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rastreadorprazo.data.AppDatabase
import com.example.rastreadorprazo.data.ObrigacaoRepository
import com.example.rastreadorprazo.data.SettingsRepository
import com.example.rastreadorprazo.data.StatusObrigacao
import com.example.rastreadorprazo.data.TemaModo
import com.example.rastreadorprazo.notification.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AjustesViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    private val obrigacaoRepository = ObrigacaoRepository(AppDatabase.getInstance(application).obrigacaoDao())

    private val _temaModo = MutableStateFlow(settingsRepository.obterTemaModo())
    val temaModo: StateFlow<TemaModo> = _temaModo.asStateFlow()

    private val _notificacoesAtivas = MutableStateFlow(settingsRepository.notificacoesGlobaisAtivas())
    val notificacoesAtivas: StateFlow<Boolean> = _notificacoesAtivas.asStateFlow()

    fun definirTemaModo(modo: TemaModo) {
        settingsRepository.definirTemaModo(modo)
        _temaModo.value = modo
    }

    fun definirNotificacoesAtivas(ativo: Boolean) {
        settingsRepository.definirNotificacoesGlobaisAtivas(ativo)
        _notificacoesAtivas.value = ativo
        viewModelScope.launch {
            val pendentes = obrigacaoRepository.observarTodas().first()
                .filter { it.status == StatusObrigacao.PENDENTE && it.lembreteAtivo }
            pendentes.forEach { obrigacao ->
                if (ativo) {
                    ReminderScheduler.agendar(getApplication(), obrigacao)
                } else {
                    ReminderScheduler.cancelar(getApplication(), obrigacao.id)
                }
            }
        }
    }
}
