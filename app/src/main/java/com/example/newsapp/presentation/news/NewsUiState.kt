package com.example.newsapp.presentation.news

import com.example.newsapp.domain.model.News

data class NewsUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val news: List<News> = emptyList(),
    val error: String? = null,
    val page: Int = 0,
    val hasMorePages: Boolean = true
)