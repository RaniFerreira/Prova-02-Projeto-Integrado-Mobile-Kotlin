package com.example.rastreadorprazo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rastreadorprazo.ui.navigation.Rotas

@Composable
fun AppBottomBar(navController: NavController, rotaAtual: String?) {

    fun navegarPara(rota: String) {
        if (rotaAtual == rota) return
        navController.navigate(rota) {
            popUpTo(Rotas.LISTA) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val itens = listOf(
        Triple(Rotas.LISTA, Icons.Outlined.Home, "Início"),
        Triple(Rotas.CALENDARIO, Icons.Outlined.CalendarMonth, "Calendário"),
        Triple(Rotas.NOTIFICACOES, Icons.Outlined.Notifications, "Notificações"),
        Triple(Rotas.AJUSTES, Icons.Outlined.Settings, "Ajustes")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        itens.forEach { (rota, icone, rotulo) ->
            val selecionado = rotaAtual == rota
            NavigationBarItem(
                selected = selecionado,
                onClick = { navegarPara(rota) },
                icon = { Icon(icone, contentDescription = rotulo) },
                label = { Text(rotulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
