package com.example.rastreadorprazo.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rastreadorprazo.data.AppDatabase
import com.example.rastreadorprazo.data.StatusObrigacao

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getLong(NotificationHelper.EXTRA_OBRIGACAO_ID, -1L)
        if (id == -1L) return Result.failure()

        val dao = AppDatabase.getInstance(applicationContext).obrigacaoDao()
        val obrigacao = dao.buscarPorId(id) ?: return Result.success()

        if (obrigacao.status == StatusObrigacao.PENDENTE && obrigacao.lembreteAtivo) {
            NotificationHelper.mostrarNotificacao(applicationContext, obrigacao)
        }

        return Result.success()
    }
}
