package com.example.app_leituras.di

import com.example.app_leituras.data.repository.LeituraRepositoryImpl
import com.example.app_leituras.data.repository.LivroRepositoryImpl
import com.example.app_leituras.data.repository.MetaRepositoryImpl
import com.example.app_leituras.data.repository.NotaRepositoryImpl
import com.example.app_leituras.domain.repository.LeituraRepository
import com.example.app_leituras.domain.repository.LivroRepository
import com.example.app_leituras.domain.repository.MetaRepository
import com.example.app_leituras.domain.repository.NotaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// abstract class (não interface) porque @Binds precisa de um método abstrato dentro de um
// @Module — vincula cada interface de domínio à sua implementação real (Room), sem instanciar
// nada manualmente: o Hilt usa o @Inject constructor de cada *RepositoryImpl pra isso.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLivroRepository(impl: LivroRepositoryImpl): LivroRepository

    @Binds
    @Singleton
    abstract fun bindLeituraRepository(impl: LeituraRepositoryImpl): LeituraRepository

    @Binds
    @Singleton
    abstract fun bindMetaRepository(impl: MetaRepositoryImpl): MetaRepository

    @Binds
    @Singleton
    abstract fun bindNotaRepository(impl: NotaRepositoryImpl): NotaRepository
}
