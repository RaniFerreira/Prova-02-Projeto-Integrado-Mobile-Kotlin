package com.example.app_leituras.di

import android.content.Context
import com.example.app_leituras.data.local.AppDatabase
import com.example.app_leituras.data.local.dao.LivroDao
import com.example.app_leituras.data.local.dao.MetaDao
import com.example.app_leituras.data.local.dao.NotaDao
import com.example.app_leituras.data.local.dao.SessaoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Reaproveita o singleton/seed que já existia em AppDatabase.getInstance() — só troca quem
    // chama isso de "código manual no MainActivity" para "o Hilt, uma vez só, no SingletonComponent".
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    fun provideLivroDao(database: AppDatabase): LivroDao = database.livroDao()

    @Provides
    fun provideSessaoDao(database: AppDatabase): SessaoDao = database.sessaoDao()

    @Provides
    fun provideMetaDao(database: AppDatabase): MetaDao = database.metaDao()

    @Provides
    fun provideNotaDao(database: AppDatabase): NotaDao = database.notaDao()
}
