package com.example.newsapp.domain.repository

import com.example.newsapp.domain.model.Category
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.NewsDetails

interface NewsRepository {

    suspend fun getCategories(): List<Category>

    suspend fun getNews(
        categoryId: Int,
        page: Int
    ): List<News>

    suspend fun getNewsDetails(newsId: Int): NewsDetails
}