package com.example.app_leituras.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Paleta pastel verde do protótipo Figma
val VerdePrincipal = Color(0xFFB2FBA5) // verde principal / destaque
val VerdeSecundario = Color(0xFF7BB074) // verde secundário, para textos/ícones sobre fundo claro
val VerdeClaro = Color(0xFFECFFE9) // verde bem claro, para fundos/superfícies

// Neutros
val Branco = Color(0xFFFFFFFF)
val CinzaEscuro = Color(0xFF1B1F1C) // texto principal sobre fundo claro
val CinzaMedio = Color(0xFF5C6660) // texto secundário
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
    secondaryContainer = VerdeClaro,
    onSecondaryContainer = VerdeSecundario,
    tertiary = VerdeSecundario,
    onTertiary = Branco,
    background = VerdeClaro,
    onBackground = CinzaEscuro,
    surface = Branco,
    onSurface = CinzaEscuro,
    surfaceVariant = VerdeClaro,
    onSurfaceVariant = CinzaMedio,
    outline = VerdeSecundario,
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
