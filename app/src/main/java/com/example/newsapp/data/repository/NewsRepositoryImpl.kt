package com.example.newsapp.data.repository

import com.example.newsapp.data.mapper.toDetailsDomain
import com.example.newsapp.data.mapper.toDomain
import com.example.newsapp.data.remote.ApiResponseHandler
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.domain.model.Category
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.NewsDetails
import com.example.newsapp.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val api: NewsApiService
) : NewsRepository {

    override suspend fun getCategories(): List<Category> {
        val response = api.getCategories()

        return ApiResponseHandler
            .handleCategories(response)
            .map { it.toDomain() }
    }

    override suspend fun getNews(
        categoryId: Int,
        page: Int
    ): List<News> {
        val response = api.getNews(
            categoryId = categoryId,
            page = page
        )

        return ApiResponseHandler
            .handleNews(response)
            .map { it.toDomain() }
    }

    override suspend fun getNewsDetails(newsId: Int): NewsDetails {
        val response = api.getNewsDetails(newsId)

        return ApiResponseHandler
            .handleNewsDetails(response)
            .toDetailsDomain()
    }
}