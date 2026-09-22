package com.example.app_leituras.ui.navigation

import android.net.Uri
import com.example.app_leituras.domain.model.LivroBusca

/** Rotas do NavHost, seguindo o grafo da seção 6 do planejamento. */
sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object BookDetail : Screen("book_detail/{livroId}")
    data object SessaoLeitura : Screen("sessao_leitura/{livroId}")
    data object DefinirMeta : Screen("definir_meta/{livroId}")
    data object DiarioNotas : Screen("diario_notas/{livroId}")
    data object BuscarLivro : Screen("buscar_livro")

    // Além de googleBooksId, carrega os demais campos do LivroBusca como argumentos opcionais:
    // é o que permite ao NovoLivroViewModel.estadoInicial pré-preencher o formulário quando a
    // rota vem da Buscar Livro, e continuar vazio quando vem do cadastro manual (sem argumentos).
    data object NovoLivro : Screen(
        "novo_livro?googleBooksId={googleBooksId}&titulo={titulo}&autor={autor}" +
            "&totalPaginas={totalPaginas}&genero={genero}&capaUrl={capaUrl}"
    )
}

fun bookDetailRoute(livroId: Long) = "book_detail/$livroId"

fun sessaoLeituraRoute(livroId: Long) = "sessao_leitura/$livroId"

fun definirMetaRoute(livroId: Long) = "definir_meta/$livroId"

fun diarioNotasRoute(livroId: Long) = "diario_notas/$livroId"

fun novoLivroRoute(livroBusca: LivroBusca? = null): String {
    if (livroBusca == null) return "novo_livro"
    return buildString {
        append("novo_livro?googleBooksId=${Uri.encode(livroBusca.googleBooksId)}")
        append("&titulo=${Uri.encode(livroBusca.titulo)}")
        append("&autor=${Uri.encode(livroBusca.autor)}")
        append("&totalPaginas=${livroBusca.totalPaginas}")
        append("&genero=${Uri.encode(livroBusca.genero)}")
        livroBusca.capaUrl?.let { append("&capaUrl=${Uri.encode(it)}") }
    }
}
