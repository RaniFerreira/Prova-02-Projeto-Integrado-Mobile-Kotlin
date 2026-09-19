package com.example.apptreino.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    background = BgApp,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = ChipBg,
    onSurfaceVariant = TextMuted,
    surfaceContainer = Surface,
    surfaceContainerLow = Surface,
    surfaceContainerHigh = Surface,
    surfaceContainerHighest = Surface,
    surfaceContainerLowest = Surface,
    primary = Primary,
    onPrimary = Surface,
    primaryContainer = Primary,
    onPrimaryContainer = Surface,
    secondary = Primary,
    onSecondary = Surface,
    secondaryContainer = ChipBg,
    onSecondaryContainer = TextPrimary,
    tertiary = Success,
    onTertiary = Surface,
    tertiaryContainer = Success,
    onTertiaryContainer = Surface,
    outline = BorderCard,
    outlineVariant = BorderCard,
    inverseSurface = TextPrimary,
    inverseOnSurface = Surface
)

@Composable
fun AppTreinoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
