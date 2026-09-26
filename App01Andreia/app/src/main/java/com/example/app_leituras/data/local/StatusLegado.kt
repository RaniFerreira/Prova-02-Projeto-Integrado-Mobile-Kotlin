package com.example.app_leituras.data.local

import com.example.app_leituras.data.local.entity.StatusLeitura
import java.text.Normalizer
import java.util.Locale

/**
 * Converte o texto gravado na coluna "status" para o enum, aceitando formatos antigos/soltos
 * ("Lendo", "quero ler", "Quero-Ler", "concluído"...) em vez de quebrar com valueOf().
 * Valor desconhecido vira QUERO_LER, para o livro continuar visível no catálogo.
 */
fun normalizarStatus(valor: String): StatusLeitura {
    val chave = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .uppercase(Locale.ROOT)
        .replace(Regex("[\\s-]+"), "_")
    return when (chave) {
        "QUERO_LER", "PARA_LER" -> StatusLeitura.QUERO_LER
        "LENDO", "EM_ANDAMENTO" -> StatusLeitura.LENDO
        "LIDO", "CONCLUIDO", "FINALIZADO" -> StatusLeitura.LIDO
        else -> StatusLeitura.QUERO_LER
    }
}
