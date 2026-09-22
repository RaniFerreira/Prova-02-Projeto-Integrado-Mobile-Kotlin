package com.example.app_leituras.data.remote.dto

/** Um item da lista `items` da resposta — corresponde a um livro (volume). */
data class VolumeDto(
    val id: String,
    val volumeInfo: VolumeInfoDto?
)
