package com.example.app_leituras.di

import com.example.app_leituras.data.remote.GoogleBooksApi
import com.example.app_leituras.data.remote.RetrofitConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGoogleBooksApi(): GoogleBooksApi = RetrofitConfig.googleBooksApi
}
