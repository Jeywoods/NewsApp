package com.example.newsapp.data.remote.dto

data class CategoriesResponseDto(
    val code: Int,
    val message: String? = null,
    val list: List<CategoryDto>? = null
)

data class NewsListResponseDto(
    val code: Int,
    val message: String? = null,
    val list: List<NewsDto>? = null
)

data class NewsDetailsResponseDto(
    val code: Int,
    val message: String? = null,
    val news: NewsDto? = null
)