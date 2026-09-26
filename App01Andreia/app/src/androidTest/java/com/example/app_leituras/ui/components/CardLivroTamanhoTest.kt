package com.example.app_leituras.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_leituras.domain.model.StatusLeitura
import com.example.app_leituras.ui.theme.AppLeiturasTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Cards da seção "Em andamento" devem ter sempre a mesma largura/altura, com "X/Y páginas"
// e a barra alinhados na base, independentemente do tamanho de título, autor ou gênero.
@RunWith(AndroidJUnit4::class)
class CardLivroTamanhoTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun cardsComTitulosCurtoELongoTemMesmoTamanho() {
        composeRule.setContent {
            AppLeiturasTheme {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    CardLivro(
                        titulo = "Ágil",
                        autor = "Kent",
                        genero = "Tecnologia",
                        capaUrl = null,
                        status = StatusLeitura.LENDO,
                        paginaAtual = 120,
                        totalPaginas = 300,
                        modifier = Modifier.testTag("curto")
                    )
                    CardLivro(
                        titulo = "O Extraordinário e Longuíssimo Título de um Livro Fictício Para Testar o Layout",
                        autor = "Um Autor Com Nome Bastante Comprido Sobrenome Muito Extenso",
                        genero = "Ficção Científica e Fantasia Épica",
                        capaUrl = null,
                        status = StatusLeitura.LENDO,
                        paginaAtual = 42,
                        totalPaginas = 500,
                        modifier = Modifier.testTag("longo")
                    )
                }
            }
        }

        val curto = composeRule.onNodeWithTag("curto").getUnclippedBoundsInRoot()
        val longo = composeRule.onNodeWithTag("longo").getUnclippedBoundsInRoot()
        assertEquals("largura", curto.width, longo.width)
        assertEquals("altura", curto.height, longo.height)
        assertEquals("largura do Figma", 246.dp, curto.width)

        // "X/Y páginas" ancorado na base: mesma distância do topo do card nos dois.
        val paginasCurto = composeRule.onNodeWithText("120/300 páginas", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val paginasLongo = composeRule.onNodeWithText("42/500 páginas", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals("posição de X/Y páginas", paginasCurto.top - curto.top, paginasLongo.top - longo.top)
        assertEquals("base de X/Y páginas", paginasCurto.bottom, paginasLongo.bottom)

        // Altura suficiente (nada cortado): abaixo de "X/Y páginas" cabem espaço 8 + barra 8 + padding 16.
        assertEquals("espaço para a barra", 32f, (curto.bottom - paginasCurto.bottom).value, 1f)
    }
}
