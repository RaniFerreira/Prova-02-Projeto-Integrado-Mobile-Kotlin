package com.example.app_leituras.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.app_leituras.data.local.dao.LivroDao
import com.example.app_leituras.data.local.dao.MetaDao
import com.example.app_leituras.data.local.dao.NotaDao
import com.example.app_leituras.data.local.dao.SessaoDao
import com.example.app_leituras.data.local.entity.LivroEntity
import com.example.app_leituras.data.local.entity.MetaEntity
import com.example.app_leituras.data.local.entity.NotaEntity
import com.example.app_leituras.data.local.entity.SessaoLeituraEntity

@Database(
    entities = [
        LivroEntity::class,
        SessaoLeituraEntity::class,
        MetaEntity::class,
        NotaEntity::class
    ],
    version = 1,
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

        @Volatile
        private var instancia: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NOME_BANCO
                ).build().also { instancia = it }
            }
    }
}
