package com.example.app_leituras.data.remote

import com.example.app_leituras.data.remote.dto.GoogleBooksResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("volumes")
    suspend fun buscarLivros(@Query("q") query: String): GoogleBooksResponseDto
}
