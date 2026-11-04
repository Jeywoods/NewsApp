package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.repository.NewsRepository

class GetNewsDetailsUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(newsId: Int) =
        repository.getNewsDetails(newsId)
}