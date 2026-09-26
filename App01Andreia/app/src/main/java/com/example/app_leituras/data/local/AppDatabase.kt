package com.example.app_leituras.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_leituras.data.local.dao.LivroDao
import com.example.app_leituras.data.local.dao.MetaDao
import com.example.app_leituras.data.local.dao.NotaDao
import com.example.app_leituras.data.local.dao.SessaoDao
import com.example.app_leituras.data.local.entity.LivroEntity
import com.example.app_leituras.data.local.entity.MetaEntity
import com.example.app_leituras.data.local.entity.NotaEntity
import com.example.app_leituras.data.local.entity.SessaoLeituraEntity
import com.example.app_leituras.data.local.entity.StatusLeitura
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        LivroEntity::class,
        SessaoLeituraEntity::class,
        MetaEntity::class,
        NotaEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun livroDao(): LivroDao
    abstract fun sessaoDao(): SessaoDao
    abstract fun metaDao(): MetaDao
    abstract fun notaDao(): NotaDao

    companion object {
        private const val NOME_BANCO = "app_leituras.db"
        private const val DIA_MS = 86_400_000L

        @Volatile
        private var instancia: AppDatabase? = null

        // Escopo de app (não amarrado a nenhuma Activity/ViewModel) só para o seed inicial,
        // que roda uma única vez, na criação do arquivo do banco.
        private val escopoSeed = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NOME_BANCO
                )
                    // v2: índice único em googleBooksId + limpeza de duplicados/status antigos.
                    // v3: página atual dos livros sincronizada com as sessões já registradas.
                    // v4: livros que já chegaram à última página marcados como LIDO.
                    .addMigrations(MIGRACAO_1_2, MIGRACAO_2_3, MIGRACAO_3_4)
                    .addCallback(object : RoomDatabase.Callback() {
                        // Só dispara na primeira vez que o arquivo do banco é criado (não a cada
                        // abertura do app), então é seguro popular aqui sem duplicar dados depois.
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            escopoSeed.launch {
                                // getInstance(context) de novo em vez de usar "instancia" direto:
                                // esse callback é assíncrono e pode disparar antes do ".also" abaixo
                                // terminar de atribuir a variável, então repetimos a chamada segura.
                                seedLivrosIniciais(getInstance(context.applicationContext).livroDao())
                            }
                        }
                    })
                    .build()
                    .also { instancia = it }
            }

        // Popula o banco com os mesmos 6 livros de exemplo que existiam no FakeLivroRepository,
        // só para dar dados reais pra testar a UI assim que o Room entra em uso.
        private suspend fun seedLivrosIniciais(livroDao: LivroDao) {
            val agora = System.currentTimeMillis()
            livroDao.inserir(
                LivroEntity(
                    titulo = "Duna",
                    autor = "Frank Herbert",
                    totalPaginas = 688,
                    genero = "Ficção Científica",
                    capaUrl = null,
                    status = StatusLeitura.QUERO_LER,
                    paginaAtual = 0,
                    googleBooksId = null,
                    dataCriacao = agora - DIA_MS * 1
                )
            )
            livroDao.inserir(
                LivroEntity(
                    titulo = "O Nome do Vento",
                    autor = "Patrick Rothfuss",
                    totalPaginas = 662,
                    genero = "Fantasia",
                    capaUrl = null,
                    status = StatusLeitura.QUERO_LER,
                    paginaAtual = 0,
                    googleBooksId = null,
                    dataCriacao = agora - DIA_MS * 2
                )
            )
            livroDao.inserir(
                LivroEntity(
                    titulo = "1984",
                    autor = "George Orwell",
                    totalPaginas = 328,
                    genero = "Ficção",
                    capaUrl = null,
                    status = StatusLeitura.LENDO,
                    paginaAtual = 120,
                    googleBooksId = null,
                    dataCriacao = agora - DIA_MS * 3
                )
            )
            livroDao.inserir(
                LivroEntity(
                    titulo = "Sapiens",
                    autor = "Yuval Noah Harari",
                    totalPaginas = 464,
                    genero = "Não-ficção",
                    capaUrl = null,
                    status = StatusLeitura.LENDO,
                    paginaAtual = 200,
                    googleBooksId = null,
                    dataCriacao = agora - DIA_MS * 4
                )
            )
            livroDao.inserir(
                LivroEntity(
                    titulo = "O Hobbit",
                    autor = "J.R.R. Tolkien",
                    totalPaginas = 310,
                    genero = "Fantasia",
                    capaUrl = null,
                    status = StatusLeitura.LIDO,
                    paginaAtual = 310,
                    googleBooksId = null,
                    dataCriacao = agora - DIA_MS * 5
                )
            )
            livroDao.inserir(
                LivroEntity(
                    titulo = "A Revolução dos Bichos",
                    autor = "George Orwell",
                    totalPaginas = 152,
                    genero = "Ficção",
                    capaUrl = null,
                    status = StatusLeitura.LIDO,
                    paginaAtual = 152,
                    googleBooksId = null,
                    dataCriacao = agora - DIA_MS * 6
                )
            )
        }
    }
}
