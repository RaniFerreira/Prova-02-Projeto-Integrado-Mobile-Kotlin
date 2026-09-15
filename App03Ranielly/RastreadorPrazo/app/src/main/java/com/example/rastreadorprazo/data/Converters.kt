package com.example.rastreadorprazo.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class Converters {

    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun localDateToEpochDay(data: LocalDate?): Long? = data?.toEpochDay()

    @TypeConverter
    fun fromSecondOfDay(value: Int?): LocalTime? = value?.let { LocalTime.ofSecondOfDay(it.toLong()) }

    @TypeConverter
    fun localTimeToSecondOfDay(hora: LocalTime?): Int? = hora?.toSecondOfDay()

    @TypeConverter
    fun fromEpochSecond(value: Long?): LocalDateTime? =
        value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }

    @TypeConverter
    fun localDateTimeToEpochSecond(dataHora: LocalDateTime?): Long? =
        dataHora?.toEpochSecond(ZoneOffset.UTC)

    @TypeConverter
    fun fromTipoObrigacao(value: String?): TipoObrigacao? = value?.let { TipoObrigacao.valueOf(it) }

    @TypeConverter
    fun tipoObrigacaoToString(tipo: TipoObrigacao?): String? = tipo?.name

    @TypeConverter
    fun fromStatusObrigacao(value: String?): StatusObrigacao? = value?.let { StatusObrigacao.valueOf(it) }

    @TypeConverter
    fun statusObrigacaoToString(status: StatusObrigacao?): String? = status?.name
}
