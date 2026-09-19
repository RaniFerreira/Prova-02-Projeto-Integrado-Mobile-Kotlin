package com.example.app_leituras.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Paleta pastel verde do protótipo Figma (arquivo "PPDM - Prova 02", tela "01 - Início")
val VerdePrincipal = Color(0xFFB2FBA5) // verde principal / destaque (fundo de tags, ícones, trilha de progresso)
val VerdeSecundario = Color(0xFF7BB074) // verde secundário (chips ativos, preenchimento de progresso, FAB)
val VerdeEscuro = Color(0xFF4E7A49) // verde escuro (texto da tag de gênero, borda dos itens de lista)
val VerdeClaro = Color(0xFFECFFE9) // verde bem claro, para fundos/superfícies

// Neutros
val Branco = Color(0xFFFFFFFF)
val CinzaEscuro = Color(0xFF211C17) // texto principal sobre fundo claro (tom acastanhado do Figma)
val CinzaMedio = Color(0xFF80756B) // texto secundário (autor, páginas — tom acastanhado do Figma)
val VerdeQuaseNegro = Color(0xFF10140F) // fundo no dark mode
val VerdeSuperficieDark = Color(0xFF1C231D) // superfícies no dark mode

// Esquema de cores — Light
val LightColorScheme = lightColorScheme(
    primary = VerdeSecundario,
    onPrimary = Branco,
    primaryContainer = VerdePrincipal,
    onPrimaryContainer = CinzaEscuro,
    secondary = VerdePrincipal,
    onSecondary = CinzaEscuro,
    secondaryContainer = VerdePrincipal,
    onSecondaryContainer = VerdeEscuro,
    tertiary = VerdeSecundario,
    onTertiary = Branco,
    background = VerdeClaro,
    onBackground = CinzaEscuro,
    surface = Branco,
    onSurface = CinzaEscuro,
    surfaceVariant = VerdeClaro,
    onSurfaceVariant = CinzaMedio,
    outline = VerdeEscuro,
)

// Esquema de cores — Dark
val DarkColorScheme = darkColorScheme(
    primary = VerdePrincipal,
    onPrimary = CinzaEscuro,
    primaryContainer = VerdeSecundario,
    onPrimaryContainer = VerdeClaro,
    secondary = VerdeSecundario,
    onSecondary = VerdeQuaseNegro,
    secondaryContainer = VerdeSuperficieDark,
    onSecondaryContainer = VerdePrincipal,
    tertiary = VerdePrincipal,
    onTertiary = CinzaEscuro,
    background = VerdeQuaseNegro,
    onBackground = VerdeClaro,
    surface = VerdeSuperficieDark,
    onSurface = VerdeClaro,
    surfaceVariant = VerdeSuperficieDark,
    onSurfaceVariant = VerdePrincipal,
    outline = VerdeSecundario,
)
