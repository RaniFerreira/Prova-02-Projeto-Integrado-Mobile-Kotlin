package com.example.app_leituras.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_leituras.data.local.entity.StatusLeitura
import com.example.app_leituras.domain.model.chaveTituloAutor

private data class LinhaLivro(
    val id: Long,
    val titulo: String,
    val autor: String,
    val status: StatusLeitura,
    val paginaAtual: Int,
    val googleBooksId: String?,
    val capaUrl: String?,
    val genero: String
)

/**
 * v1 -> v2: o catálogo passa a ter googleBooksId único. Antes de criar o índice:
 * 1. normaliza status gravados em formato antigo ("Lendo", "quero ler"...) para o nome do enum;
 * 2. junta livros duplicados (mesmo googleBooksId ou mesmo título + autor), que faziam o mesmo
 *    livro aparecer em duas seções do Dashboard. Fica o registro com o status mais avançado
 *    (LIDO > LENDO > QUERO_LER; empate = o mais antigo), com a maior página atual, e as sessões,
 *    notas e meta dos duplicados são transferidas para ele — nada é perdido.
 */
val MIGRACAO_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val linhas = mutableListOf<LinhaLivro>()
        db.query("SELECT id, titulo, autor, status, paginaAtual, googleBooksId, capaUrl, genero FROM livros ORDER BY id")
            .use { cursor ->
                while (cursor.moveToNext()) {
                    linhas += LinhaLivro(
                        id = cursor.getLong(0),
                        titulo = cursor.getString(1),
                        autor = cursor.getString(2),
                        status = normalizarStatus(cursor.getString(3)),
                        paginaAtual = cursor.getInt(4),
                        googleBooksId = if (cursor.isNull(5)) null else cursor.getString(5),
                        capaUrl = if (cursor.isNull(6)) null else cursor.getString(6),
                        genero = cursor.getString(7)
                    )
                }
            }

        linhas.forEach { livro ->
            db.execSQL("UPDATE livros SET status = ? WHERE id = ?", arrayOf<Any?>(livro.status.name, livro.id))
        }

        agruparDuplicados(linhas).forEach { grupo -> juntarDuplicados(db, grupo) }

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_livros_googleBooksId` ON `livros` (`googleBooksId`)")
    }
}

// Union-find simples: dois livros são o mesmo se compartilham googleBooksId OU título + autor
// (transitivo — A~B pelo id da API e B~C pelo título juntam A, B e C num grupo só).
private fun agruparDuplicados(linhas: List<LinhaLivro>): List<List<LinhaLivro>> {
    val pai = linhas.associate { it.id to it.id }.toMutableMap()
    fun raiz(id: Long): Long {
        var atual = id
        while (pai.getValue(atual) != atual) atual = pai.getValue(atual)
        return atual
    }
    fun unir(grupo: List<LinhaLivro>) {
        grupo.drop(1).forEach { livro ->
            val a = raiz(grupo.first().id)
            val b = raiz(livro.id)
            if (a != b) pai[maxOf(a, b)] = minOf(a, b)
        }
    }
    linhas.filter { it.googleBooksId != null }.groupBy { it.googleBooksId }.values.forEach(::unir)
    linhas.groupBy { chaveTituloAutor(it.titulo, it.autor) }.values.forEach(::unir)
    return linhas.groupBy { raiz(it.id) }.values.filter { it.size > 1 }
}

private fun juntarDuplicados(db: SupportSQLiteDatabase, grupo: List<LinhaLivro>) {
    val mantido = grupo.maxWith(compareBy<LinhaLivro> { it.status.ordinal }.thenByDescending { it.id })
    var mantidoTemMeta = contarMetas(db, mantido.id) > 0

    grupo.filter { it.id != mantido.id }.forEach { duplicado ->
        db.execSQL("UPDATE sessoes_leitura SET livroId = ? WHERE livroId = ?", arrayOf<Any?>(mantido.id, duplicado.id))
        db.execSQL("UPDATE notas SET livroId = ? WHERE livroId = ?", arrayOf<Any?>(mantido.id, duplicado.id))
        // metas tem índice único por livro: só transfere se o livro mantido ainda não tem meta.
        if (!mantidoTemMeta && contarMetas(db, duplicado.id) > 0) {
            db.execSQL("UPDATE metas SET livroId = ? WHERE livroId = ?", arrayOf<Any?>(mantido.id, duplicado.id))
            mantidoTemMeta = true
        } else {
            db.execSQL("DELETE FROM metas WHERE livroId = ?", arrayOf<Any?>(duplicado.id))
        }
        db.execSQL("DELETE FROM livros WHERE id = ?", arrayOf<Any?>(duplicado.id))
    }

    // Aproveita dados que só o duplicado tinha (id da API, capa, gênero, página mais avançada).
    db.execSQL(
        "UPDATE livros SET paginaAtual = ?, googleBooksId = ?, capaUrl = ?, genero = ? WHERE id = ?",
        arrayOf<Any?>(
            grupo.maxOf { it.paginaAtual },
            mantido.googleBooksId ?: grupo.firstNotNullOfOrNull { it.googleBooksId },
            mantido.capaUrl ?: grupo.firstNotNullOfOrNull { it.capaUrl },
            mantido.genero.ifBlank { grupo.firstOrNull { it.genero.isNotBlank() }?.genero.orEmpty() },
            mantido.id
        )
    )
}

/**
 * v2 -> v3: acerta livros.paginaAtual de quem já tinha sessões registradas antes da correção
 * (a página do livro nunca era atualizada ao finalizar uma sessão — ex.: 1984 parado em 120
 * com sessão até 328). Só avança, nunca diminui. O esquema das tabelas não muda.
 */
val MIGRACAO_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            UPDATE livros SET paginaAtual = (
                SELECT MAX(sessoes_leitura.paginaFim) FROM sessoes_leitura WHERE sessoes_leitura.livroId = livros.id
            )
            WHERE (
                SELECT MAX(sessoes_leitura.paginaFim) FROM sessoes_leitura WHERE sessoes_leitura.livroId = livros.id
            ) > paginaAtual
            """.trimIndent()
        )
    }
}

/**
 * v3 -> v4: livros que já chegaram à última página (ex.: 1984 em 328/328) passam para LIDO,
 * mesma regra aplicada daqui pra frente ao registrar uma sessão (marcarComoLidoSeTerminou).
 */
val MIGRACAO_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "UPDATE livros SET status = 'LIDO' WHERE totalPaginas > 0 AND paginaAtual >= totalPaginas AND status != 'LIDO'"
        )
    }
}

private fun contarMetas(db: SupportSQLiteDatabase, livroId: Long): Int =
    db.query("SELECT COUNT(*) FROM metas WHERE livroId = ?", arrayOf<Any?>(livroId)).use { cursor ->
        cursor.moveToFirst()
        cursor.getInt(0)
    }
