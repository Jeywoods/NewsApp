package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.repository.NewsRepository

class GetCategoriesUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke() = repository.getCategories()
}