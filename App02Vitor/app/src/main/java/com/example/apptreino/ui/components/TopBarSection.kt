package com.example.apptreino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.BorderCard
import com.example.apptreino.ui.theme.Dimens
import com.example.apptreino.ui.theme.Surface
import com.example.apptreino.ui.theme.TextMuted
import com.example.apptreino.ui.theme.TextPrimary

@Composable
fun TopBarSection(title: String, subtitle: String, onBack: (() -> Unit)? = null) {
    val strokeWidth = with(androidx.compose.ui.platform.LocalDensity.current) { 1.dp.toPx() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .drawBehind {
                drawLine(
                    color = BorderCard,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .padding(
                start = Dimens.screenPaddingH,
                end = Dimens.screenPaddingH,
                top = Dimens.topBarPaddingTop,
                bottom = Dimens.topBarPaddingBottom
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.topBarItemSpacing))
            }
            Text(text = title, style = AppTextStyles.topBarTitle, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(Dimens.topBarItemSpacing))
        Text(text = subtitle, style = AppTextStyles.topBarSubtitle, color = TextMuted)
    }
}
