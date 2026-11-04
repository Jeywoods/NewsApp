package com.example.newsapp.data.remote

import com.example.newsapp.data.remote.dto.CategoriesResponseDto
import com.example.newsapp.data.remote.dto.NewsDetailsResponseDto
import com.example.newsapp.data.remote.dto.NewsListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {

    @GET("v1/news/categories")
    suspend fun getCategories(): CategoriesResponseDto

    @GET("v1/news/categories/{id}/news")
    suspend fun getNews(
        @Path("id") categoryId: Int,
        @Query("page") page: Int
    ): NewsListResponseDto

    @GET("v1/news/details")
    suspend fun getNewsDetails(
        @Query("id") newsId: Int
    ): NewsDetailsResponseDto
}