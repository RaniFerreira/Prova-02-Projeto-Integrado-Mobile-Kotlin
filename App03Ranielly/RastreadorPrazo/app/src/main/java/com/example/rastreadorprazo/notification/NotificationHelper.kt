package com.example.rastreadorprazo.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rastreadorprazo.MainActivity
import com.example.rastreadorprazo.R
import com.example.rastreadorprazo.data.Obrigacao
import com.example.rastreadorprazo.util.formatarData
import com.example.rastreadorprazo.util.formatarMoeda

object NotificationHelper {

    const val CHANNEL_ID = "lembretes_obrigacoes"
    const val EXTRA_OBRIGACAO_ID = "obrigacaoId"

    fun criarCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Lembretes de Vencimento",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações de lembrete para obrigações e faturas pendentes"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    fun mostrarNotificacao(context: Context, obrigacao: Obrigacao) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificationId = obrigacao.id.toInt()

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OBRIGACAO_ID, obrigacao.id)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val marcarPagaIntent = Intent(context, MarkAsPaidReceiver::class.java).apply {
            action = MarkAsPaidReceiver.ACTION_MARCAR_PAGA
            putExtra(EXTRA_OBRIGACAO_ID, obrigacao.id)
        }
        val marcarPagaPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            marcarPagaIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val valorFormatado = formatarMoeda(obrigacao.valor)
        val texto = "O item ${obrigacao.titulo} vence em ${formatarData(obrigacao.dataVencimento)}. " +
            "Valor a pagar: $valorFormatado."

        val notificacao = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Lembrete de Vencimento")
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "Ver Detalhes", contentPendingIntent)
            .addAction(0, "Marcar como Paga", marcarPagaPendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notificacao)
    }

    fun cancelarNotificacao(context: Context, id: Long) {
        NotificationManagerCompat.from(context).cancel(id.toInt())
    }
}
