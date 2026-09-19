package com.example.apptreino.ui.state

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val mensagem: String) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}
