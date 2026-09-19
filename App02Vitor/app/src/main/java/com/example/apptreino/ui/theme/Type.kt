package com.example.apptreino.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Estilos de texto exatos do Figma, por contexto de uso (fonte padrão do sistema). */
object AppTextStyles {
    val topBarTitle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
    val topBarSubtitle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)
    val cardTitle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    val cardSubtitle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
    val chipLabel = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
    val buttonText = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    val fieldLabel = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal)
    val fieldValue = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    val highlightValue = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
    val secondaryLine = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
    val highlightLineSmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    val highlightLineLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
}

val Typography = Typography(
    titleLarge = AppTextStyles.topBarTitle,
    titleMedium = AppTextStyles.cardTitle,
    bodyMedium = AppTextStyles.cardSubtitle,
    labelLarge = AppTextStyles.buttonText,
    labelMedium = AppTextStyles.chipLabel,
    labelSmall = AppTextStyles.fieldLabel
)
