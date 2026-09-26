package com.example.app_leituras.data.local

import com.example.app_leituras.data.local.entity.StatusLeitura
import com.example.app_leituras.domain.model.chaveTituloAutor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class StatusLegadoTest {

    @Test
    fun nomesDoEnumContinuamIguais() {
        StatusLeitura.entries.forEach { assertEquals(it, normalizarStatus(it.name)) }
    }

    @Test
    fun formatosAntigosSaoConvertidos() {
        assertEquals(StatusLeitura.QUERO_LER, normalizarStatus("Quero Ler"))
        assertEquals(StatusLeitura.QUERO_LER, normalizarStatus(" quero-ler "))
        assertEquals(StatusLeitura.LENDO, normalizarStatus("Lendo"))
        assertEquals(StatusLeitura.LENDO, normalizarStatus("em andamento"))
        assertEquals(StatusLeitura.LIDO, normalizarStatus("lido"))
        assertEquals(StatusLeitura.LIDO, normalizarStatus("Concluído"))
    }

    @Test
    fun valorDesconhecidoViraQueroLerEmVezDeQuebrar() {
        assertEquals(StatusLeitura.QUERO_LER, normalizarStatus("???"))
        assertEquals(StatusLeitura.QUERO_LER, normalizarStatus(""))
    }

    @Test
    fun chaveTituloAutorIgnoraMaiusculasAcentosEEspacos() {
        assertEquals(
            chaveTituloAutor("O Alquimista", "Paulo Coelho"),
            chaveTituloAutor("  o  alquimista ", "PAULO COELHO")
        )
        assertEquals(chaveTituloAutor("Revolução", "Orwell"), chaveTituloAutor("Revolucao", "orwell"))
        assertNotEquals(chaveTituloAutor("Duna", "Frank Herbert"), chaveTituloAutor("Duna", "Outro Autor"))
    }
}
