package com.example.rastreadorprazo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

enum class TipoObrigacao {
    PRAZO,
    FATURA
}

enum class StatusObrigacao {
    PENDENTE,
    PAGA,
    CANCELADA
}

@Entity(tableName = "obrigacoes")
data class Obrigacao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val tipo: TipoObrigacao,
    val valor: Double,
    val favorecido: String,
    val dataVencimento: LocalDate,
    val horaLembrete: LocalTime,
    val lembreteAtivo: Boolean = true,
    val status: StatusObrigacao = StatusObrigacao.PENDENTE,
    val dataCriacao: LocalDateTime = LocalDateTime.now()
)
