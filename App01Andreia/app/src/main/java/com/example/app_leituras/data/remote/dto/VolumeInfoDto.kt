package com.example.app_leituras.data.remote.dto

/** Subcampo `volumeInfo` do volume — só os campos que o app realmente usa. */
data class VolumeInfoDto(
    val title: String?,
    val authors: List<String>?,
    val pageCount: Int?,
    val categories: List<String>?,
    val imageLinks: ImageLinksDto?
)

data class ImageLinksDto(
    val thumbnail: String?
)
