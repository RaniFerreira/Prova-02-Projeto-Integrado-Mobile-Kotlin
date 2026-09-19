package com.example.apptreino.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apptreino.ui.state.UiState
import com.example.apptreino.ui.theme.AppTextStyles
import com.example.apptreino.ui.theme.Primary
import com.example.apptreino.ui.theme.TextMuted

@Composable
fun <T> UiStateContent(
    state: UiState<T>,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    success: @Composable (T) -> Unit
) {
    Box(modifier = modifier) {
        when (state) {
            is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }

            is UiState.Error -> Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = state.mensagem, style = AppTextStyles.cardSubtitle, color = TextMuted)
            }

            is UiState.Empty -> Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = emptyMessage, style = AppTextStyles.cardSubtitle, color = TextMuted)
            }

            is UiState.Success -> success(state.data)
        }
    }
}
