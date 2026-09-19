package com.example.apptreino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.Dimens
import com.example.apptreino.ui.theme.Primary

@Composable
fun PrimaryFab(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = AppTextStyles.buttonText,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = Primary, shape = RoundedCornerShape(Dimens.fabCornerRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.fabPaddingH, vertical = Dimens.fabPaddingV)
    )
}
