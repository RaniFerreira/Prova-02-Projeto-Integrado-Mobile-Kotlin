package com.example.app_leituras.data.remote

import com.example.app_leituras.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Configuração única do Retrofit para a API do Google Books. */
object RetrofitConfig {

    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    // Chamadas sem "key" caem no projeto anônimo do Google, cuja cota é zero (429 em toda
    // requisição). A chave vem do local.properties via BuildConfig — ver app/build.gradle.kts.
    private val apiKeyInterceptor = Interceptor { chain ->
        val requisicao = chain.request()
        if (BuildConfig.GOOGLE_BOOKS_API_KEY.isBlank()) return@Interceptor chain.proceed(requisicao)

        val urlComChave = requisicao.url.newBuilder()
            .addQueryParameter("key", BuildConfig.GOOGLE_BOOKS_API_KEY)
            .build()
        chain.proceed(requisicao.newBuilder().url(urlComChave).build())
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(logging)
            .build()
    }

    val googleBooksApi: GoogleBooksApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApi::class.java)
    }
}
