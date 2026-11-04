package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.repository.NewsRepository

class GetNewsUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(
        categoryId: Int,
        page: Int
    ) = repository.getNews(categoryId, page)
}