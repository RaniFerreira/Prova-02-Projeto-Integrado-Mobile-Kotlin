package com.example.rastreadorprazo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.rastreadorprazo.notification.NotificationHelper
import com.example.rastreadorprazo.ui.navigation.AppNavHost
import com.example.rastreadorprazo.ui.theme.RastreadorPrazoTheme

class MainActivity : ComponentActivity() {

    private var pendingObrigacaoId by mutableStateOf<Long?>(null)

    private val permissaoNotificacaoLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.criarCanal(this)
        solicitarPermissaoNotificacaoSeNecessario()

        pendingObrigacaoId = extrairObrigacaoId(intent)

        setContent {
            RastreadorPrazoTheme {
                AppNavHost(
                    pendingObrigacaoId = pendingObrigacaoId,
                    onPendingConsumido = { pendingObrigacaoId = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extrairObrigacaoId(intent)?.let { pendingObrigacaoId = it }
    }

    private fun extrairObrigacaoId(intent: Intent?): Long? {
        val id = intent?.getLongExtra(NotificationHelper.EXTRA_OBRIGACAO_ID, -1L) ?: -1L
        return if (id == -1L) null else id
    }

    private fun solicitarPermissaoNotificacaoSeNecessario() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissaoNotificacaoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
