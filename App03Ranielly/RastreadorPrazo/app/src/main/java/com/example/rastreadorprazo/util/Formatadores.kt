package com.example.rastreadorprazo.util

import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val formatoData: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
val formatoHora: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val formatoDataHora: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")

private val localePtBr = Locale.forLanguageTag("pt-BR")

fun formatarMoeda(valor: Double): String =
    NumberFormat.getCurrencyInstance(localePtBr).format(valor)

fun formatarData(data: LocalDate): String = data.format(formatoData)

fun formatarHora(hora: LocalTime): String = hora.format(formatoHora)

fun formatarDataHora(dataHora: LocalDateTime): String = dataHora.format(formatoDataHora)

fun formatarValorParaEdicao(valor: Double): String =
    String.format(localePtBr, "%.2f", valor).replace(".", ",")

fun parseValorBr(texto: String): Double? {
    val limpo = texto.trim().replace(".", "").replace(",", ".")
    return limpo.toDoubleOrNull()
}
