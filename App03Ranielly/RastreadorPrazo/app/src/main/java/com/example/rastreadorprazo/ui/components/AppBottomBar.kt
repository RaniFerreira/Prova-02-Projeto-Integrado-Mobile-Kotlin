package com.example.rastreadorprazo.ui.components

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppBottomBar(rotaAtual: String?, onInicioClick: () -> Unit) {
    val contexto = LocalContext.current
    NavigationBar {
        NavigationBarItem(
            selected = rotaAtual == "lista",
            onClick = onInicioClick,
            icon = { Icon(Icons.Filled.Home, contentDescription = "Início") },
            label = { Text("Início") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { Toast.makeText(contexto, "Em breve", Toast.LENGTH_SHORT).show() },
            icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Calendário") },
            label = { Text("Calendário") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { Toast.makeText(contexto, "Em breve", Toast.LENGTH_SHORT).show() },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notificações") },
            label = { Text("Notificações") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { Toast.makeText(contexto, "Em breve", Toast.LENGTH_SHORT).show() },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Ajustes") },
            label = { Text("Ajustes") }
        )
    }
}
