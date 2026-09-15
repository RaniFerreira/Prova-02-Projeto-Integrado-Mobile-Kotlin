package com.example.rastreadorprazo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.rastreadorprazo.data.AppDatabase
import com.example.rastreadorprazo.data.StatusObrigacao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarkAsPaidReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_MARCAR_PAGA = "com.example.rastreadorprazo.ACTION_MARCAR_PAGA"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(NotificationHelper.EXTRA_OBRIGACAO_ID, -1L)
        if (id == -1L) return

        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getInstance(appContext).obrigacaoDao()
                val obrigacao = dao.buscarPorId(id)
                if (obrigacao != null) {
                    dao.atualizar(obrigacao.copy(status = StatusObrigacao.PAGA))
                    ReminderScheduler.cancelar(appContext, id)
                    NotificationHelper.cancelarNotificacao(appContext, id)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
