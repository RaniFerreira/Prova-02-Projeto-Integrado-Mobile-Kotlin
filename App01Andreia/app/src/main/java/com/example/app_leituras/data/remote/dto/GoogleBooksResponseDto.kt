package com.example.app_leituras.data.remote.dto

/** Corpo de resposta de GET volumes?q=... da API do Google Books. */
data class GoogleBooksResponseDto(
    val items: List<VolumeDto>?
)
