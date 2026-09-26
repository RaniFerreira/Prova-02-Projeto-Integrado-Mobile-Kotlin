package com.example.app_leituras.domain.model

import java.text.Normalizer
import java.util.Locale

/**
 * Chave que identifica "o mesmo livro" quando não há googleBooksId (cadastro manual):
 * título + autor sem espaços nas pontas/duplicados, sem acentos e sem diferença de maiúsculas.
 * Usada tanto para bloquear cadastro duplicado quanto na migração que junta duplicados antigos.
 */
fun chaveTituloAutor(titulo: String, autor: String): String =
    "${normalizarTexto(titulo)}|${normalizarTexto(autor)}"

private fun normalizarTexto(texto: String): String =
    Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .replace(Regex("\\s+"), " ")
        .lowercase(Locale.ROOT)
