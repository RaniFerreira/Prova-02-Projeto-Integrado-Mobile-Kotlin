package com.example.app_leituras.data.local

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_leituras.data.local.entity.StatusLeitura
import com.example.app_leituras.data.remote.GoogleBooksRepository
import com.example.app_leituras.data.remote.RetrofitConfig
import com.example.app_leituras.data.repository.LeituraRepositoryImpl
import com.example.app_leituras.data.repository.LivroRepositoryImpl
import com.example.app_leituras.domain.model.SessaoLeitura
import com.example.app_leituras.domain.model.StatusLeitura as DominioStatus
import com.example.app_leituras.ui.novolivro.NovoLivroViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// Bug: o mesmo livro cadastrado duas vezes aparecia em "Quero Ler" e em "Em andamento".
// Cobre a migração (limpa duplicados já salvos sem perder dados) e o bloqueio no cadastro.
@RunWith(AndroidJUnit4::class)
class MigracaoDuplicadosTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val nomeBanco = "teste_migracao_duplicados.db"
    private var banco: AppDatabase? = null

    @Before
    fun limpar() {
        context.deleteDatabase(nomeBanco)
    }

    @After
    fun fechar() {
        banco?.close()
        context.deleteDatabase(nomeBanco)
    }

    // Esquema v1 exatamente como está no aparelho (copiado do sqlite_master do app).
    private fun criarBancoV1(preencher: SQLiteDatabase.() -> Unit) {
        val arquivo = context.getDatabasePath(nomeBanco).apply { parentFile?.mkdirs() }
        SQLiteDatabase.openOrCreateDatabase(arquivo, null).use { db ->
            db.execSQL("CREATE TABLE `livros` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `titulo` TEXT NOT NULL, `autor` TEXT NOT NULL, `totalPaginas` INTEGER NOT NULL, `genero` TEXT NOT NULL, `capaUrl` TEXT, `status` TEXT NOT NULL, `paginaAtual` INTEGER NOT NULL, `googleBooksId` TEXT, `data_criacao` INTEGER NOT NULL)")
            db.execSQL("CREATE TABLE `sessoes_leitura` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `livroId` INTEGER NOT NULL, `paginaInicio` INTEGER NOT NULL, `paginaFim` INTEGER NOT NULL, `duracaoSegundos` INTEGER NOT NULL, `data_hora` INTEGER NOT NULL, FOREIGN KEY(`livroId`) REFERENCES `livros`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            db.execSQL("CREATE INDEX `index_sessoes_leitura_livroId` ON `sessoes_leitura` (`livroId`)")
            db.execSQL("CREATE TABLE `metas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `livroId` INTEGER NOT NULL, `tempoPrevistoMinutos` INTEGER NOT NULL, `data_alvo` INTEGER, FOREIGN KEY(`livroId`) REFERENCES `livros`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            db.execSQL("CREATE UNIQUE INDEX `index_metas_livroId` ON `metas` (`livroId`)")
            db.execSQL("CREATE TABLE `notas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `livroId` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `conteudo` TEXT NOT NULL, `data_hora` INTEGER NOT NULL, FOREIGN KEY(`livroId`) REFERENCES `livros`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            db.execSQL("CREATE INDEX `index_notas_livroId` ON `notas` (`livroId`)")
            db.preencher()
            db.version = 1
        }
    }

    private fun abrirComRoom(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, nomeBanco)
            .addMigrations(MIGRACAO_1_2, MIGRACAO_2_3, MIGRACAO_3_4)
            .build()
            .also { banco = it }

    private fun SQLiteDatabase.livro(id: Long, titulo: String, autor: String, status: String, pagina: Int, googleId: String?) =
        execSQL(
            "INSERT INTO livros (id, titulo, autor, totalPaginas, genero, capaUrl, status, paginaAtual, googleBooksId, data_criacao) VALUES (?, ?, ?, 500, 'Fantasia', NULL, ?, ?, ?, 0)",
            arrayOf<Any?>(id, titulo, autor, status, pagina, googleId)
        )

    @Test
    fun migracaoJuntaDuplicadosSemPerderDados() = runBlocking {
        criarBancoV1 {
            // Cenário do aparelho: mesmo livro da API salvo 2x (8 = Quero Ler, 9 = Lendo).
            livro(8, "Harry Potter and the Order of the Phoenix", "J. K. Rowling", "QUERO_LER", 0, "KZvHBAAAQBAJ")
            livro(9, "Harry Potter and the Order of the Phoenix", "J. K. Rowling", "LENDO", 40, "KZvHBAAAQBAJ")
            // Duplicado manual (sem id da API), diferente só em maiúsculas/espaços, + status antigo.
            livro(20, "O Alquimista", "Paulo Coelho", "Quero Ler", 0, null)
            livro(21, "  o alquimista ", "PAULO COELHO", "Lido", 208, null)
            // Livro sem duplicado, com status em formato antigo.
            livro(30, "Duna", "Frank Herbert", "lendo", 10, null)
            execSQL("INSERT INTO sessoes_leitura (livroId, paginaInicio, paginaFim, duracaoSegundos, data_hora) VALUES (8, 0, 20, 600, 1), (9, 20, 40, 900, 2)")
            execSQL("INSERT INTO notas (livroId, tipo, conteudo, data_hora) VALUES (8, 'NOTA', 'nota do duplicado', 1)")
            execSQL("INSERT INTO metas (livroId, tempoPrevistoMinutos, data_alvo) VALUES (8, 30, NULL)")
        }

        val db = abrirComRoom()
        val livros = withContext(Dispatchers.IO) { db.livroDao().listarTodos() }

        // Cada livro aparece uma única vez (uma seção só no Dashboard).
        assertEquals(listOf(9L, 21L, 30L), livros.map { it.id })

        val harryPotter = livros.first { it.id == 9L }
        assertEquals(StatusLeitura.LENDO, harryPotter.status) // status mais avançado vence
        assertEquals(40, harryPotter.paginaAtual)
        assertEquals(StatusLeitura.LIDO, livros.first { it.id == 21L }.status)
        assertEquals(StatusLeitura.LENDO, livros.first { it.id == 30L }.status) // "lendo" normalizado

        // Sessões, notas e meta do duplicado foram transferidas para o livro mantido.
        assertEquals(2, db.sessaoDao().observarSessoesPorLivro(9L).first().size)
        assertEquals(1, db.notaDao().observarNotasPorLivro(9L).first().size)
        assertEquals(30, db.metaDao().buscarPorLivro(9L)?.tempoPrevistoMinutos)

        // Índice único criado: o mesmo googleBooksId não entra de novo.
        try {
            withContext(Dispatchers.IO) {
                db.livroDao().inserir(harryPotter.copy(id = 0L, status = StatusLeitura.QUERO_LER))
            }
            fail("deveria barrar googleBooksId repetido")
        } catch (_: SQLiteConstraintException) {
        }
    }

    @Test
    fun cadastrarLivroQueJaExisteNaoCriaOutroRegistro() = runBlocking {
        criarBancoV1 {
            livro(9, "Harry Potter and the Order of the Phoenix", "J. K. Rowling", "LENDO", 40, "KZvHBAAAQBAJ")
            livro(20, "O Alquimista", "Paulo Coelho", "QUERO_LER", 0, null)
        }
        val db = abrirComRoom()
        val repositorio = LivroRepositoryImpl(db.livroDao(), GoogleBooksRepository(RetrofitConfig.googleBooksApi))

        // Mesmo livro vindo da busca (mesmo googleBooksId), com dois toques seguidos em "Salvar".
        val viaApi = novoLivroViewModel(repositorio, "Harry Potter and the Order of the Phoenix", "J. K. Rowling", "KZvHBAAAQBAJ")
        withContext(Dispatchers.Main) {
            viaApi.onSalvarClick()
            viaApi.onSalvarClick()
        }
        assertEquals(9L, idSalvo(viaApi))

        // Mesmo livro cadastrado à mão, com maiúsculas/acentos/espaços diferentes.
        val manual = novoLivroViewModel(repositorio, "  o alquimista", "PAULO COELHO", null)
        withContext(Dispatchers.Main) { manual.onSalvarClick() }
        assertEquals(20L, idSalvo(manual))

        val livros = withContext(Dispatchers.IO) { db.livroDao().listarTodos() }
        assertEquals(listOf(9L, 20L), livros.map { it.id })
        assertEquals(StatusLeitura.LENDO, livros.first { it.id == 9L }.status) // status não foi sobrescrito
        assertNull(livros.first { it.id == 20L }.googleBooksId)
    }

    // Bug: o card "Em andamento" (livros.paginaAtual) não acompanhava as sessões de leitura.
    @Test
    fun migracaoSincronizaPaginaAtualComSessoesJaRegistradas() = runBlocking {
        criarBancoV1 {
            // Cenário do aparelho: 1984 parado em 120 com sessão até 328; Entardecer em 0 com sessão até 2.
            livro(3, "1984", "George Orwell", "LENDO", 120, null)
            execSQL("UPDATE livros SET totalPaginas = 328 WHERE id = 3") // como no aparelho
            livro(10, "Entardecer", "Andréia", "LENDO", 0, null)
            livro(4, "Sapiens", "Yuval Noah Harari", "LENDO", 200, null) // sem sessões: não muda
            execSQL("INSERT INTO sessoes_leitura (livroId, paginaInicio, paginaFim, duracaoSegundos, data_hora) VALUES (3, 120, 125, 94, 1), (3, 125, 328, 0, 2), (10, 0, 2, 62, 3)")
        }
        val livros = withContext(Dispatchers.IO) { abrirComRoom().livroDao().listarTodos() }.associateBy { it.id }
        assertEquals(328, livros.getValue(3L).paginaAtual)
        assertEquals(2, livros.getValue(10L).paginaAtual)
        assertEquals(200, livros.getValue(4L).paginaAtual)
        // v3 -> v4: 1984 chegou à última página e vai para "Lido"; os outros seguem "Lendo".
        assertEquals(StatusLeitura.LIDO, livros.getValue(3L).status)
        assertEquals(StatusLeitura.LENDO, livros.getValue(10L).status)
        assertEquals(StatusLeitura.LENDO, livros.getValue(4L).status)
    }

    @Test
    fun registrarSessaoAtualizaPaginaExibidaNoDashboard() = runBlocking {
        criarBancoV1 { livro(10, "Entardecer", "Andréia", "LENDO", 0, null) }
        val db = abrirComRoom()
        val livros = LivroRepositoryImpl(db.livroDao(), GoogleBooksRepository(RetrofitConfig.googleBooksApi))
        val leitura = LeituraRepositoryImpl(db.sessaoDao(), livros)

        leitura.registrarSessao(SessaoLeitura(livroId = 10L, paginaInicio = 0, paginaFim = 35, duracaoSegundos = 600, dataHora = 1L))

        // Mesma consulta reativa que alimenta o Dashboard: já emite a página nova.
        val noDashboard = livros.observarLivrosFiltrados(null, null).first().first { it.id == 10L }
        assertEquals(35, noDashboard.paginaAtual)

        // Uma sessão com página menor (ex.: releitura) não faz o progresso voltar.
        leitura.registrarSessao(SessaoLeitura(livroId = 10L, paginaInicio = 5, paginaFim = 10, duracaoSegundos = 60, dataHora = 2L))
        assertEquals(35, livros.buscar(10L)?.paginaAtual)
        assertEquals(DominioStatus.LENDO, livros.buscar(10L)?.status)
    }

    @Test
    fun sessaoQueChegaNaUltimaPaginaMarcaLivroComoLido() = runBlocking {
        criarBancoV1 { livro(3, "1984", "George Orwell", "LENDO", 125, null) }
        val db = abrirComRoom()
        val livros = LivroRepositoryImpl(db.livroDao(), GoogleBooksRepository(RetrofitConfig.googleBooksApi))
        val leitura = LeituraRepositoryImpl(db.sessaoDao(), livros)

        // totalPaginas do helper é 500: terminar na página 500 conclui o livro.
        leitura.registrarSessao(SessaoLeitura(livroId = 3L, paginaInicio = 125, paginaFim = 500, duracaoSegundos = 600, dataHora = 1L))

        val livro = livros.observarLivrosFiltrados(null, null).first().first { it.id == 3L }
        assertEquals(DominioStatus.LIDO, livro.status) // sai de "Em andamento" e vai para "Lido"
        assertEquals(500, livro.paginaAtual)
    }

    private fun novoLivroViewModel(
        repositorio: LivroRepositoryImpl,
        titulo: String,
        autor: String,
        googleBooksId: String?
    ) = NovoLivroViewModel(
        livroRepository = repositorio,
        context = context,
        savedStateHandle = SavedStateHandle(
            mapOf("titulo" to titulo, "autor" to autor, "totalPaginas" to 300, "googleBooksId" to googleBooksId)
        )
    )

    private suspend fun idSalvo(viewModel: NovoLivroViewModel): Long? =
        withTimeout(5_000L) { viewModel.uiState.first { it.livroSalvoId != null }.livroSalvoId }
}
