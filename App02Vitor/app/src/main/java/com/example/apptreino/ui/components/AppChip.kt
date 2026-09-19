package com.example.apptreino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.ChipBg
import com.example.apptreino.ui.theme.Dimens
import com.example.apptreino.ui.theme.Primary
import com.example.apptreino.ui.theme.TextPrimary

@Composable
fun AppChip(
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val background = if (active) Primary else ChipBg
    val textColor = if (active) Color.White else TextPrimary

    var chipModifier = modifier
        .background(color = background, shape = RoundedCornerShape(Dimens.chipCornerRadius))
    if (onClick != null) {
        chipModifier = chipModifier.clickable(onClick = onClick)
    }
    chipModifier = chipModifier.padding(horizontal = Dimens.chipPaddingH, vertical = Dimens.chipPaddingV)

    Text(text = label, style = AppTextStyles.chipLabel, color = textColor, modifier = chipModifier)
}
