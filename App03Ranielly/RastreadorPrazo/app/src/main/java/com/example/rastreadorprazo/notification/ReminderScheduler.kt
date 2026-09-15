package com.example.rastreadorprazo.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.rastreadorprazo.data.Obrigacao
import com.example.rastreadorprazo.data.StatusObrigacao
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private fun tagPara(id: Long) = "lembrete_obrigacao_$id"

    fun agendar(context: Context, obrigacao: Obrigacao) {
        if (!obrigacao.lembreteAtivo || obrigacao.status != StatusObrigacao.PENDENTE) {
            cancelar(context, obrigacao.id)
            return
        }

        val disparo = LocalDateTime.of(obrigacao.dataVencimento, obrigacao.horaLembrete)
        val atrasoMillis = Duration.between(LocalDateTime.now(), disparo).toMillis().coerceAtLeast(0L)

        val dados = Data.Builder()
            .putLong(NotificationHelper.EXTRA_OBRIGACAO_ID, obrigacao.id)
            .build()

        val requisicao = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(atrasoMillis, TimeUnit.MILLISECONDS)
            .setInputData(dados)
            .addTag(tagPara(obrigacao.id))
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(tagPara(obrigacao.id), ExistingWorkPolicy.REPLACE, requisicao)
    }

    fun cancelar(context: Context, id: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(tagPara(id))
    }
}
