package com.example.app_leituras.data.local

import androidx.room.TypeConverter
import com.example.app_leituras.data.local.entity.StatusLeitura
import com.example.app_leituras.data.local.entity.TipoNota

class   Converters {

    @TypeConverter
    fun fromStatusLeitura(status: StatusLeitura): String = status.name

    @TypeConverter
    fun toStatusLeitura(status: String): StatusLeitura = StatusLeitura.valueOf(status)

    @TypeConverter
    fun fromTipoNota(tipo: TipoNota): String = tipo.name

    @TypeConverter
    fun toTipoNota(tipo: String): TipoNota = TipoNota.valueOf(tipo)
}
