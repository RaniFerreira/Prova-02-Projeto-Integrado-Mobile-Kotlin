package com.example.rastreadorprazo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rastreadorprazo.data.AppDatabase
import com.example.rastreadorprazo.data.Obrigacao
import com.example.rastreadorprazo.data.ObrigacaoRepository
import com.example.rastreadorprazo.data.StatusObrigacao
import com.example.rastreadorprazo.notification.NotificationHelper
import com.example.rastreadorprazo.notification.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FiltroStatus(val rotulo: String) {
    TODAS("Todas"),
    PENDENTES("Pendentes"),
    PAGAS("Pagas"),
    CANCELADAS("Canceladas")
}

class ObrigacoesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ObrigacaoRepository(AppDatabase.getInstance(application).obrigacaoDao())

    private val _filtro = MutableStateFlow(FiltroStatus.TODAS)
    val filtro: StateFlow<FiltroStatus> = _filtro.asStateFlow()

    private val _busca = MutableStateFlow("")
    val busca: StateFlow<String> = _busca.asStateFlow()

    val obrigacoes: StateFlow<List<Obrigacao>> = combine(
        repository.observarTodas(),
        _filtro,
        _busca
    ) { lista, filtro, busca ->
        lista.filter { obrigacao ->
            val statusOk = when (filtro) {
                FiltroStatus.TODAS -> true
                FiltroStatus.PENDENTES -> obrigacao.status == StatusObrigacao.PENDENTE
                FiltroStatus.PAGAS -> obrigacao.status == StatusObrigacao.PAGA
                FiltroStatus.CANCELADAS -> obrigacao.status == StatusObrigacao.CANCELADA
            }
            val buscaOk = busca.isBlank() ||
                obrigacao.titulo.contains(busca, ignoreCase = true) ||
                obrigacao.favorecido.contains(busca, ignoreCase = true)
            statusOk && buscaOk
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun definirFiltro(filtro: FiltroStatus) {
        _filtro.value = filtro
    }

    fun definirBusca(texto: String) {
        _busca.value = texto
    }

    fun obterPorId(id: Long): Flow<Obrigacao?> = repository.observarPorId(id)

    fun salvar(obrigacao: Obrigacao) {
        viewModelScope.launch {
            val id = repository.salvar(obrigacao)
            ReminderScheduler.agendar(getApplication(), obrigacao.copy(id = id))
        }
    }

    fun marcarComoPaga(obrigacao: Obrigacao) {
        viewModelScope.launch {
            repository.atualizar(obrigacao.copy(status = StatusObrigacao.PAGA))
            ReminderScheduler.cancelar(getApplication(), obrigacao.id)
            NotificationHelper.cancelarNotificacao(getApplication(), obrigacao.id)
        }
    }

    fun cancelarObrigacao(obrigacao: Obrigacao) {
        viewModelScope.launch {
            repository.atualizar(obrigacao.copy(status = StatusObrigacao.CANCELADA))
            ReminderScheduler.cancelar(getApplication(), obrigacao.id)
            NotificationHelper.cancelarNotificacao(getApplication(), obrigacao.id)
        }
    }

    fun excluir(obrigacao: Obrigacao) {
        viewModelScope.launch {
            repository.excluir(obrigacao)
            ReminderScheduler.cancelar(getApplication(), obrigacao.id)
            NotificationHelper.cancelarNotificacao(getApplication(), obrigacao.id)
        }
    }
}
